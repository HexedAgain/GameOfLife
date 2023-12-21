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
    stepsRemaining: Int = 10_000,
    stepDuration: Long = 1_000
): ViewModel() {
    private var initialStepsRemaining = stepsRemaining
    private val _cells = MutableStateFlow(Cells.makeGrid(25, 25))
    val cells = _cells.asStateFlow()

    private val _stepDurationMs: MutableStateFlow<Long?> = MutableStateFlow(stepDuration)
    val stepDurationMs = _stepDurationMs.asStateFlow()

    fun updateStepDuration(newDuration: String) {
        val numericStepDuration = kotlin.runCatching { newDuration.toLong() }.getOrNull()
        if (numericStepDuration != null && numericStepDuration < 0) {
            return
        }
        _stepDurationMs.value = numericStepDuration
    }

    private val _stepsRemaining: MutableStateFlow<Int?> = MutableStateFlow(stepsRemaining)
    val stepsRemaining = _stepsRemaining.asStateFlow()
    fun updateStepsRemaining(newStepsRemaining: String) {
        val numericStepsRemaining = kotlin.runCatching { newStepsRemaining.toInt() }.getOrNull()
        if (numericStepsRemaining != null && numericStepsRemaining < 0) {
            return
        }
        _stepsRemaining.value = numericStepsRemaining
        initialStepsRemaining = numericStepsRemaining ?: 0
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _rows = MutableStateFlow(_cells.value.get().size.takeIf { it > 0 })
    val rows = _rows.asStateFlow()
    fun updateRows(newRows: String) {
        val numericNewRows = kotlin.runCatching { newRows.toInt() }.getOrNull()
        if (numericNewRows != null && (numericNewRows < 0 || numericNewRows > MAX_ROWS)) {
            return
        }
        _rows.value = numericNewRows
    }

    private val _columns = MutableStateFlow(_cells.value.get().firstOrNull()?.size?.takeIf { it > 0 })
    val columns = _columns.asStateFlow()
    fun updateColumns(newColumns: String) {
        val numericNewColumns = kotlin.runCatching { newColumns.toInt() }.getOrNull()
        if (numericNewColumns != null && (numericNewColumns < 0 || numericNewColumns > MAX_COLUMNS)) {
            return
        }
        _columns.value = numericNewColumns
    }

    fun initialiseCells(rows: Int?, columns: Int?) {
        _cells.value = Cells.makeGrid(rows ?: return, columns ?: return)
        _rows.value = rows
        _columns.value = columns
    }

    fun clearCells() {
        _cells.value = Cells.makeGrid(_rows.value ?: return, _columns.value ?: return)
        _stepsRemaining.value = initialStepsRemaining
    }

    private var gameJob: Job? = null

    fun updateCell(row: Int, column: Int) {
        _cells.update { it.toggleLiveness(row, column) }
    }

    fun startGameOfLife() {
        _isPlaying.value = true
        gameJob = viewModelScope.launch(defaultDispatcher) {
            while ((stepsRemaining.value ?: 0) > 0 && cells.value.numLive > 0) {
                stepDurationMs.value?.let { delay(it) }
                _cells.value = _cells.value.getNextGeneration()
                _stepsRemaining.value = _stepsRemaining.value?.minus(1)
            }
            this.cancel()
            _isPlaying.value = false
            _stepsRemaining.value = initialStepsRemaining
        }
    }

    fun pauseGameOfLife() {
        gameJob?.cancel()
        _isPlaying.value = false
    }

    fun continueGameOfLife() {
       startGameOfLife()
    }

    fun stopGameOfLife() {
        _stepsRemaining.value = 0
        gameJob?.cancel()
        _isPlaying.value = false
    }

    companion object {
        const val MAX_ROWS = 99
        const val MAX_COLUMNS = 99
    }
}