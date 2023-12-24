package com.example.gameoflife.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.gameoflife.R
import com.example.gameoflife.ui.ActionButtonConfig.ActionType
import com.example.gameoflife.ui.theme.GameOfLifeTheme
import com.example.gameoflife.viewmodel.MainScreenViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameOfLifeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showBottomSheet by remember { mutableStateOf(false) }
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            SheetContent()
                        }
                    }
                    GameOfLifeContent() {
                        showBottomSheet = true
                    }
                }
            }
        }
    }
}

@Composable
fun SheetContent() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(state = scrollState)
    ) {
        NumberOfCellsRow()
        AnimationSpeedRow()
        NumberOfGenerationsRow()
        Box(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun GameOfLifeContent(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    onShowBottomSheet: () -> Unit
) {
    val isPlaying by mainScreenViewModel.isPlaying.collectAsState()
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        CellsGrid()
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isPlaying) {
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.PLAY),
                    actionClick = { mainScreenViewModel.startGameOfLife() }
                )
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.RANDOMISE),
                    isEnabled = false,
                    actionClick = {}
                )
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.CLEAR),
                    actionClick = { mainScreenViewModel.clearCells() }
                )
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.SETTINGS),
                    actionClick = { onShowBottomSheet() }
                )
            } else {
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.PAUSE),
                    actionClick = { mainScreenViewModel.pauseGameOfLife() }
                )
            }
        }
    }
}

@Composable
fun NumberOfCellsRow(
    mainScreenViewModel: MainScreenViewModel = koinViewModel()
) {
    val rows by mainScreenViewModel.rows.collectAsState()
    val columns by mainScreenViewModel.columns.collectAsState()
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(120.dp)
                .padding(8.dp),
            label = { Text(stringResource(id = R.string.number_of_rows), fontSize = 14.sp) },
            value = "${rows ?: ""}",
            onValueChange = { mainScreenViewModel.updateRows(it) }
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(120.dp)
                .padding(8.dp),
            label = { Text(stringResource(id = R.string.number_of_columns)) },
            value = "${columns ?: ""}",
            onValueChange = { mainScreenViewModel.updateColumns(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { mainScreenViewModel.initialiseCells(rows, columns) },
            enabled = rows != null && columns != null
        ) {
            Text(stringResource(id = R.string.update_grid))
        }
    }
}

@Composable
fun AnimationSpeedRow(mainScreenViewModel: MainScreenViewModel = koinViewModel()) {
    val animationSpeed by mainScreenViewModel.stepDurationMs.collectAsState()
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(8.dp),
            label = { Text(stringResource(id = R.string.animation_speed), fontSize = 14.sp) },
            value = "${animationSpeed ?: ""}",
            onValueChange = { mainScreenViewModel.updateStepDuration(it) }
        )
    }
}

@Composable
fun NumberOfGenerationsRow(
    mainScreenViewModel: MainScreenViewModel = koinViewModel()
) {
    val noOfGenerations by mainScreenViewModel.stepsRemaining.collectAsState()
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(8.dp),
            label = { Text(stringResource(id = R.string.number_of_generations), fontSize = 14.sp) },
            value = "${noOfGenerations ?: ""}",
            onValueChange = { mainScreenViewModel.updateStepsRemaining(it) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellsGrid(
    mainScreenViewModel: MainScreenViewModel = koinViewModel()
) {
    val cells by mainScreenViewModel.cells.collectAsState()
    val numRows = cells.get().size
    val numCols = cells.get().firstOrNull()?.size ?: 0
    val configuration = LocalConfiguration.current
    val gridWidth = configuration.screenWidthDp.dp - 16.dp
    val gridHeight = configuration.screenHeightDp.dp - 128.dp
    val width = gridWidth / numCols
    val height = gridHeight / numRows
    val dragStart = remember { mutableStateOf(Offset(Float.MAX_VALUE, Float.MAX_VALUE)) }
    val dragEnd = remember { mutableStateOf(Offset(Float.MAX_VALUE, Float.MAX_VALUE)) }
    val draggable2DState = rememberDraggable2DState(onDelta = {
        dragEnd.value = Offset(dragEnd.value.x + it.x, dragEnd.value.y + it.y)
    })

    val selectedStart = Pair(-1, -1)
    val selectedEnd = Pair(-1, -1)
    Column(
        // Note: defining clickable on each of the cells seems to be a massive performance hit
        modifier = Modifier
            .pointerInput(Pair(numRows, numCols)) {
                detectTapGestures(
                    onTap = {
                        val offsetX = it.x.toDp() - 0.dp
                        val offsetY = it.y.toDp() - 0.dp
                        val columnIdx = (offsetX / width).toInt()
                        val rowIdx = (offsetY / height).toInt()
                        mainScreenViewModel.updateCell(row = rowIdx, column = columnIdx)
                    },
                )
            }
            .draggable2D(
                draggable2DState,
                onDragStarted = {
                    dragStart.value = it
                    dragEnd.value = Offset(0f, 0f)
                }
            )
            .height(gridHeight)
            .width(gridWidth)
            .border(border = BorderStroke(1.dp, Color.Gray))
    ) {
        if (dragStart.value.x < Float.MAX_VALUE && dragStart.value.y < Float.MAX_VALUE && dragEnd.value.x < Float.MAX_VALUE && dragEnd.value.y < Float.MAX_VALUE) {
            Canvas(modifier = Modifier.zIndex(1f)) {
                drawRect(
                    topLeft = dragStart.value,
                    color = Color.Yellow.copy(alpha = 0.5f),
                    size = Size(dragEnd.value.x, dragEnd.value.y)
                )
            }
        }
        val cellModifier = Modifier
            .width(width)
            .height(height)
            .border(border = BorderStroke(.5.dp, Color.Gray))
        for (i in 0 until numRows) {
            Row {
                for (j in 0 until numCols) {
                    Canvas(
                        modifier = cellModifier
                    ) {
                        drawRect(
                            color = if (cells.get()[i][j]) Color.Green else Color.White,
                            size = Size(width.toPx(), height.toPx())
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    actionButtonConfig: ActionButtonConfig,
    isEnabled: Boolean = true,
    actionClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = actionClick,
            enabled = isEnabled,
            modifier = Modifier
                .clip(CircleShape)
                .background(color = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = actionButtonConfig.iconResId),
                    contentDescription = stringResource(id = actionButtonConfig.contentDescriptionResId),
                    tint = Color.White
                )
            }
        }
        Text(
            text = stringResource(id = actionButtonConfig.textResId)
        )
    }
}