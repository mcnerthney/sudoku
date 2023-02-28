package co.softllc.sudoku.data.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softllc.sudoku.data.helpers.CellRestrictionHelper
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.GamesUIState
import co.softllc.sudoku.data.state.SudokuUIState
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GamesViewModel(
    private val gamesRepository: GamesRepository = GamesRepository()
) : ViewModel() {
    val games : Flow<GamesUIState> = gamesRepository.getGames().map {
        GamesUIState(it.map { Game(it.id, it.name ,it.start)})
    }

    fun newGame() : String {
        val game = Game(
            UUID.randomUUID().toString(),
            "New Game",
            Collections.nCopies(81, 0))
        viewModelScope.launch {
            gamesRepository.saveGame(game)
        }
        return game.id
    }
}


