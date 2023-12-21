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
                viewModel.stepsRemaining
            }
            "it resets the steps remaining to its initial value" {
                // TODO, would be better to have some stock configurations to simplify the tests
                setup(initialStepsRemaining = 2, initialStepDuration = 100)
                viewModel.startGameOfLife()
                viewModel.initialiseCells(rows = 2, columns = 2)
                viewModel.updateCell(0, 0)
                viewModel.updateCell(0, 1)
                viewModel.updateCell(1, 0)
                defaultDispatcher.scheduler.advanceTimeBy(101)
                viewModel.pauseGameOfLife()
                viewModel.stepsRemaining.value shouldBe 1

                viewModel.clearCells()

                viewModel.stepsRemaining.value shouldBe 2
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
        "updateRows" - {
            "if the value cannot be parsed as an integer rows is set null" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateRows("fake")

                viewModel.rows.value shouldBe null
            }
            "if the value is negative, rows is left unchanged" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateRows("-3")

                viewModel.rows.value shouldBe 1
            }
            "otherwise the number of rows is set to that which was passed" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateRows("3")

                viewModel.rows.value shouldBe 3
            }
        }
        "updateColumns" - {
            "if the value cannot be parsed as an integer it does nothing" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateColumns("fake")

                viewModel.columns.value shouldBe null
            }
            "if the value is negative, columns is left unchanged" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateColumns("-3")

                viewModel.columns.value shouldBe 2
            }
            "otherwise the number of columns is set to that which was passed" {
                setup()
                viewModel.initialiseCells(1, 2)

                viewModel.updateColumns("3")

                viewModel.columns.value shouldBe 3
            }
        }
        "updateStepsRemaining" - {
            // FIXME - this function really should take a string
            "if the steps passed cannot be parsed as an integer, stepsRemaining is set to null" {
                setup(initialStepsRemaining = 1)

                viewModel.updateStepsRemaining("fake")

                viewModel.stepsRemaining.value shouldBe null
            }
            "if the steps passed parses to a negative integer then stepsRemaining stays unchanged" {
                setup(initialStepsRemaining = 1)

                viewModel.updateStepsRemaining("-2")

                viewModel.stepsRemaining.value shouldBe 1
            }
            "otherwise it updates stepsRemaining to the number passed to it" {
                setup(initialStepsRemaining = 1)

                viewModel.updateStepsRemaining("2")

                viewModel.stepsRemaining.value shouldBe 2
            }
            "the value passed will be used to reset the steps remaining if a game runs to completion" {
                setup(initialStepsRemaining = 1)

                viewModel.updateStepsRemaining("2")
                viewModel.startGameOfLife()
                defaultDispatcher.scheduler.advanceUntilIdle()

                viewModel.stepsRemaining.value shouldBe 2
            }
        }
        "updateStepDuration" - {
            "if the duration passed in does not parse as a long stepDuration is set null" {
                setup(initialStepDuration = 1)

                viewModel.updateStepDuration("fake")

                viewModel.stepDurationMs.value shouldBe null
            }
            "if duration passed parses to a negative integer it does nothing" {
                setup(initialStepDuration = 1)

                viewModel.updateStepDuration("-123")

                viewModel.stepDurationMs.value shouldBe 1
            }
            "otherwise it updates the duration between steps to the number passed in" {
                setup(initialStepDuration = 1)

                viewModel.updateStepDuration("2")

                viewModel.stepDurationMs.value shouldBe 2
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

//                viewModel.stepsRemaining.value shouldBe 0
            }
            "once step counter becomes zero ..." - {
                "the steps remaining is reset to its original value and the counter stops decrementing" {
                    viewModel.stepsRemaining.value shouldBe 2

                    defaultDispatcher.scheduler.advanceTimeBy(100)

                    viewModel.stepsRemaining.value shouldBe 2
                }
                "isPlaying becomes false" {
                    viewModel.isPlaying.value shouldBe false
                }
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
            viewModel.pauseGameOfLife()

            viewModel.continueGameOfLife()

            "isPlaying becomes true" {
                viewModel.isPlaying.value shouldBe true
            }

            "the counter will decrement again and cells advance to the next state every step duration" {
                defaultDispatcher.scheduler.advanceTimeBy(101)

                viewModel.stepsRemaining.value shouldBe 1
                viewModel.cells.value.get() shouldBe listOf(
                    listOf(false, true, false),
                    listOf(false, true, false),
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