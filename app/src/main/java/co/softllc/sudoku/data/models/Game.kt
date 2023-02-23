package co.softllc.sudoku.data.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

data class Game (
    val start: List<Int>,
    val move: List<GameMove>
    )

data class GameMove(
    val time: Date,
    val value: Int
)

//@Dao
//interface GameDao {
//    @Insert
//    fun insertAll(vararg users: User)
//
//    @Delete
//    fun delete(user: User)
//
//    @Query("SELECT * FROM user")
//    fun getAll(): List<User>
//}