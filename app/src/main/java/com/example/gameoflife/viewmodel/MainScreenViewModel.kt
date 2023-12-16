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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val defaultDispatcher: CoroutineDispatcher,
    stepDuration: Long = 1_000
): ViewModel() {
    private val _cells = MutableStateFlow(Cells.makeGrid(0, 0))
    val cells = _cells.asStateFlow()

    private val _stepDurationMs = MutableStateFlow(stepDuration)
    val stepDurationMs = _stepDurationMs.asStateFlow()

    private val _stepsRemaining = MutableStateFlow(10_000)
    val stepsRemaining = _stepsRemaining.asStateFlow()

    private val _rows = MutableStateFlow("")
    val rows = _rows.asStateFlow()
    fun updateRows(newRows: String) {
        if (newRows.length <= 2) {
            _rows.value = newRows
        }
    }

    private val _columns = MutableStateFlow("")
    val columns = _columns.asStateFlow()
    fun updateColumns(newColumns: String) {
        if (newColumns.length <= 2) {
            _columns.value = newColumns
        }
    }

    fun initialiseCells(rows: Int, columns: Int) {
        _cells.value = Cells.makeGrid(rows, columns)
    }

    private var gameJob: Job? = null

    fun updateCell(row: Int, column: Int) {
        _cells.update { it.toggleLiveness(row, column) }
    }

    fun startGameOfLife(noOfGenerations: Int) {
        _stepsRemaining.value = noOfGenerations
        gameJob = viewModelScope.launch(defaultDispatcher) {
            while (stepsRemaining.value > 0 && cells.value.get().flatten().any { it }) {
                delay(stepDurationMs.value)
                _cells.value = _cells.value.getNextGeneration()
                _stepsRemaining.value -= 1
            }
            stopGameOfLife()
        }
    }

    fun startGameOfLife2() {
        gameJob = viewModelScope.launch(defaultDispatcher) {
            while (stepsRemaining.value > 0 && cells.value.numLive > 0) {
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

    fun continueGameOfLife() {
       startGameOfLife(stepsRemaining.value)
    }

    fun stopGameOfLife() {
        _stepsRemaining.value = 0
        gameJob?.cancel()
    }
}