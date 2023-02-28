package co.softllc.sudoku.ui.composables

import android.util.Log
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import co.softllc.sudoku.data.helpers.CellValueBuilder
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.SudokuUIState
import co.softllc.sudoku.data.viewmodels.SudokuViewModel
import co.softllc.sudoku.ui.theme.SudokuTheme
import java.util.*

interface GameListener {
    fun onValueChange(index: Int, value: String)
    fun onFocus(index: Int)
    fun onNameChange(value: String)

    fun onDeleteGame()
}

@Composable
fun SudokuScreen(
    navController: NavController,
    gameId: String,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val uiState by sudokuViewModel.uiState.collectAsState()

    sudokuViewModel.loadGame(gameId)

    val listener = object : GameListener {
        override fun onValueChange(index: Int, value: String) {
            sudokuViewModel.setValue(index, value)
        }

        override fun onFocus(index: Int) {
            sudokuViewModel.setCurrentPosition(index)
        }

        override fun onNameChange(value: String) {
            sudokuViewModel.setName(value)
        }

        override fun onDeleteGame() {
            sudokuViewModel.deleteGame(navController)
        }
    }
    GameBoard(uiState = uiState, listener = listener)
}

@Composable
fun GameBoard(uiState: SudokuUIState, listener: GameListener) {
    val openDeleteDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row {
                Text(
                    text = uiState.status + uiState.note,
                    modifier = Modifier
                        .padding(10.dp),
                    fontSize = 24.sp
                )
            }
            Row {
                if (uiState.cellValues.isNotEmpty()) Game(uiState, listener)
            }
            Row {
                val name: String = uiState.game?.name.orEmpty()
                BasicTextField(
                    value = name,
                    onValueChange = {
                        listener.onNameChange(it)
                    },
                    modifier = Modifier
                        .padding(10.dp),
                    textStyle = TextStyle.Default.copy(
                        fontSize = 26.sp
                    )
                )
            }
            Row {
                IconButton(
                    onClick = {
                        openDeleteDialog.value = true
                    }
                ) {
                    Icon(Icons.Rounded.Delete, "Remove Game")
                }
            }
        }
    }

    if (openDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDeleteDialog.value = false
            },
            title = {
                Text(text = "Delete Game")
            },
            text = {
                Text("Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        listener.onDeleteGame()
                    }
                ) {
                    Text("DELETE")
                }
            }
        )
    }
}

@Composable
fun Game(uiState: SudokuUIState, gameListener: GameListener) {
    val configuration = LocalConfiguration.current
    val gameWidth = configuration.screenWidthDp.dp
    val cellSize = gameWidth / 9

    Log.d("djm", "Current Position ${uiState.currentPosition}")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Divider(color = MaterialTheme.colors.primary, thickness = 5.dp)
        for (x in 1..9) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (y in 1..9) {
                    val index = (x - 1) * 9 + (y - 1)
                    GameCell(cellSize, uiState, index, gameListener)
                    if (y / 3 * 3 == y) {
                        Divider(
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .height(cellSize)
                                .width(5.dp)
                        )
                    }
                }
            }
            if (x / 3 * 3 == x) {
                Divider(color = MaterialTheme.colors.primary, thickness = 5.dp)
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

@Composable
fun GameCell(
    cellSize: Dp,
    uiState: SudokuUIState,
    index: Int,
    gameListener: GameListener
) {
    val cellValue = uiState.cellValues[index] ?: return

    fun associated(index: Int, cell: CellValue?): Boolean {
        cell?.restrictions?.forEach { rest ->
            rest.associatedIndex.forEach {
                if (it == index) {
                    return true
                }
            }
        }
        return false
    }

    val focusCell = uiState.cellValues[uiState.currentPosition]
    Box(
        modifier = Modifier
            .width(cellSize)
            .height(cellSize)
            .background(
                if (cellValue.validValues.isEmpty()) {
                    Color.Yellow
                } else {
                    if (uiState.currentPosition == index) {
                        Color.Red
                    } else {
                        if (focusCell == null || associated(index, focusCell)) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    }
                }
            )
            .border(1.dp, MaterialTheme.colors.primary)
    ) {
        val modifier = Modifier
            .align(Alignment.Center)
            .onFocusChanged { focusState ->
                if (focusState.hasFocus) {
                    gameListener.onFocus(cellValue.index)
                }
            }
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
            modifier = modifier,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize =
                if (uiState.currentPosition == index) {
                    26.sp
                } else {
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
    val game = Game(
        UUID.randomUUID().toString(),
        "New Game",
        Collections.nCopies(81, 3)
    )

    val uiState = SudokuUIState(
        game,
        "status",
        "note",
        CellValueBuilder.fromGame(game).associateBy { it.index },
        10
    )

    val listener = object : GameListener {
        override fun onValueChange(index: Int, value: String) {
        }
        override fun onFocus(index: Int) {
        }
        override fun onNameChange(value: String) {
        }
        override fun onDeleteGame() {
        }
    }

    SudokuTheme {
        GameBoard(uiState = uiState, listener = listener)
    }
}
