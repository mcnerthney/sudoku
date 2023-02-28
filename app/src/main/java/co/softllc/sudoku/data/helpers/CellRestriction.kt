package co.softllc.sudoku.data.helpers

import co.softllc.sudoku.data.state.CellValueRestriction

object CellRestrictionHelper {
    private fun indexToXY (index: Int) : Pair<Int,Int> {
        val x = index / 9
        val y = index % 9
        return Pair(x,y)
    }
    fun getRowRestriction(index: Int): CellValueRestriction {
        val xy = indexToXY(index)
        val values = mutableListOf<Int>()
        for (y in 0..8) {
            val i = xy.first * 9 + y
            if ( i != index ) values.add(i)
        }
        return CellValueRestriction(values)
    }
    fun getColumnRestriction(index: Int): CellValueRestriction {
        val xy = indexToXY(index)
        val values = mutableListOf<Int>()
        for (x in 0..8) {
            val i = x * 9 + xy.second
            if ( i != index ) values.add(i)
        }
        return CellValueRestriction(values)
    }

    fun getSectionRestriction(index: Int): CellValueRestriction {
        val section = sectionsIndex.find { it.contains(index) }!!
        return CellValueRestriction(section.mapNotNull { if ( it != index ) it else null })
    }


    private val sectionsIndex = buildSections()

    private fun buildSections(): List<Set<Int>> {
        val sections = mutableListOf<Set<Int>>()
        for (i in 1..3) sections.addAll(buildSectionRow(i))
        return sections
    }

    private fun buildSectionRow(i: Int): List<Set<Int>> {
        val offset = (i - 1) * 27 - 1
        val sectionIndex = listOf(
            listOf(1, 2, 3, 10, 11, 12, 19, 20, 21),
            listOf(4, 5, 6, 13, 14, 15, 22, 23, 24),
            listOf(7, 8, 9, 16, 17, 18, 25, 26, 27)
        )
        return sectionIndex.map {
            it.map { it + offset }.toSet()
        }
    }

}

