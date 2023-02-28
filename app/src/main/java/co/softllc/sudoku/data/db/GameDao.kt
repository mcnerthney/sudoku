package co.softllc.sudoku.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: GameEntity)

    @Query("SELECT * FROM GameEntity WHERE id = :id LIMIT 1")
    fun getGameById(id: String): Flow<GameEntity?>

    @Query("SELECT * FROM GameEntity")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("DELETE FROM GameEntity WHERE id = :id")
    suspend fun delete(id: String)
}
