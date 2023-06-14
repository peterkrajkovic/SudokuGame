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
    private var solution = ArrayList<Int>()
    var mistakes = 0
    private var finished = false

    init {
        selectedCell.postValue(Pair(10, 10))
        createSolution()
        when (mode) {
            1 -> makeBoard(30)
            2 -> makeBoard(40)
            else-> makeBoard(45)
        }
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
    private fun makeBoard(number: Int) {
        var reps  = number
        val array = ArrayList<Pair<Int, Int>?>()
        for (i in 0 until solution.size) {
            array.add(Pair(solution[i],0))
        }

        while(reps != 0) {
            val randomNumber = Random.nextInt(0,81)
            if (array[randomNumber] != null) {
                array[randomNumber]= null
                reps--
            }
        }
        boardNumbers.postValue(array.toList())
    }

    fun updateSelectedCell(row: Int, column: Int) {
        if (finished) return
        if (boardNumbers.value!!.toMutableList()[row * 9 + column] == null ||
            (boardNumbers.value!!.toMutableList()[row * 9 + column]!!.second != 0 &&
            boardNumbers.value!!.toMutableList()[row * 9 + column]!!.second != 3))
        {
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

    fun numberInputWithCheck(number: Int) : Int {
        if (selectedCell.value!!.first == 10 || finished) return 0
        val row = selectedCell.value!!.first
        val column = selectedCell.value!!.second
        numberInput(number)
        if (checkCell(row, column)) {
            return 1
        }
        return -1
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
        for (row in 0 until 9) {
            for (column in 0 until 9) {
                if (!checkCell(row, column)) {
                    mistakes++
                }
            }
        }
        finished = true
        return true
    }

    private fun checkCell(row: Int, column: Int) : Boolean {
        val currentList = boardNumbers.value?.toMutableList()

        return if (currentList?.get(row * 9 + column)!!.first  == solution[row * 9 + column]) {
            currentList?.set(row * 9 + column, Pair(currentList[row * 9 + column]!!.first,3))
            boardNumbers.value = currentList
            true
        } else {
            currentList?.set(row * 9 + column, Pair(currentList[row * 9 + column]!!.first,4))
            boardNumbers.value = currentList
            false
        }

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