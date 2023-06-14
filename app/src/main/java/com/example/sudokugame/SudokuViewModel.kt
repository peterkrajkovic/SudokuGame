package com.example.sudokugame


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class SudokuViewModel(mode: Int) : ViewModel() {

    var selectedCell = MutableLiveData<Pair<Int, Int>>()
    var boardNumbers = MutableLiveData<List<Pair<Int, Int>?>>()
    var minutes = MutableLiveData<Int>()
    var seconds = MutableLiveData<Int>()
    private var timer: Timer? = null
    private var elapsedTimeInSeconds = 0
    private var solution = ArrayList<Int?>()
    var mistakes = 0
    private var finished = false

    init {
        selectedCell.postValue(Pair(10, 10))
        createSolution()
        when (mode) {
            1 -> deleteFromSolution(1)
            2 -> deleteFromSolution(45)
            else-> deleteFromSolution(50)
        }
        val array = ArrayList<Pair<Int, Int>?>()

        for (i in 0 until solution.size) {
            if (solution[i] == null) {
                array.add(null)
            } else {
                array.add(Pair(solution[i]!!,0))
            }
        }
        boardNumbers.postValue(array.toList())
        startTimer()
    }


    private fun createSolution() {
        val numbers = (1..9).shuffled(Random).toList()
        solution.addAll(numbers)

        var shiftedNumbers = numbers

        repeat(2) {
            shiftedNumbers = shiftedNumbers.subList(3, shiftedNumbers.size) + shiftedNumbers.subList(0, 3)
            solution.addAll(shiftedNumbers)
        }
        repeat(2) {
            shiftedNumbers = shiftedNumbers.subList(1, shiftedNumbers.size) + shiftedNumbers.subList(0, 1)
            solution.addAll(shiftedNumbers)

            repeat(2) {
                shiftedNumbers = shiftedNumbers.subList(3, shiftedNumbers.size) + shiftedNumbers.subList(0, 3)
                solution.addAll(shiftedNumbers)
            }
        }
    }
    private fun deleteFromSolution(number: Int) {
        var reps  = number
        while(reps != 0) {
            val randomNumber = Random.nextInt(0,81)
            if (solution[randomNumber] != null) {
                solution[randomNumber]= null
                reps--
            }
        }
    }

    fun updateSelectedCell(row: Int, column: Int) {
        if (finished) return
        if (boardNumbers.value!!.toMutableList()[row * 9 + column] == null || boardNumbers.value!!.toMutableList()[row * 9 + column]!!.second != 0) {
            selectedCell.postValue(Pair(row, column))
        }
    }

    fun numberInput(number: Int) {
        if (selectedCell.value!!.first == 10 || finished) return
        val currentList = boardNumbers.value?.toMutableList()
        currentList?.set(selectedCell.value!!.first * 9 + selectedCell.value!!.second, Pair(number,1))
        selectedCell.value = Pair(10,10)
        boardNumbers.value = currentList
    }

    fun acceptNumber() {
        if (selectedCell.value!!.first == 10 || finished) return
        val currentList = boardNumbers.value?.toMutableList()
        if (currentList!![selectedCell.value!!.first * 9 + selectedCell.value!!.second] == null) return

        currentList[selectedCell.value!!.first * 9 + selectedCell.value!!.second] =
            Pair(currentList[selectedCell.value!!.first * 9 + selectedCell.value!!.second]!!.first,2)

        selectedCell.value = Pair(10,10)
        boardNumbers.value = currentList
    }
    fun removeNumber() {
        if (selectedCell.value!!.first == 10 || finished) return
        val currentList = boardNumbers.value?.toMutableList()
        if (currentList!![selectedCell.value!!.first * 9 + selectedCell.value!!.second] == null) return

        currentList[selectedCell.value!!.first * 9 + selectedCell.value!!.second] = null

        selectedCell.value = Pair(10,10)
        boardNumbers.value = currentList
    }

    fun finish() : Boolean {
        if (finished) return false

        boardNumbers.value?.toMutableList()?.forEach { pair ->
            if (pair == null) return false
        }
        stopTimer()
        val board = boardNumbers.value
        val currentList = boardNumbers.value?.toMutableList()
        for (row in 0 until 9) {
            for (column in 0 until 9) {
                val rowValues = (0 until 9).map { board?.get(row * 9 + it)?.first }
                val colValues = (0 until 9).map { board?.get(it * 9 + column)?.first }

                val startRow = row / 3 * 3
                val startCol = column / 3 * 3
                val gridValues = mutableListOf<Int?>()
                for (rowOffset in 0 until 3) {
                    for (colOffset in 0 until 3) {
                        gridValues.add(board?.get((startRow + rowOffset) * 9 + (startCol + colOffset))?.first)
                    }
                }
                val cellValue = board?.get(row * 9 + column)?.first
                val isRowValid = rowValues.count { it == cellValue } == 1
                val isColValid = colValues.count { it == cellValue } == 1
                val isGridValid = gridValues.count { it == cellValue } == 1

                if (isRowValid && isColValid && isGridValid) {
                    currentList?.set(row * 9 + column, Pair(currentList[row * 9 + column]!!.first,3))
                } else {
                    currentList?.set(row * 9 + column, Pair(currentList[row * 9 + column]!!.first,4))
                    mistakes++
                }
            }
        }
        boardNumbers.value = currentList
        finished = true
        mistakes /= 3
        return true
    }


    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateTime()
            }
        }, 0L, 1000L)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun updateTime() {
        elapsedTimeInSeconds++
        val minutes = elapsedTimeInSeconds / 60
        val seconds = elapsedTimeInSeconds % 60
        this.minutes.postValue(minutes)
        this.seconds.postValue(seconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}