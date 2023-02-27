package co.softllc.sudoku.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [GameEntity::class, GameMoveEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IntTypeConverter::class)
abstract class GameRoomDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: GameRoomDatabase? = null

        fun getDatabase(context: Context? = null): GameRoomDatabase {
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