package co.softllc.sudoku

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.softllc.sudoku.data.models.GameEntity
import co.softllc.sudoku.data.models.GameMoveEntity
import co.softllc.sudoku.data.models.IntTypeConverter
import co.softllc.sudoku.data.repositories.GamesRepository
import co.softllc.sudoku.ui.composables.App
import java.util.prefs.Preferences

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
class MainActivity() : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GamesRepository(this)
        setContent {
            App()
        }
    }
}


