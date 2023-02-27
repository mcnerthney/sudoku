package co.softllc.sudoku.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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