package co.softllc.sudoku.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softllc.sudoku.data.helpers.CellRestrictionHelper
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.SudokuUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SudokuViewModel(
    private val gamesRepository: GamesRepository = GamesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUIState())
    var job : Job? = null
    val uiState: StateFlow<SudokuUIState> = _uiState

    fun setGame(gameId: String) {
         Log.d("djm", "get game $gameId")
         job?.cancel()
         job = viewModelScope.launch {
            gamesRepository.getGame(gameId).collect { game ->
                Log.d("djm", "updated game $game")
                _uiState.update { currentUiState ->
                    val cellValues = mutableMapOf<Int, CellValue>()
                    val cells = game.start
                    cells.forEachIndexed { index, value ->
                        val restrictions = listOf(
                            CellRestrictionHelper.getRowRestriction(index),
                            CellRestrictionHelper.getColumnRestriction(index),
                            CellRestrictionHelper.getSectionRestriction(index)
                        )
                        cellValues[index] = CellValue(index, value, restrictions)
                    }

                    //}
                    val c2 = cellValues.map { mapEntry ->
                        val usedValues = mutableSetOf<Int>()
                        mapEntry.value.restrictions.forEach {
                            it.associatedIndex.forEach { index ->
                                usedValues.add(
                                    cellValues[index]!!.value
                                )
                            }
                        }
                        val remainingValues =
                            allValues.mapNotNull { if (usedValues.contains(it)) null else it }
                        mapEntry.value.copy(validValues = remainingValues)
                    }.associateBy { it.index }

                    currentUiState.copy(
                        game = game,
                        cellValues = c2
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

    fun setValue(index: Int, valueInput: String) {
        //_uiState.update { currentUiState ->
            val valueInt = valueInput.toIntOrNull()
            val value = (valueInt ?: 0) % 10
            val pos = index
            val cell = uiState.value.cellValues[pos]!!
            val usedValues = mutableSetOf<Int>()
            cell.restrictions.forEach {
                it.associatedIndex.forEach { index ->
                    usedValues.add(
                        uiState.value.cellValues[index]!!.value
                    )
                }
            }
            val remainingValues = allValues.mapNotNull { if (usedValues.contains(it)) null else it }
            if (remainingValues.contains(value)) {
                setCellValue(uiState.value, index, value, "")
            } else {
                setCellValue(uiState.value, index, 0, "\n$valueInput invalid")
            }

        }
      //  saveStart()

    private fun setCellValue(uiState: SudokuUIState, index: Int, value: Int, note: String) {
        val updatedCells = uiState.cellValues.toMutableMap().apply {
            this[index] = this[index]!!.copy(value = value)
        }
        viewModelScope.launch {
            gamesRepository.saveGame(
                uiState.game!!.copy(
                    start = updatedCells.map { it.value.value }
                )
            )
        }
    }
    private val allValues = (1..9).toList()
    private fun buildStatus(pos: Int, uiState: SudokuUIState, note: String = ""): String {
        val cell = uiState.cellValues[pos]!!
        return "Valid Values ${cell.validValues} $note"
    }

}


