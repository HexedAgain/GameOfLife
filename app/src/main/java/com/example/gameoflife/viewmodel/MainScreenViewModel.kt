package com.example.gameoflife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameoflife.model.Cells
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val defaultDispatcher: CoroutineDispatcher,
    stepDuration: Long = 1_000
): ViewModel() {
    private val _cells = MutableStateFlow(Cells.makeGrid(0, 0))
    val cells = _cells.asStateFlow()

    private val _stepDurationMs = MutableStateFlow(stepDuration)
    val stepDurationMs = _stepDurationMs.asStateFlow()

    private val _stepsRemaining = MutableStateFlow(0)
    val stepsRemaining = _stepsRemaining.asStateFlow()

    fun initialiseCells(rows: Int, columns: Int) {
        _cells.value = Cells.makeGrid(rows, columns)
    }

    private var gameJob: Job? = null

    fun updateCell(row: Int, column: Int) {
        _cells.value = cells.value.toggleLiveness(row, column)
    }

    // stepsRemaining should decrement every stepDurationMs milliseconds
    fun startGameOfLife(noOfGenerations: Int) {
        _stepsRemaining.value = noOfGenerations
        gameJob = viewModelScope.launch(defaultDispatcher) {
            while (stepsRemaining.value > 0) {
                delay(stepDurationMs.value)
                _cells.value = _cells.value.getNextGeneration()
                _stepsRemaining.value -= 1
            }
            this.cancel()
        }
    }

    fun pauseGameOfLife() {
        gameJob?.cancel()
    }

    // a new coroutine scope should be launched to decrement the counter
    fun continueGameOfLife() {
       startGameOfLife(stepsRemaining.value)
    }

    // stepsRemaining should be set to 0
    fun stopGameOfLife() {
        _stepsRemaining.value = 0
        gameJob?.cancel()
    }
}