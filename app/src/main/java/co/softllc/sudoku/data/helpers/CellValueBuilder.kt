package co.softllc.sudoku.data.helpers

import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.state.CellValue

object CellValueBuilder {
    fun fromGame(game: Game): Map<Int, CellValue> {
        val allValues = (1..9).toList()
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

        return cellValues.map { mapEntry ->
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
    }
}
