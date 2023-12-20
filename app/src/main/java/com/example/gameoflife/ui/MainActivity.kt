package com.example.gameoflife.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    val scope = rememberCoroutineScope()
                    var showBottomSheet by remember { mutableStateOf(false) }
                    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)
                    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
                    LaunchedEffect(Unit) {
                        sheetState.show() // this is a hack because the modal sheet instantly opens
                        sheetState.hide() // without animation when first triggered (looks crapola)
                    }
//                    BottomSheetScaffold(
//                        scaffoldState = scaffoldState,
//                        sheetContent = {
//                            Column(modifier = Modifier.fillMaxHeight(.75f)){
//                                Text("sheet content")
//                            }
//                        },
//                        sheetPeekHeight = 0.dp
//                    ) {
//                        GameOfLifeContent(sheetState, scope)
//                    }
                    if (showBottomSheet) {
                        LaunchedEffect(Unit) {
                            sheetState.hide()
                        }
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
//                            Column(modifier = Modifier.fillMaxHeight(0.75f)) {
                                Text("test")
//                            }
                        }
                    }
                    //GameOfLifeContent(sheetState, scope)
                    GameOfLifeContent2() {
                        showBottomSheet = true
                    }
                }
            }
        }
    }
}

@Composable
fun SheetContent() {
    Column() {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//fun GameOfLifeContent(sheetState: SheetState, scope: CoroutineScope) {
fun GameOfLifeContent(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    onShow: () -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        CellsGrid()
        Row() {
//            Button(onClick = { mainScreenViewModel.startGameOfLife2() }) {
//                Text(stringResource(id = R.string.play_game_of_life))
//            }
//            Button( onClick = {}) {
//                Text(stringResource(id = R.string.play_game_of_life))
//            }
            IconButton(onClick = { /*TODO*/ }) {

            }
        }
        Button(onClick = {
            onShow()
//            scope.launch {
//                Timber.e("showing sheet")
//                sheetState.expand()
//            }
        }) {
            Text ("show sheet")
        }
        NumberOfCellsRow()
        PlayGameOfLife()
    }
}

@Composable
fun GameOfLifeContent2(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    onShow: () -> Unit
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
            if (isPlaying) {
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.PLAY),
                    actionClick = { mainScreenViewModel.startGameOfLife2() }
                )
            } else {
                ActionButton(
                    ActionButtonConfig.actionFor(ActionType.PAUSE),
                    actionClick = { mainScreenViewModel.startGameOfLife2() }
                )
            }
            ActionButton(
                ActionButtonConfig.actionFor(ActionType.RANDOMISE),
                actionClick = {}
            )
            ActionButton(
                ActionButtonConfig.actionFor(ActionType.CLEAR),
                actionClick = {}
            )
            ActionButton(
                ActionButtonConfig.actionFor(ActionType.SETTINGS),
                actionClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberOfCellsRow(
    mainScreenViewModel: MainScreenViewModel = koinViewModel()
) {
    val rows by mainScreenViewModel.rows.collectAsState()
    val columns by mainScreenViewModel.columns.collectAsState()
    Row(
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
        Button(
            onClick = { mainScreenViewModel.initialiseCells(rows, columns) },
            enabled = rows != null && columns != null
        ) {
            Text(stringResource(id = R.string.update_grid))
        }
    }
}

fun Float.toDp(context: Context): Dp {
    val density = (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return (this / density).dp
}

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
    val width = gridWidth / numRows
    val height = gridHeight / numCols
//    val height = colHeight / numCols
    val context = LocalContext.current
    Column(
        // Note: defining clickable on each of the cells seems to be a massive performance hit
        modifier = Modifier
            .pointerInput(Pair(numRows, numCols)) {
                detectTapGestures(
                    onTap = {
                        val offsetX = it.x.toDp(context) - 0.dp
                        val offsetY = it.y.toDp(context) - 0.dp
                        val columnIdx = (offsetX / width).toInt()
                        val rowIdx = (offsetY / height).toInt()
                        mainScreenViewModel.updateCell(row = rowIdx, column = columnIdx)
                    }
                )
            }
            .height(gridHeight)
            .width(gridWidth)
            .border(border = BorderStroke(1.dp, Color.Gray))
    ) {
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
fun PlayGameOfLife(mainScreenViewModel: MainScreenViewModel = koinViewModel()) {
    Button(onClick = {
        mainScreenViewModel.startGameOfLife2()
    }) {
        Text(stringResource(id = R.string.content_descr_play_game_of_life))
    }
}

@Composable
fun ActionButton(
    actionButtonConfig: ActionButtonConfig,
    actionClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = actionClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary),
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

data class ActionButtonConfig(
    val contentDescriptionResId: Int,
    val textResId: Int,
    val iconResId: Int
) {
    enum class ActionType {
        PLAY,
        PAUSE,
        RANDOMISE,
        CLEAR,
        SETTINGS
    }

    companion object {
        fun actionFor (actionType: ActionType): ActionButtonConfig {
            return when (actionType) {
                ActionType.PLAY -> ActionButtonConfig(
                    iconResId = R.drawable.play_arrow,
                    textResId = R.string.play,
                    contentDescriptionResId = R.string.content_descr_play_game_of_life,
                )

                ActionType.PAUSE -> ActionButtonConfig(
                    iconResId = R.drawable.pause,
                    textResId = R.string.pause,
                    contentDescriptionResId = R.string.content_descr_pause_game_of_life
                )
                ActionType.RANDOMISE -> ActionButtonConfig(
                    iconResId = R.drawable.refresh,
                    textResId = R.string.randomise_cells,
                    contentDescriptionResId = R.string.content_descr_randomise_cell_liveness
                )
                ActionType.CLEAR -> ActionButtonConfig(
                    iconResId = R.drawable.clear,
                    textResId = R.string.clear_cells,
                    contentDescriptionResId = R.string.content_descr_clear_cells
                )
                ActionType.SETTINGS -> ActionButtonConfig(
                    iconResId = R.drawable.settings,
                    textResId = R.string.game_settings,
                    contentDescriptionResId = R.string.content_descr_game_settings
                )
            }
        }
    }
}