package com.example.gameoflife.model

class Cells(
    private val grid: List<List<Boolean>>
) {
    private val _grid: MutableList<MutableList<Boolean>> = grid.map { it.toMutableList() }.toMutableList()
    fun get(): List<List<Boolean>> = _grid

    fun makeCellLive(x: Int, y: Int): Boolean {
        return setLiveness(x, y, alive = true)
    }

    fun makeCellDead(x: Int, y: Int): Boolean {
        return setLiveness(x, y, alive = false)
    }

    private fun setLiveness(x: Int, y: Int, alive: Boolean): Boolean {
        if (x >= grid.first().size || y >= grid.size) return false

        _grid[x][y] = alive
        return true
    }
}