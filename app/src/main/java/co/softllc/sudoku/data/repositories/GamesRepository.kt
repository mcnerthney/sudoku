package co.softllc.sudoku.data.repositories

import android.content.Context
import co.softllc.sudoku.data.db.GameRoomDatabase
import co.softllc.sudoku.data.models.Game
import co.softllc.sudoku.data.db.GameEntity
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull


class GamesRepository()
{
    private val db = GameRoomDatabase.getDatabase()

    fun getGame(id: String): Flow<Game?> {
        return db.gameDao().getGameById(id).mapNotNull {
            it?.let {
                Game(it.id, it.name, it.start)
            }
        }
    }

    suspend fun saveGame(game: Game) {
        val saveGame = GameEntity(game.id,  game.start, game.name)
        db.gameDao().insert(saveGame)
    }

    fun getGames(): Flow<List<Game>> {
        return db.gameDao().getAllGames().mapNotNull {
            it.map { Game(it.id, it.name, it.start) }
        }
    }

    suspend fun deleteGame(gameId: String) {
        db.gameDao().delete(gameId)
    }

}
