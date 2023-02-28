package co.softllc.sudoku.ui.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.softllc.sudoku.R
import co.softllc.sudoku.ui.theme.SudokuTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import java.util.*


@Composable
fun App() {

    SudokuTheme {

        val navController = rememberNavController()

        Scaffold(
            topBar = {
                TopAppBar{
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize().padding(padding),
                color = MaterialTheme.colors.background
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("sudoku/{id}") {backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("id") ?: "djm"
                        SudokuScreen(navController,gameId)
                    }
                    composable("home") {
                        GamesScreen(navController)
                    }
                }
            }
        }
    }
}
