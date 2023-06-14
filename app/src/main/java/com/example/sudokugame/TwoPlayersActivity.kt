package com.example.sudokugame

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager.*
import android.widget.Button
import android.widget.GridLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider


class TwoPlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: SudokuViewModel
    private var started = false
    private var rotation = false
    private var finished = false
    private var secondsOne = 0
    private var secondsTwo = 0
    private var minutesOne = 3
    private var minutesTwo = 3
    private var mistakesOne = 0
    private var mistakesTwo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions

        setContentView(R.layout.activity_two_players)

        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.onTouchListener = object :  SudokuBoardView.OnTouchListener {
            override fun onTouch(row: Int, column: Int) {
                onCellTouched(row, column)
            }
        }
        val viewModelFactory = ViewModelFactory(1)
        viewModel = ViewModelProvider(this, viewModelFactory)[SudokuViewModel::class.java]
        viewModel.selectedCell.observe(this) { updateSelectedCell(it) }
        viewModel.boardNumbers.observe(this) { updateBoardNumbers(it) }
        viewModel.seconds.observe(this) { updateTime() }

        val buttonList = listOf(
            findViewById(R.id.oneButton),
            findViewById(R.id.twoButton),
            findViewById(R.id.threeButton),
            findViewById(R.id.fourButton),
            findViewById(R.id.fiveButton),
            findViewById(R.id.sixButton),
            findViewById(R.id.sevenButton),
            findViewById(R.id.eightButton),
            findViewById<Button>(R.id.nineButton)
        )
        buttonList.forEachIndexed { index, button ->
            button.setOnClickListener { numberClicked(index + 1) }
        }
    }

    private fun numberClicked(number : Int) {
        when (viewModel.numberInputWithCheck(number)) {
            0 -> return
            -1 -> {
                if (rotation) {
                    mistakesTwo++
                    val mistakesView = findViewById<TextView>(R.id.mistakesTwo)
                    mistakesView.text = mistakesTwo.toString()
                } else {
                    mistakesOne++
                    val mistakesView = findViewById<TextView>(R.id.mistakesOne)
                    mistakesView.text = mistakesOne.toString()
                }
            }
        }
        var finish = true
        viewModel.boardNumbers.value?.toMutableList()?.forEach { pair ->
            if ((pair == null) || (pair.second == 4)) finish = false
        }
        if (finish) {
            if (mistakesTwo < mistakesOne) {
                end("Sudoku is completed.\nPlayer 2 won.")
            } else if (mistakesOne < mistakesTwo) {
                end("Sudoku is completed.\nPlayer 1 won.")
            } else {
                if (minutesOne * 60 + secondsOne < minutesTwo * 60 + minutesTwo) {
                    end("Sudoku is completed.\nPlayer 2 won.")
                } else if (minutesOne * 60 + secondsOne > minutesTwo * 60 + minutesTwo) {
                    end("Sudoku is completed.\nPlayer 1 won.")
                } else {
                    end("Sudoku is completed.\nIt's a draw.")
                }
            }
            finished = true
            return
        }

        rotateScreen()
    }

    private fun updateSelectedCell(cell: Pair<Int, Int>?) {
        if(finished) return
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        if (cell != null) {
            boardView.updateSelectedCell(cell.first, cell.second)
        }
    }

    private fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateBoardNumbers(boardNumbers)
    }


    private fun rotateScreen() {
        if (!rotation) {
            val buttonsLayout = findViewById<GridLayout>(R.id.buttonsLayout)
            val layoutParam = buttonsLayout.layoutParams as RelativeLayout.LayoutParams
            layoutParam.addRule(RelativeLayout.BELOW, R.id.timeTwo)
            buttonsLayout.layoutParams = layoutParam
            buttonsLayout.rotation = 180f

            val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
            val layoutParams = boardView.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.BELOW, R.id.buttonsLayout)
            boardView.layoutParams = layoutParams
            boardView.rotation = 180f
        } else {

            val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
            val layoutParams = boardView.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.BELOW, R.id.timeTwo)
            boardView.layoutParams = layoutParams
            boardView.rotation = 0f

            val buttonsLayout = findViewById<GridLayout>(R.id.buttonsLayout)
            val layoutParam = buttonsLayout.layoutParams as RelativeLayout.LayoutParams
            layoutParam.addRule(RelativeLayout.BELOW, R.id.sudokuBoardView)
            buttonsLayout.layoutParams = layoutParam
            buttonsLayout.rotation = 0f
        }

        rotation = !rotation

    }

    private fun updateTime() {
        if (finished) return
        var text: String
        if (!rotation) {
            secondsOne--
            if (secondsOne == -1) {
                secondsOne = 59
                minutesOne--
                if (minutesOne == -1) {
                    end("Player 1 ran out of time.\nPlayer 2 won.")
                    finished = true
                    return
                }
            }
            text = "$minutesOne:"
            if (secondsOne < 10) {
                text += "0$secondsOne"
            } else {
                text += secondsOne
            }
            val timeTextView = findViewById<TextView>(R.id.timeOne)
            timeTextView.text = text
            if (minutesOne < 1 && secondsOne < 30) timeTextView.setTextColor(Color.RED)
        } else {
            secondsTwo--
            if (secondsTwo == -1) {
                secondsTwo = 59
                minutesTwo--
                if (minutesTwo == -1){
                    end("Player 2 ran out of time.\nPlayer 1 won.")
                    finished = true
                    return
                }
            }
            text = "$minutesTwo:"
            if (secondsTwo < 10) {
                text += "0$secondsTwo"
            } else {
                text += secondsTwo
            }
            val timeTextView = findViewById<TextView>(R.id.timeTwo)
            timeTextView.text = text
            if (minutesTwo < 1 && secondsTwo < 30) timeTextView.setTextColor(Color.RED)
        }
    }

    fun onCellTouched(row: Int, column: Int) {
        viewModel.updateSelectedCell(row, column)
    }

    private fun end(type: String) {
        val handler = Handler()

        val startNewActivityRunnable = Runnable {
            val intent = Intent(this, GameFinishedActivity::class.java)
            intent.putExtra("activity", "twoPlayers")
            intent.putExtra("type", type)
            intent.putExtra("mistakes1", mistakesOne)
            intent.putExtra("mistakes2", mistakesTwo)
            startActivity(intent)
            this.finish()
        }
        handler.postDelayed(startNewActivityRunnable, 2000)
    }
}