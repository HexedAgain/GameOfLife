package com.example.gameoflife.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class CellsTest: FreeSpec() {
    lateinit var cells: Cells
    fun setup(grid: List<List<Boolean>> = (0 until 16).map { false}.chunked(4)) {
        cells = Cells(grid)
    }
    init {
       "makeCellLive" - {
           "if there exists a slot at the position then this cell is made live and the function returns true" {
               setup(grid = (0 until 1).map { false }.chunked(1))

               val isSuccess = cells.makeCellLive(0,0)

               isSuccess shouldBe true
               cells.get().flatten().first() shouldBe true
           }
           "otherwise it does not change any cells and returns false" - {
               "x out of bounds" {
                   setup(grid = (0 until 1).map { false }.chunked(1))

                   val isSuccess = cells.makeCellLive(1, 0)

                   isSuccess shouldBe false
                   cells.get().flatten().first() shouldBe false
               }
               "y out of bounds" {
                   setup(grid = (0 until 1).map { false }.chunked(1))

                   val isSuccess = cells.makeCellLive(0, 1)

                   isSuccess shouldBe false
                   cells.get().flatten().first() shouldBe false
               }
           }
       }
        "makeCellDead" - {
            "if there exists a slot at the position then this cell is made dead and the function returns true" {
                setup(grid = (0 until 1).map { true }.chunked(1))

                val isSuccess = cells.makeCellDead(0,0)

                isSuccess shouldBe true
                cells.get().flatten().first() shouldBe false
            }
            "otherwise it does not change any cells and returns false" - {
                "x out of bounds" {
                    setup(grid = (0 until 1).map { true }.chunked(1))

                    val isSuccess = cells.makeCellDead(1, 0)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe true
                }
                "y out of bounds" {
                    setup(grid = (0 until 1).map { true }.chunked(1))

                    val isSuccess = cells.makeCellDead(0, 1)

                    isSuccess shouldBe false
                    cells.get().flatten().first() shouldBe true
                }
            }
        }
    }
}