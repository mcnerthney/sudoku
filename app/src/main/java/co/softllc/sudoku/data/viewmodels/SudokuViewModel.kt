package co.softllc.sudoku.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softllc.sudoku.data.helpers.CellRestrictionHelper
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.SudokuUIState
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SudokuViewModel(
    private val gamesRepository: GamesRepository = GamesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUIState())
    val uiState: StateFlow<SudokuUIState> = _uiState

    fun getGame(gameId: String) {
        viewModelScope.launch {
            val game = gamesRepository.getGame(gameId)
            _uiState.update { currentUiState ->
                val cellValues = mutableMapOf<Int, CellValue>()
                if (currentUiState.cellValues.isEmpty()) {
                    val cells = game.start
                    cells.forEachIndexed { index, value ->
                        val restrictions = listOf(
                            CellRestrictionHelper.getRowRestriction(index),
                            CellRestrictionHelper.getColumnRestriction(index),
                            CellRestrictionHelper.getSectionRestriction(index)
                        )
                        cellValues[index] = CellValue(index, value, restrictions)
                    }
                }
                currentUiState.copy(
                    game = game,
                    cellValues = cellValues
                )
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
        _uiState.update { currentUiState ->
            val valueInt = valueInput.toIntOrNull()
            val value = (valueInt ?: 0) % 10
            val pos = index
            val cell = currentUiState.cellValues[pos]!!
            val usedValues = mutableSetOf<Int>()
            cell.restrictions.forEach {
                it.associatedIndex.forEach { index ->
                    usedValues.add(
                        currentUiState.cellValues[index]!!.value
                    )
                }
            }
            val remainingValues = allValues.mapNotNull { if (usedValues.contains(it)) null else it }
            if (remainingValues.contains(value)) {
                setCellValue(currentUiState, index, value, "")
            } else {
                setCellValue(currentUiState, index, 0, "\n$valueInput invalid")
            }

        }
        saveGame()
    }

    fun saveGame() {
        val theGame = uiState.value.game ?: return

        viewModelScope.launch {
            gamesRepository.saveGame(
                theGame.copy(
                    start = uiState.value.cellValues.map { it.value.value }
                )
            )
        }
    }
    private fun setCellValue(uiState: SudokuUIState, index: Int, value: Int, note: String) : SudokuUIState {
        val updatedCells = uiState.cellValues.toMutableMap().apply {
            this[index] = this[index]!!.copy(value = value)
        }
        return uiState.copy(
            cellValues = updatedCells,
            status = buildStatus(index, uiState)
        )

    }
    private val allValues = (1..9).toList()
    private fun buildStatus(pos: Int, uiState: SudokuUIState, note: String = ""): String {
        val cell = uiState.cellValues[pos]!!
        val usedValues = mutableSetOf<Int>()
        cell.restrictions.forEach { it.associatedIndex.forEach { index -> usedValues.add(uiState.cellValues[index]!!.value) } }
        val remainingValues = allValues.mapNotNull { if (usedValues.contains(it)) null else it }
        return "Valid Values $remainingValues $note"
    }

}


