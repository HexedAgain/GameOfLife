package com.example.gameoflife.model

class Cells(
    private val grid: List<List<Boolean>>
) {
    private var _grid: MutableList<MutableList<Boolean>> = grid.map { it.toMutableList() }.toMutableList()
    fun get(): List<List<Boolean>> = _grid

    val totalCells: Int = get().flatten().size

    fun makeCellLive(row: Int, column: Int): Boolean {
        return setLiveness(row, column, alive = true)
    }

    fun makeCellDead(row: Int, column: Int): Boolean {
        return setLiveness(row, column, alive = false)
    }

    fun getNoOfLiveNeighbours(row: Int, column: Int): Int {
        if (_grid.size == 0) return 0
        var sum = 0
        val iLower = Math.max(0, row - 1)
        val iUpper = Math.min(_grid.size - 1, row + 1)
        val jLower = Math.max(0, column - 1)
        val jUpper = Math.min(_grid[0].size - 1, column + 1)
        for (i in iLower..iUpper) {
            for (j in jLower..jUpper) {
                when {
                    i == row && j == column -> continue
                    _grid[i][j] -> sum += 1
                }
            }
        }
        return sum
    }

    fun nextGeneration() {
        val newGrid: MutableList<MutableList<Boolean>> = mutableListOf()
        for (i in grid.indices) {
            newGrid.add(mutableListOf())
            for (j in grid.indices) {
                when (getNoOfLiveNeighbours(i, j)) {
                    0, 1 -> newGrid[i].add(false)
                    2, 3 -> newGrid[i].add(true)
                    else -> newGrid[i].add(false)
                }
            }
        }
        _grid = newGrid
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