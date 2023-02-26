package co.softllc.sudoku.data.repositories

import android.content.Context
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.models.GameEntity
import java.util.*


class GamesRepository(context: Context? = null)
{
    private val db = co.softllc.sudoku.data.models.GameRoomDatabase.getDatabase(context)

    suspend fun getGames(context: Context): List<Game> {
        return db.gameDao().getAll().map {
            Game(it.id, it.name, it.start)
        }
    }

    suspend fun getGame(id: String): Game {
        return db.gameDao().getGameById(id)?.let {
            Game(it.id, it.name, it.start)
        } ?: Game(id, "new game", Collections.nCopies(81, 0))
    }

    suspend fun saveGame(game: Game) {
        val saveGame = GameEntity(game.id,  game.start, game.name)
        db.gameDao().insert(saveGame)
    }

}
