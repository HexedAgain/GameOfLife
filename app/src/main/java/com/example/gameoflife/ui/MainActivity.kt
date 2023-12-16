package com.example.gameoflife.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameoflife.ui.theme.GameOfLifeTheme
import com.example.gameoflife.viewmodel.MainScreenViewModel
import org.koin.androidx.compose.koinViewModel

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
                    GameOfLifeContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOfLifeContent(mainScreenViewModel: MainScreenViewModel = koinViewModel()) {
    val rows by mainScreenViewModel.rows.collectAsState()
    val columns by mainScreenViewModel.columns.collectAsState()
    val cells by mainScreenViewModel.cells.collectAsState()
    Column() {
        Column(
            modifier = Modifier
                .fillMaxHeight(.5f)
                .fillMaxWidth()
                .padding(8.dp)
                .border(border = BorderStroke(1.dp, Color.Gray))
        ) {
            val numRows = cells.get().size
            val numCols = cells.get().firstOrNull()?.size ?: 0
            val configuration = LocalConfiguration.current
            val width = (configuration.screenWidthDp.dp - 16.dp) / numRows
            val height = ((configuration.screenHeightDp.dp / 2) - 16.dp) / numCols - 2.dp
            for (i in 0 until numRows) {
                Row(
                ) {
                    for (j in 0 until numCols) {
                        Box(
                            modifier = Modifier
                                .clickable { mainScreenViewModel.updateCell(i, j) }
                                .width(width)
                                .height(height)
                                .background(color = if (cells.get()[i][j]) Color.Green else Color.White)
                                .border(border = BorderStroke(1.dp, Color.Gray))
                        ) {

                        }
                    }
                }
            }
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(140.dp)
                        .padding(8.dp),
                    label = { Text("Rows", fontSize = 14.sp) },
                    value = rows,
                    onValueChange = { mainScreenViewModel.updateRows(it) }
                )
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(140.dp)
                        .padding(8.dp),
                    label = { Text("Columns") },
                    value = columns,
                    onValueChange = { mainScreenViewModel.updateColumns(it) }
                )
                Button(onClick = {
                    mainScreenViewModel.initialiseCells(rows.toInt(), columns.toInt())
                }) {
                    Text("Update Grid")
                }
            }
        }
        Button(onClick = {
            mainScreenViewModel.startGameOfLife2()
        }) {
            Text("Play Game Of Life")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GameOfLifeTheme {
        Greeting("Android")
    }
}