package com.example.gameoflife.model

class Cells(
    private val grid: List<List<Boolean>>
) {
    private val _grid: MutableList<MutableList<Boolean>> = grid.map { it.toMutableList() }.toMutableList()
    fun get(): List<List<Boolean>> = _grid

    val totalCells: Int = get().flatten().size

    fun makeCellLive(row: Int, column: Int): Boolean {
        return setLiveness(row, column, alive = true)
    }

    fun makeCellDead(row: Int, column: Int): Boolean {
        return setLiveness(row, column, alive = false)
    }

    fun getNoOfLiveNeighbours(row: Int, column: Int): Int {
        // Will attempt to refactor this once sufficiently exhaustive tests in place
        var sum = 0
        for (i in (row - 1)..(row + 1)) {
            for (j in (column - 1)..(column) + 1) {
                if (i == row && j == column) continue // don't include target cell liveness in sum
                if (i >= 0 && i < _grid.size && j >= 0 && j < _grid[i].size) {
                    if (_grid[i][j]) sum += 1
                }
            }
        }
        return sum
    }

    private fun setLiveness(x: Int, y: Int, alive: Boolean): Boolean {
        if (x >= grid.first().size || y >= grid.size) return false

        _grid[x][y] = alive
        return true
    }

    companion object {
        fun makeGrid(rows: Int, columns: Int): Cells {
            return Cells((0 until rows * columns).map { false }.chunked(rows))
        }
    }
}