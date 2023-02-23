package co.softllc.sudoku.data.viewmodels

import androidx.lifecycle.ViewModel
import co.softllc.sudoku.data.helpers.CellRestrictionHelper
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.data.state.CellValue
import co.softllc.sudoku.data.state.SudokuUIState
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SudokuViewModel(
    private val gamesRepository: GamesRepository = GamesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUIState())
    val uiState: StateFlow<SudokuUIState> = _uiState


    fun getGame(gameId: String?) {
        _uiState.update { currentUiState ->
            val game = gamesRepository.getGame(gameId)
            val cellValues = mutableMapOf<Int, CellValue>()
            if (currentUiState.cellValues.isEmpty()) {
                val initValues = game?.start ?: Collections.nCopies(81, 0)
                initValues.forEachIndexed { index, value ->
                    val restrictions = listOf(
                        CellRestrictionHelper.getRowRestriction(index),
                        CellRestrictionHelper.getColumnRestriction(index),
                        CellRestrictionHelper.getSectionRestriction(index)
                    )
                    cellValues[index] = CellValue(index, value, restrictions)
                }
            }
            currentUiState.copy(
                cellValues = currentUiState.cellValues.ifEmpty {
                    cellValues
                }
            )
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
                val updatedCells = currentUiState.cellValues.toMutableMap().apply {
                    this[index] = this[index]!!.copy(value = value)
                }
                currentUiState.copy(
                    cellValues = updatedCells,
                    status = buildStatus(pos, currentUiState)
                )
            } else {
                currentUiState.copy(
                    status = buildStatus(pos, currentUiState, "\n$valueInput invalid")
                )
            }
        }
    }

    val allValues = (1..9).toList()
    private fun buildStatus(pos: Int, uiState: SudokuUIState, note: String = ""): String {
        val cell = uiState.cellValues[pos]!!
        val usedValues = mutableSetOf<Int>()
        cell.restrictions.forEach { it.associatedIndex.forEach { index -> usedValues.add(uiState.cellValues[index]!!.value) } }
        val remainingValues = allValues.mapNotNull { if (usedValues.contains(it)) null else it }
        return "Valid Values $remainingValues $note"
    }

}


