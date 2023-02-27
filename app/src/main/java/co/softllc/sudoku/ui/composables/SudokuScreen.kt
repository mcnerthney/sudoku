package co.softllc.sudoku.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.SudokuUIState
import co.softllc.sudoku.data.viewmodels.SudokuViewModel
import co.softllc.sudoku.ui.theme.SudokuTheme
import java.util.*


interface GameListener {
    fun onValueChange(index: Int, value: String)
    fun onFocus(index: Int)
}


@Composable
fun SudokuScreen(
    gameId: String,
    navController: NavController,
    sudokuViewModel: SudokuViewModel = viewModel(),
    ) {
    val uiState by sudokuViewModel.uiState.collectAsState()

    sudokuViewModel.setGame(gameId)

    val listener = object : GameListener {
        override fun onValueChange(index: Int, value: String) {
            sudokuViewModel.setValue(index, value)
        }

        override fun onFocus(index: Int) {
            sudokuViewModel.setCurrentPosition(index)
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row {
                Text(
                    text = uiState.status,
                    modifier = Modifier
                        .padding(10.dp),
                    fontSize = 24.sp
                )
            }
            Row {
                if (uiState.cellValues.isNotEmpty()) Game(uiState, listener)
            }
        }
    }
}

@Composable
fun Game(uiState: SudokuUIState, gameListener: GameListener) {

    val configuration = LocalConfiguration.current
    val gameWidth = configuration.screenWidthDp.dp
    val cellSize = gameWidth / 9

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (x in 1..9) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (y in 1..9) {
                        val index = (x - 1) * 9 + (y - 1)
                        GameCell(cellSize, uiState, index, gameListener)
                    }
                }
            }
        }
        Column {
            for (x in 1..3) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (y in 1..3) {
                        Box(
                            modifier = Modifier
                                .width(cellSize * 3)
                                .height(cellSize * 3)
                                .border(2.dp, MaterialTheme.colors.primary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(cellSize: Dp, uiState: SudokuUIState, index: Int, gameListener: GameListener) {

    val cellValue = uiState.cellValues[index] ?: return

    fun associated(index: Int, cell: CellValue): Boolean {
        cell.restrictions.forEach { rest ->
            rest.associatedIndex.forEach {
                if (it == index) {
                    return true
                }
            }
        }
        return false
    }

    val focusCell = uiState.cellValues[uiState.currentPosition]!!
    Box(
        modifier = Modifier
            .width(cellSize)
            .height(cellSize)
            .background(
                if ( cellValue.validValues.isEmpty()) {
                    Color.Yellow
                }
                else {
                    if (uiState.currentPosition == index) {
                        Color.Red
                    }
                    else {
                        if (associated(index, focusCell)) {
                                Color.White
                        }
                        else {
                            Color.LightGray
                        }
                    }
                }
            )
            .border(1.dp, MaterialTheme.colors.primary)
    ) {

        BasicTextField(
            value = if (cellValue.value == 0) {
                ""
            } else {
                cellValue.value.toString()
            },
            onValueChange = {
                gameListener.onValueChange(cellValue.index, it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .align(Alignment.Center)
                .onFocusChanged { gameListener.onFocus(cellValue.index) },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize =
                if (uiState.currentPosition == index)
                    26.sp else {
                    if (associated(index, focusCell)) {
                        24.sp
                    } else {
                        20.sp
                    }
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultSudokuPreview() {
    SudokuTheme {
        val listener = object : GameListener {
            override fun onValueChange(index: Int, value: String) {
            }

            override fun onFocus(index: Int) {
            }

        }
        val cellValues = Collections.nCopies(81, 0)
        //Game(cellValues, listener)
    }
}

