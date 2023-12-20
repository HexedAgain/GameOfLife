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

    fun setup(
        initialStepsRemaining: Int = 1,
        initialStepDuration: Long = 1,
    ) {
        defaultDispatcher = StandardTestDispatcher()
        viewModel = MainScreenViewModel(
            defaultDispatcher = defaultDispatcher,
            stepsRemaining = initialStepsRemaining,
            stepDuration = initialStepDuration
        )
    }
    init {
        "initialiseCells" - {
            "for rows M, and columns N supplied it populates a M x N grid of cells" - {
                setup()

                viewModel.initialiseCells(rows = 1, columns = 2)

                viewModel.cells.value.get() shouldBe listOf(listOf(false, false))

                "rows and columns will be set to the values passed to it" {
                    viewModel.rows.value shouldBe 1
                    viewModel.columns.value shouldBe 2
                }
            }
        }
        "clearCells" - {
            "for an M x N grid of cells where some of them are live, the result is an M x N grid of cells with none of them live" {
                setup()
                viewModel.initialiseCells(rows = 1, columns = 2)
                viewModel.updateCell(0, 0)

                viewModel.clearCells()

                viewModel.cells.value.get().flatten() shouldBe listOf(false, false)
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
        "updateStepsRemaining" - {
            "if the steps passed is negative it does nothing" {
                setup(initialStepsRemaining = 0)

                viewModel.updateStepsRemaining(-123)

                viewModel.stepsRemaining.value shouldBe 0
            }
            "otherwise it updates stepsRemaining to the number passed to it" {
                setup(initialStepsRemaining = 0)

                viewModel.updateStepsRemaining(123)

                viewModel.stepsRemaining.value shouldBe 123
            }
        }
        "updateStepDuration" - {
            "if duration passed in is negative it does nothing" {
                setup(initialStepDuration = 0)

                viewModel.updateStepDuration(-123)

                viewModel.stepDurationMs.value shouldBe 0
            }
            "otherwise it updates the duration between steps to the number passed in" {
                setup(initialStepDuration = 0)

                viewModel.updateStepDuration(123)

                viewModel.stepDurationMs.value shouldBe 123
            }
        }
        "startGameOfLife" - {
            /*
             * X 0 X    X 0 X    X X X
             * 0 X 0 -> X 0 X -> X X X
             * X X X    X X X    X X X
             */
            setup(initialStepsRemaining = 2, initialStepDuration = 100)
            viewModel.initialiseCells(3, 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)

            viewModel.startGameOfLife()

            "it sets isPlaying to true" {
                viewModel.isPlaying.value shouldBe true
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
            "once step counter becomes zero the counter stops decrementing and isPlaying becomes false" {
                defaultDispatcher.scheduler.advanceTimeBy(100)

                viewModel.stepsRemaining.value shouldBe 0
                viewModel.isPlaying.value shouldBe false
            }
        }

        "pauseGameOfLife" - {
            setup(initialStepsRemaining = 2, initialStepDuration = 100)
            viewModel.initialiseCells(rows = 3, columns = 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)
            viewModel.startGameOfLife()
            defaultDispatcher.scheduler.advanceTimeBy(101)

            viewModel.pauseGameOfLife()

            "it sets isPlaying to false" {
                viewModel.isPlaying.value shouldBe false
            }

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
            setup(initialStepDuration = 100, initialStepsRemaining = 2)
            viewModel.initialiseCells(rows = 3, columns = 3)
            viewModel.updateCell(0, 1)
            viewModel.updateCell(1, 0)
            viewModel.updateCell(1, 2)
            viewModel.startGameOfLife()
            defaultDispatcher.scheduler.advanceTimeBy(101)
            viewModel.pauseGameOfLife()

            viewModel.continueGameOfLife()

            "isPlaying becomes true" {
                viewModel.isPlaying.value shouldBe true
            }

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
            "the counter will be set to zero and cells will not advance to future generations" - {
                setup(initialStepDuration = 100, initialStepsRemaining = 2)
                viewModel.initialiseCells(rows = 3, columns = 3)
                viewModel.updateCell(0, 1)
                viewModel.updateCell(1, 0)
                viewModel.updateCell(1, 2)
                viewModel.startGameOfLife()

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

                "it sets isPlaying to false" {
                    viewModel.isPlaying.value shouldBe false
                }
            }
        }
    }
}