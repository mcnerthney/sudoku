package co.tapcart.sudoku.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.tapcart.sudoku.data.models.Game
import java.util.prefs.Preferences

object GamesRepository{


    private var games: List<Game> = emptyList()
    suspend fun getGames(context: Context): List<Game> {
        return emptyList()
    }

    fun getGame(gameId: String?): Game? {
        return null
//        return products.find {
//            it.id == productId
//        }
    }


}
