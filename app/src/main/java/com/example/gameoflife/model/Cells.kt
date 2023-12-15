package com.example.gameoflife.model

class Cells(
    grid: List<List<Boolean>>
) {
    private var _grid: MutableList<MutableList<Boolean>> = grid.map { it.toMutableList() }.toMutableList()
    fun get(): List<List<Boolean>> = _grid

    val totalCells: Int = get().flatten().size

    fun makeCellLive(row: Int, column: Int): Cells {
        return setLiveness(row, column, alive = true)
    }

    fun makeCellDead(row: Int, column: Int): Cells {
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

    fun getNextGeneration(): Cells {
        val newGrid: MutableList<MutableList<Boolean>> = mutableListOf()
        for (i in _grid.indices) {
            newGrid.add(mutableListOf())
            for (j in _grid[i].indices) {
                when (getNoOfLiveNeighbours(i, j)) {
                    0, 1 -> newGrid[i].add(false) // starvation
                    2 -> newGrid[i].add(_grid[i][j]) // if alive it stays alive
                    3 -> newGrid[i].add(true) // if dead there is reproduction, if alive it stays alive
                    else -> newGrid[i].add(false) // overpopulation
                }
            }
        }
        return Cells(newGrid)
    }

    fun toggleLiveness(x: Int, y: Int): Cells {
        return setLiveness(x, y, !_grid[x][y])
    }

    private fun setLiveness(x: Int, y: Int, alive: Boolean): Cells {
        if (x >= _grid.first().size || y >= _grid.size) return this

        _grid[x][y] = alive
        return Cells(_grid)
    }

    companion object {
        fun makeGrid(rows: Int, columns: Int): Cells {
            if (rows == 0 || columns == 0) return Cells(listOf())
            return Cells((0 until rows * columns).map { false }.chunked(columns))
        }
    }
}