package com.example.gameoflife.model

import io.kotest.assertions.AssertionFailedError
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class CellsTest: FreeSpec() {
    lateinit var cells: Cells
    fun setup(rows: Int = 4, columns: Int = 4) {
        cells = Cells.makeGrid(rows, columns)
    }

    fun testGrid(cells: Cells, action: (Cells, Int, Int) -> Unit) {
        (0 until cells.totalCells).forEach { idx ->
            val row = idx / 3;
            val column = idx % 3;
            try {
                action(cells, row, column)
            } catch (ex: AssertionFailedError) {
                throw AssertionFailedError(
                    message ="""test failed at row: $row, column: $column
                                   |    expected: ${ex.expectedValue}, actual: ${ex.actualValue}
                               """.trimMargin(),
                    cause = ex.cause,
                    expectedValue = ex.expectedValue,
                    actualValue = ex.actualValue
                )
            }
        }
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
        "getNoOfLiveNeighbours" - {
            "it returns the count of cells that are one cell away from target in any direction" - {
                "0 live" {
                    cells = Cells.makeGrid(3, 3)

                    testGrid(cells) { cells, row, column ->
                        cells.getNoOfLiveNeighbours(row, column) shouldBe 0
                    }
                }
                // Note can exploit rotational symmetry to exhaustively test these ...
                "1 live" - {
                    "live at corner" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(0, 0)

                        val expected = listOf(
                            0,1,0,
                            1,1,0,
                            0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "live at edge" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(0, 1)

                        val expected = listOf(
                            1,0,1,
                            1,1,1,
                            0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "live inside" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(1, 1)

                        val expected = listOf(
                            1,1,1,
                            1,0,1,
                            1,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                }
            }
        }
//        "getCellLiveness" - {
//            "if a cell has zero or one live neighbours the cell dies of starvation" - {
//                "zero neighbours" - {
//                    "cell on corner" {
//                        setup(rows = 2, columns = 2)
//                        true shouldBe false
//                    }
//                    "cell on edge" {
//                        true shouldBe false
//                    }
//                    "cell inside" {
//                        true shouldBe false
//                    }
//                }
//                "one neighbour" {
//                    true shouldBe false
//                }
//            }
//        }
    }
}