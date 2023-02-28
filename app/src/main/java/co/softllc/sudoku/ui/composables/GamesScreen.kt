package co.softllc.sudoku.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.state.GamesUIState
import co.softllc.sudoku.data.viewmodels.GamesViewModel


@Composable
fun GamesScreen(
    navController: NavController,
    gamesViewModel: GamesViewModel = viewModel()) {

    val uiGamesState = gamesViewModel.games.collectAsState(initial = GamesUIState())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val gameId = gamesViewModel.newGame()
                navController.navigate("sudoku/$gameId")
            }) {
                Icon(Icons.Filled.Add,"Create New Game")
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn {
                items(uiGamesState.value.games) { game ->
                    GameRow(navController, game)
                }
            }
        }
    }
}

@Composable
fun GameRow(
    navController: NavController,
    game: Game
) {

     Row(
        modifier = Modifier
            .clickable {
                navController.navigate("sudoku/${game.id}")
            }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text ="${game.name} : ${game.id.takeLast(4)}"
            )
        }

    }
    //SudokuScreen(gameId = game.id, navController = navController)

    Divider(color = Color.LightGray)

}