package co.softllc.sudoku.data.state

import co.softllc.sudoku.data.models.Game

data class GamesUIState(
    val games: List<Game> = emptyList()
)

