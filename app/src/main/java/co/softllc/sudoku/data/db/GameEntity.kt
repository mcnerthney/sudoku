package co.softllc.sudoku.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class GameEntity(
    @PrimaryKey val id: String,
    @field:TypeConverters(IntTypeConverter::class)
    val start: List<Int>,
    val name: String
)
