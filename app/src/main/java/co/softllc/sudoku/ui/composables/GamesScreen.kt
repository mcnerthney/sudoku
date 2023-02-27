package co.softllc.sudoku.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    gamesViewModel: GamesViewModel = viewModel()

    ) {

    val uiGamesState = gamesViewModel.games.collectAsState(initial = GamesUIState())

    LazyColumn {
        items(uiGamesState.value.games) { game ->
            GameRow(navController, game)
        }
    }

}

@Composable
fun GameRow(
    navController: NavController,
    game: Game
) {

    Row(
        modifier = Modifier.clickable {
            navController.navigate("sudoku/${game.id}")
        }.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(game.name)
            Text(game.id)
        }
    }

    Divider(color = Color.LightGray)
}