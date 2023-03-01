package co.softllc.sudoku.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.GamesUIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class GamesViewModel(
    private val gamesRepository: GamesRepository = GamesRepository()
) : ViewModel() {
    val games: Flow<GamesUIState> = gamesRepository.getGames().map { games ->
        GamesUIState(games.map { Game(it.id, it.name, it.start) }.sortedBy { it.name.lowercase() })
    }

    fun newGame(onCreated: ((gameId: String) -> Unit)? = null): String {
        val game = Game(
            UUID.randomUUID().toString(),
            "New Game",
            Collections.nCopies(81, 0)
        )
        viewModelScope.launch {
            gamesRepository.saveGame(game)
            onCreated?.invoke(game.id)
        }
        return game.id
    }
}
