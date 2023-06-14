package com.example.sudokugame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.*
import android.widget.Button
import android.widget.GridLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class TwoPlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: SudokuViewModel
    private var started = false
    private var rotation = false
    private var secondsOne = 0
    private var secondsTwo = 0
    private var minutesOne = 3
    private var minutesTwo = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_two_players)

        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.onTouchListener = object :  SudokuBoardView.OnTouchListener {
            override fun onTouch(row: Int, column: Int) {
                onCellTouched(row, column)
            }
        }
        val viewModelFactory = ViewModelFactory(2)
        viewModel = ViewModelProvider(this, viewModelFactory)[SudokuViewModel::class.java]
        viewModel.selectedCell.observe(this) { updateSelectedCell(it) }
        viewModel.boardNumbers.observe(this) { updateBoardNumbers(it) }
        viewModel.seconds.observe(this) { updateTime(it) }

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
            button.setOnClickListener { viewModel.numberInput(index + 1) }
        }
    }
    private fun updateSelectedCell(cell: Pair<Int, Int>?) = cell?.let {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateSelectedCell(cell.first, cell.second)
    }

    private fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateBoardNumbers(boardNumbers)
    }

    private fun updateTime(seconds: Int) {
        var text = viewModel.minutes.value.toString() + ":"
        if (viewModel.minutes.value!! < 10) {
            text = "0" + text
        }
        if (seconds < 10) {
            text += "0" + seconds
        } else {
            text += seconds
        }
        val timeTextView = findViewById<TextView>(R.id.timeTwo)
        timeTextView.text = text
        rotateScreen()
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

        }
        rotation = !rotation

    }

    private fun getTimeString(player : Int): String {
        var text = ""
        return text
    }

    fun onCellTouched(row: Int, column: Int) {
        viewModel.updateSelectedCell(row, column)
    }

    private fun end() {
        val handler = Handler()

        val startNewActivityRunnable = Runnable {
            val intent = Intent(this, GameFinishedActivity::class.java)
            intent.putExtra("mistakesCount", viewModel.mistakes)
            intent.putExtra("minutes", viewModel.minutes.value)
            intent.putExtra("seconds", viewModel.seconds.value)
            startActivity(intent)
            this.finish()
        }
        handler.postDelayed(startNewActivityRunnable, 2000)
    }
}