package com.example.gameoflife.model

class Cells private constructor(
    private val grid: MutableList<MutableList<Boolean>>,
    nLive: Int = 0
) {
    fun get(): List<List<Boolean>> = grid

    var numLive = nLive
        private set

    val totalCells: Int = grid.size * (grid.firstOrNull()?.size ?: 0)

    fun makeCellLive(row: Int, column: Int): Cells {
        return setLiveness(row, column, isAlive = true)
    }

    fun makeCellDead(row: Int, column: Int): Cells {
        return setLiveness(row, column, isAlive = false)
    }

    fun getNoOfLiveNeighbours(row: Int, column: Int): Int {
        if (grid.size == 0) return 0
        var sum = 0
        val iLower = 0.coerceAtLeast(row - 1)
        val jLower = 0.coerceAtLeast(column - 1)
        val iUpper = (grid.size - 1).coerceAtMost(row + 1)
        val jUpper = (grid[0].size - 1).coerceAtMost(column + 1)
        for (i in iLower..iUpper) {
            for (j in jLower..jUpper) {
                when {
                    i == row && j == column -> continue
                    grid[i][j] -> sum += 1
                }
            }
        }
        return sum
    }

    // A performance tweak to this would be to scan in 3-length rows and cache them
    fun getNextGeneration(): Cells {
        val newGrid: MutableList<MutableList<Boolean>> = mutableListOf()
        var nLive = 0
        for (i in grid.indices) {
            newGrid.add(mutableListOf())
            for (j in grid[i].indices) {
                when (getNoOfLiveNeighbours(i, j)) {
                    0, 1 -> newGrid[i].add(false) // starvation
                    2 -> { nLive += newGrid[i].addAndCount(grid[i][j]) } // if alive it stays alive
                    3 -> { nLive += newGrid[i].addAndCount(true) }// if dead there is reproduction, if alive it stays alive
                    else -> newGrid[i].add(false) // overpopulation
                }
            }
        }
        return Cells(newGrid, nLive)
    }

    fun toggleLiveness(x: Int, y: Int): Cells {
        return setLiveness(x, y, !grid[x][y])
    }

    private fun setLiveness(x: Int, y: Int, isAlive: Boolean): Cells {
        if (x >= grid.first().size || y >= grid.size) return this

        setAndCount(x, y, isAlive)
        return Cells(grid, numLive)
    }

    private fun setAndCount(x: Int, y: Int, isAlive: Boolean) {
        if (grid[x][y] xor isAlive) {
            grid[x][y] = isAlive
            numLive += if (isAlive) 1 else -1
        }
    }

    private fun MutableList<Boolean>.addAndCount(value: Boolean): Int {
        this.add(value)
        return if (value) 1 else 0
    }

    companion object {
        fun makeGrid(rows: Int, columns: Int): Cells {
            if (rows == 0 || columns == 0) return Cells(mutableListOf())

            val cells = (0 until rows * columns)
                .map { false }
                .chunked(columns)
                .map { it.toMutableList() }
                .toMutableList()

            return Cells(cells)
        }
    }
}