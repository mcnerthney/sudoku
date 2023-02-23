package co.softllc.sudoku.data.state

data class SudokuUIState(
    val status : String = "",
    val cellValues: Map<Int, CellValue> = emptyMap(),
    val currentPosition: Int = 0
)

data class CellValue(
    val index : Int,
    val value : Int = 0,
    val restrictions: List<CellValueRestriction> = emptyList()
)

data class CellValueRestriction(
    val associatedIndex: List<Int> = emptyList(),
)