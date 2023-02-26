package co.softllc.sudoku.data.models

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlinx.coroutines.flow.Flow

data class Game (
    val id: String,
    val name: String,
    val start: List<Int>
    )
class IntTypeConverter {
    @TypeConverter
    fun saveIntList(list: List<Int>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getIntList(list: String): List<Int> {
        return Gson().fromJson(
            list,
            object : TypeToken<List<Int>>() {}.type
        )
    }
}

@Entity
data class GameEntity(
    @PrimaryKey val id: String,
    @field:TypeConverters(IntTypeConverter::class)
    val start: List<Int>,
    val name: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gameId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameMoveEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val gameId: String,
    val time: Long,
    val value: Int
)

@Dao
interface GameDao {
    @Insert
    fun insertAll(vararg games: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: GameEntity)

    @Delete
    fun delete(game: GameEntity)

    @Query("SELECT * FROM GameEntity")
    fun getAll(): List<GameEntity>

    @Query("SELECT * FROM GameEntity WHERE id = :id LIMIT 1")
    suspend fun getGameById(id: String): GameEntity?
}



// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [GameEntity::class, GameMoveEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IntTypeConverter::class)
public abstract class GameRoomDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: GameRoomDatabase? = null

        fun getDatabase(context: Context?): GameRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    GameRoomDatabase::class.java,
                    "sudoku_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
