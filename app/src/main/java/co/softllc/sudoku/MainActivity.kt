package co.softllc.sudoku

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import co.softllc.sudoku.data.db.GameRoomDatabase
import co.softllc.sudoku.ui.composables.App

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
class MainActivity() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameRoomDatabase.getDatabase(this)
        setContent {
            App()
        }
    }
}
