package co.softllc.sudoku.data.state

import co.softllc.sudoku.data.models.Game

data class SudokuUIState(
    val game: Game? = null,
    val status : String = "",
    val note : String = "",
    val cellValues: Map<Int, CellValue> = emptyMap(),
    val currentPosition: Int? = null
)

data class CellValue(
    val index : Int,
    val value : Int = 0,
    val restrictions: List<CellValueRestriction> = emptyList(),
    val validValues: List<Int> = emptyList()
)

data class CellValueRestriction(
    val associatedIndex: List<Int> = emptyList(),
)