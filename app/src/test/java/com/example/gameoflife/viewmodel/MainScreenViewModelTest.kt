@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.gameoflife.viewmodel

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class MainScreenViewModelTest: FreeSpec() {
    lateinit var viewModel: MainScreenViewModel
    lateinit var defaultDispatcher: TestDispatcher
    lateinit var mockMainDispatcher: TestDispatcher

    override suspend fun beforeEach(testCase: TestCase) {
        mockMainDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(mockMainDispatcher)
        super.beforeEach(testCase)
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        Dispatchers.resetMain()
    }

    fun setup() {
        defaultDispatcher = StandardTestDispatcher()
        viewModel = MainScreenViewModel(
            defaultDispatcher = defaultDispatcher,
            stepDuration = 100
        )
    }
    init {
        "initialiseCells" - {
            "for rows M, and columns N supplied it populates a M x N grid of cells" {
                setup()

                viewModel.initialiseCells(rows = 1, columns = 2)

                viewModel.cells.value.get() shouldBe listOf(listOf(false, false))
            }
        }
        "updateCell" - {
            "if the cell at [row, column] provided is currently dead, it is brought to life" {
                setup()
                viewModel.initialiseCells(rows = 1, columns = 2)

                viewModel.updateCell(0, 0)

                viewModel.cells.value.get()[0][0] shouldBe true
                viewModel.cells.value.get()[0][1] shouldBe false
            }
            "otherwise it is killed" {
                setup()
                viewModel.initialiseCells(rows = 1, columns = 2)
                viewModel.updateCell(0, 0)

                viewModel.updateCell(0, 0)

                viewModel.cells.value.get()[0][0] shouldBe false
                viewModel.cells.value.get()[0][1] shouldBe false
            }
        }
        "startGameOfLife" - {
            /*
             * X 0 X    X 0 X    X X X
             * 0 X 0 -> X 0 X -> X X X
             * X X X    X X X    X X X
             */
            setup()
            viewModel.initialiseCells(3, 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)

            viewModel.startGameOfLife(noOfGenerations = 2)

            "it initialises the steps counter" {
                viewModel.stepsRemaining.value shouldBe 2
            }
            "after every stepDuration interval it advances cells to the next generation" {
                defaultDispatcher.scheduler.advanceTimeBy(101)

                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, true, false),
                    listOf(false, true, false),
                    listOf(false, false, false),
                )

                viewModel.stepsRemaining.value shouldBe 1

                defaultDispatcher.scheduler.advanceTimeBy(100)

                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, false, false),
                    listOf(false, false, false),
                    listOf(false, false, false),
                )

                viewModel.stepsRemaining.value shouldBe 0
            }
            "once step counter becomes zero the counter stops decrementing" {
                defaultDispatcher.scheduler.advanceTimeBy(100)

                viewModel.stepsRemaining.value shouldBe 0
            }
        }

        "pauseGameOfLife" - {
            setup()
            viewModel.initialiseCells(rows = 3, columns = 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)
            viewModel.startGameOfLife(noOfGenerations = 2)
            defaultDispatcher.scheduler.advanceTimeBy(101)

            viewModel.pauseGameOfLife()

            "the counter no longer decrements after step duration and the cells do not advance to future generations" {
                defaultDispatcher.scheduler.advanceTimeBy(100)

                viewModel.stepsRemaining.value shouldBe 1
                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, true, false),
                    listOf(false, true, false),
                    listOf(false, false, false),
                )
            }
        }

        "continueGameOfLife" - {
            setup()
            viewModel.initialiseCells(rows = 3, columns = 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)
            viewModel.startGameOfLife(noOfGenerations = 2)
            defaultDispatcher.scheduler.advanceTimeBy(101)
            viewModel.pauseGameOfLife()

            viewModel.continueGameOfLife()

            "the counter will decrement again and cells advance to the next state every step duration" {
                defaultDispatcher.scheduler.advanceTimeBy(101)

                viewModel.stepsRemaining.value shouldBe 0
                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, false, false),
                    listOf(false, false, false),
                    listOf(false, false, false),
                )
            }
        }

        "stopGameOfLife" - {
            "the counter will be set to zero and cells will not advance to future generations" {
                setup()
                viewModel.initialiseCells(rows = 3, columns = 3)
                viewModel.updateCell(0, 1)
                viewModel.updateCell(1, 0)
                viewModel.updateCell(1, 2)
                viewModel.startGameOfLife(noOfGenerations = 2)

                viewModel.stopGameOfLife()

                viewModel.stepsRemaining.value shouldBe 0
                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, true, false),
                    listOf(true, false, true),
                    listOf(false, false, false)
                )

                defaultDispatcher.scheduler.advanceTimeBy(101)

                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, true, false),
                    listOf(true, false, true),
                    listOf(false, false, false)
                )
            }
        }
    }
}