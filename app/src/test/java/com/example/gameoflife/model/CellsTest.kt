package com.example.gameoflife.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class CellsTest: FreeSpec() {
    lateinit var cells: Cells
//    fun setup(grid: List<List<Boolean>> = (0 until 16).map { false}.chunked(4)) {
//        cells = Cells(grid)
//    }
    fun setup(rows: Int = 4, columns: Int = 4) {
        cells = Cells.makeGrid(rows, columns)
    }
    init {
        "makeGrid" - {
            "given M rows and N columns it creates an M x N grid with all cells initialised false" {
                cells = Cells.makeGrid(rows = 3, columns = 2)

                cells.get().map { it.size } shouldBe listOf(3, 3)
                cells.get().flatten().any { it } shouldBe false
            }
        }
        "makeCellLive" - {
            "if there exists a slot at the position then this cell is made live and the function returns true" {
                setup(rows = 1, columns = 1)

                val isSuccess = cells.makeCellLive(0,0)

                isSuccess shouldBe true
                cells.get().flatten().first() shouldBe true
            }
            "otherwise it does not change any cells and returns false" - {
                "x out of bounds" {
                    setup(rows = 1, columns = 1)

                    val isSuccess = cells.makeCellLive(1, 0)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe false
                }
                "y out of bounds" {
                    setup(rows = 1, columns = 1)

                    val isSuccess = cells.makeCellLive(0, 1)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe false
                }
            }
        }
        "makeCellDead" - {
            "if there exists a slot at the position then this cell is made dead and the function returns true" {
                setup(rows = 1, columns = 1)

                val isSuccess = cells.makeCellDead(0,0)

                isSuccess shouldBe true
                cells.get().flatten().first() shouldBe false
            }
            "otherwise it does not change any cells and returns false" - {
                "x out of bounds" {
                    setup(rows = 1, columns = 1)
                    cells.makeCellLive(0, 0)

                    val isSuccess = cells.makeCellDead(1, 0)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe true
                }
                "y out of bounds" {
                    setup(rows = 1, columns = 1)
                    cells.makeCellLive(0, 0)

                    val isSuccess = cells.makeCellDead(0, 1)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe true
                }
            }
        }
        "getCellLiveness" - {
            "if a cell has zero or one live neighbours the cell dies of starvation" - {
                "zero neighbours" - {
                    "cell on corner" {
                        setup(rows = 2, columns = 2)
                        true shouldBe false
                    }
                    "cell on edge" {
                        true shouldBe false
                    }
                    "cell inside" {
                        true shouldBe false
                    }
                }
                "one neighbour" {

                }
            }
        }
    }
}