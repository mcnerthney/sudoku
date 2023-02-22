package co.tapcart.sudoku.ui.composables

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.tapcart.sudoku.R
import co.tapcart.sudoku.ui.theme.SudokuTheme


@Composable
fun App() {

    SudokuTheme {

        val navController = rememberNavController()

        Scaffold(
            topBar = {
                TopAppBar {
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                NavHost(navController = navController, startDestination = "sudoku") {
                    composable("sudoku") {
                        SudokuScreen(navController)
                    }
                }
            }
        }
    }
}
