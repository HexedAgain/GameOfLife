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
            val row = idx / 3
            val column = idx % 3
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

                cells.get().map { it.size } shouldBe listOf(2, 2, 2)
                cells.get().flatten().any { it } shouldBe false
            }
        }
        "makeCellLive" - {
            "if there exists a slot at the position then it returns a new Cells where this particular cell is alive" {
                setup(rows = 1, columns = 1)

                val cellsUnderTest = cells.makeCellLive(0,0)

                cellsUnderTest.get().flatten().first() shouldBe true
            }
            "otherwise it does not change any cells" - {
                "x out of bounds" {
                    setup(rows = 1, columns = 1)

                    val cellsUnderTest = cells.makeCellLive(1, 0)

                    cellsUnderTest.get().flatten().first() shouldBe false
                }
                "y out of bounds" {
                    setup(rows = 1, columns = 1)

                    val cellsUnderTest = cells.makeCellLive(0, 1)

                    cellsUnderTest.get().flatten().first() shouldBe false
                }
            }
        }
        "makeCellDead" - {
            "if there exists a slot at the position then it returns a new Cells where this particular cell is dead" {
                setup(rows = 1, columns = 1)

                val cellsUnderTest = cells.makeCellDead(0,0)

                cellsUnderTest.get().flatten().first() shouldBe false
            }
            "otherwise it does not change any cells and returns false" - {
                "x out of bounds" {
                    setup(rows = 1, columns = 1)
                    cells = cells.makeCellLive(0, 0)

                    val cellsUnderTest = cells.makeCellDead(1, 0)

                    cellsUnderTest.get().flatten().first() shouldBe true
                }
                "y out of bounds" {
                    setup(rows = 1, columns = 1)
                    cells = cells.makeCellLive(0, 0)

                    val cellsUnderTest = cells.makeCellDead(0, 1)

                    cellsUnderTest.get().flatten().first() shouldBe true
                }
            }
        }
        "toggleLiveness" - {
            "if the cell at [row][column] is dead it becomes alive" {
                cells = Cells.makeGrid(1, 1)

                val cellsUnderTest = cells.toggleLiveness(0, 0)

                cellsUnderTest.get()[0][0] shouldBe true
            }
            "otherwise it becomes dead" {
                cells = Cells.makeGrid(1, 1)
                cells = cells.toggleLiveness(0, 0)

                val cellsUnderTest = cells.toggleLiveness(0, 0)

                cellsUnderTest.get()[0][0] shouldBe false
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
                // Note can exploit rotational or reflectional symmetry to exhaustively test these ...
                "1 live" - {
                    "live at corner" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(0, 0)

                        val expected = listOf( 0,1,0, 1,1,0, 0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "live at edge" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(0, 1)

                        val expected = listOf( 1,0,1, 1,1,1, 0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "live inside" {
                        cells = Cells.makeGrid(3, 3)
                        cells.makeCellLive(1, 1)

                        val expected = listOf( 1,1,1, 1,0,1, 1,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                }
                "2 live" - {
                    "[OO.][...][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,1,1, 2,2,1, 0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O.O][...][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,2,0, 1,2,1, 0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][.O.][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(1, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,2,1, 2,1,1, 1,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][..O][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(1, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,2,1, 1,2,0, 0,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][...][.O.]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(2, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,1,0, 2,2,1, 1,0,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][...][..O]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(2, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,1,0, 1,2,1, 0,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                }
                "3 live" - {
                    "[OOO][...][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,2,1, 2,3,2, 0,0,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][O..][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(1, 0)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 2,2,1, 2,3,1, 1,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][.O.][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 2,2,2, 3,2,2, 1,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][..O][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(1, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,2,2, 2,3,1, 0,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][...][O..]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(2, 0)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,1,1, 3,3,1, 0,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][...][.O.]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(2, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,1,1, 3,3,2, 1,0,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[OO.][...][..O]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 1), Pair(2, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,1,1, 2,3,2, 0,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O.O][O..][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 2), Pair(1, 0)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,3,0, 1,3,1, 1,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O.O][.O.][...]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 2), Pair(1, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,3,1, 2,2,2, 1,1,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O.O][...][O..]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,2,0, 2,3,1, 0,1,0).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O.O][...][.O.]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(0, 2), Pair(2, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,2,0, 2,3,2, 1,0,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][.O.][.O.]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(1, 1), Pair(2, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,2,1, 3,2,2, 2,1,2).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][.O.][..O]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 1,2,1, 2,2,2, 1,2,1).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                    "[O..][..O][.O.]" {
                        cells = Cells.makeGrid(3, 3)
                        listOf(Pair(0, 0), Pair(1, 2), Pair(2, 1)).forEach { cells.makeCellLive(it.first, it.second) }

                        val expected = listOf( 0,2,1, 2,3,1, 1,1,2).chunked(3)

                        testGrid(cells) { cells, row, column ->
                            cells.getNoOfLiveNeighbours(row, column) shouldBe expected[row][column]
                        }
                    }
                }
                // That's enough. If I break something I'd be surprised if one of these doesn't fail (and the 4 case is massive)
            }
        }
        "getNextGeneration" - {
            "it returns a new grid, where each cell lives or dies according to game of life rules" - {
                "if the grid contains a single live cell then all cells become dead" {
                    cells = Cells.makeGrid(3, 3)
                    cells.makeCellLive(0, 0)

                    val cellsUnderTest = cells.getNextGeneration()

                    cellsUnderTest.get() shouldBe listOf(listOf(false, false, false), listOf(false, false, false), listOf(false, false, false))
                }
                "if the grid contains two live cells then all cells also become dead (no live cells have 2 or 3 neighbours)" {
                    cells = Cells.makeGrid(3, 3)
                    cells.makeCellLive(0, 0)
                    cells.makeCellLive(0, 1)

                    val cellsUnderTest = cells.getNextGeneration()

                    cellsUnderTest.get() shouldBe listOf(listOf(false, false, false), listOf(false, false, false), listOf(false, false, false))
                }
                "if the grid contains three live cells then ..." - {
                    cells = Cells.makeGrid(3, 3)
                    listOf(Pair(0,0), Pair(0,2), Pair(1, 1)).map { cells.makeCellLive(it.first, it.second) }
                    /*
                     *   0 X 0      X 0 X
                     *   X 0 X  ->  X 0 X
                     *   X X X      X X X
                     */

                    val cellsUnderTest = cells.getNextGeneration()

                    "all live cells that had 1 or 0 neighbours die" {
                        cellsUnderTest.get()[0][0] shouldBe false
                        cellsUnderTest.get()[0][2] shouldBe false
                    }
                    "all live cells with 2 neighbours remain alive" {
                        cellsUnderTest.get()[1][1] shouldBe true
                    }
                    "all dead cells with 3 neighbours become alive" {
                        cellsUnderTest.get()[0][1] shouldBe true
                    }
                }
                "if the grid contains four live cells then ..." - {
                    cells = Cells.makeGrid(3, 3)
                    listOf(Pair(0,0), Pair(0,2), Pair(1, 0), Pair(2, 1)).map { cells.makeCellLive(it.first, it.second) }
                    /*
                     *   0 X 0      X 0 X
                     *   0 X X  ->  0 X X
                     *   X O X      X X X
                     */

                    val cellsUnderTest = cells.getNextGeneration()

                    "all live cells that had 1 or 0 neighbours die" {
                        cellsUnderTest.get()[0][0] shouldBe false
                        cellsUnderTest.get()[0][2] shouldBe false
                        cellsUnderTest.get()[2][1] shouldBe false
                    }
                    "all live cells with 2 neighbours remain alive" {
                        cellsUnderTest.get()[1][0] shouldBe true
                    }
                    "all dead cells with 3 neighbours become alive" {
                        cellsUnderTest.get()[0][1] shouldBe true
                    }
                    "all dead cells with 4 neighbours remain dead" {
                        cellsUnderTest.get()[1][1] shouldBe false
                    }
                }
            }
            "it respects known starting configurations" - {
                "block" {
                    cells = Cells.makeGrid(2, 2)
                    listOf(Pair(0,0), Pair(0,1), Pair(1, 0), Pair(1, 1)).map { cells.makeCellLive(it.first, it.second) }

                    val cellsUnderTest = cells.getNextGeneration()

                    cellsUnderTest.get().flatten() shouldBe cells.get().flatten()
                }
                "blinker" {
                    cells = Cells.makeGrid(3, 3)
                    listOf(Pair(1,0), Pair(1,1), Pair(1, 2)).map { cells.makeCellLive(it.first, it.second) }

                    val secondGeneration = cells.getNextGeneration()
                    val thirdGeneration = secondGeneration.getNextGeneration()

                    secondGeneration.get()[0] shouldBe listOf(false, true, false)
                    secondGeneration.get()[1] shouldBe listOf(false, true, false)
                    secondGeneration.get()[2] shouldBe listOf(false, true, false)
                    thirdGeneration.get().flatten() shouldBe cells.get().flatten()
                }
                "glider" {
                    /*
                     * X X 0 X X    X X X X X    X X X X X    X X X X X    X X X X X
                     * X X X 0 X -> X 0 X 0 X -> X X X 0 X -> X X 0 X X -> X X X 0 X
                     * X 0 0 0 X    X X 0 0 X    X 0 X 0 X    X X X 0 0    X X X X 0
                     * X X X X X    X X 0 X X    X X 0 0 X    X X 0 0 X    X X 0 0 0
                     */
                    cells = Cells.makeGrid(4, 5)
                    listOf(Pair(0,2), Pair(1,3), Pair(2, 1), Pair(2, 2), Pair(2, 3)).map { cells.makeCellLive(it.first, it.second) }

                    val secondGeneration = cells.getNextGeneration()
                    val thirdGeneration = secondGeneration.getNextGeneration()
                    val fourthGeneration = thirdGeneration.getNextGeneration()
                    val fifthGeneration = fourthGeneration.getNextGeneration()

                    secondGeneration.get()[0] shouldBe listOf(false, false, false, false, false)
                    secondGeneration.get()[1] shouldBe listOf(false, true, false, true, false)
                    secondGeneration.get()[2] shouldBe listOf(false, false, true, true, false)
                    secondGeneration.get()[3] shouldBe listOf(false, false, true, false, false)

                    thirdGeneration.get()[0] shouldBe listOf(false, false, false, false, false)
                    thirdGeneration.get()[1] shouldBe listOf(false, false, false, true, false)
                    thirdGeneration.get()[2] shouldBe listOf(false, true, false, true, false)
                    thirdGeneration.get()[3] shouldBe listOf(false, false, true, true, false)

                    fourthGeneration.get()[0] shouldBe listOf(false, false, false, false, false)
                    fourthGeneration.get()[1] shouldBe listOf(false, false, true, false, false)
                    fourthGeneration.get()[2] shouldBe listOf(false, false, false, true, true)
                    fourthGeneration.get()[3] shouldBe listOf(false, false, true, true, false)

                    fifthGeneration.get()[0] shouldBe listOf(false, false, false, false, false)
                    fifthGeneration.get()[1] shouldBe listOf(false, false, false, true, false)
                    fifthGeneration.get()[2] shouldBe listOf(false, false, false, false, true)
                    fifthGeneration.get()[3] shouldBe listOf(false, false, true, true, true)
                }
            }
        }
    }
}