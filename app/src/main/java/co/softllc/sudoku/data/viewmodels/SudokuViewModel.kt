package co.softllc.sudoku.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import co.softllc.sudoku.data.helpers.CellValueBuilder
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.SudokuUIState
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SudokuViewModel(
    private var gameId : String = "",
    private val gamesRepository: GamesRepository = GamesRepository(),
    private val backgroundJob: BackgroundJob = BackgroundJob ()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUIState())
    private var gameIdjob : Job? = null

    val uiState: StateFlow<SudokuUIState> = _uiState

    fun loadGame(gameId: String) {
        Log.d("djm", "load game $gameId")
        if ( gameId == this.gameId ) return
        this.gameId = gameId

         gameIdjob?.cancel()
         gameIdjob = viewModelScope.launch {
            gamesRepository.getGame(gameId).collect { game ->
                Log.d("djm", "updated game $game")
                _uiState.update { currentUiState ->
                    val cellValueMap =
                        game?.let {
                            CellValueBuilder.fromGame(game).associateBy { it.index }
                        }.orEmpty()

                    currentUiState.copy(
                        game = game,
                        cellValues = cellValueMap
                    )
                }
            }
        }
    }

    fun setCurrentPosition(index: Int) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                currentPosition = index,
                status = buildStatus(index, currentUiState)
            )
        }
    }

    var nameJob : Job? = null
    fun setName(value: String) {
        val game = uiState.value.game?.copy(name = value) ?: return
        _uiState.update { uiState ->
            uiState.copy(game = game)
        }
        nameJob?.cancel()
        nameJob = backgroundJob.launch {
            gamesRepository.saveGame(
                game
            )
        }
    }

    fun setValue(index: Int, valueInput: String) {
            val valueInt = valueInput.toIntOrNull()
            val value = (valueInt ?: 0) % 10
            val cell = uiState.value.cellValues[index]!!
            if (cell.validValues.contains(value)) {
                setCellValue(uiState.value, index, value, "")
            } else {
                setCellValue(uiState.value, index, 0, "\n$valueInput invalid")
            }
        }

    private fun setCellValue(uiState: SudokuUIState, index: Int, value: Int, note: String) {
        val game = uiState.game ?: return

        val updatedValues = uiState.cellValues.values.map { cellValue ->
            if ( cellValue.index == index) {
                value
            }
            else {
                cellValue.value
            }
        }

        val updatedGame = game.copy(
            start = updatedValues
        )
        _uiState.update {
            it.copy(
                game = updatedGame,
                note = note
            )
        }
        backgroundJob.launch {
            gamesRepository.saveGame(
                updatedGame
            )
        }
    }

    private fun buildStatus(pos: Int, uiState: SudokuUIState, note: String = ""): String {
        val cell = uiState.cellValues[pos]!!
        return "Valid Values ${cell.validValues} $note"
    }

    fun deleteGame(navController: NavController) {
        viewModelScope.launch {
            gamesRepository.deleteGame(gameId)
            navController.popBackStack()
        }
    }

}


class BackgroundJob(private val defaultContext: CoroutineContext = Dispatchers.IO) : CoroutineScope {
    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = defaultContext + job
    fun cancelChildren() {
        job.cancelChildren()
    }
}
