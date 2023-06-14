package com.example.sudokugame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory(private val parameter: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SudokuViewModel::class.java)) {
            return SudokuViewModel(parameter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}