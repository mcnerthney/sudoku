package com.example.grids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grids.ui.theme.GridsTheme
import java.util.*

class GameState {
    val ints : MutableState<List<Int>> = mutableStateOf(Collections.nCopies(81, 0))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GridsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state = remember { GameState() }
                    Game()
                }
            }
        }
    }
}



@Composable
fun Game() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cellSize = screenWidth/9

    Column(horizontalAlignment = CenterHorizontally) {
        for ( x in 1..9 ){
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                for ( y in 1..9 ) {
                    GameCell(cellSize)
                }
            }
        }
    }
    Column {
        for ( x in 1..3 ){
            Row( horizontalArrangement = Arrangement.SpaceEvenly) {
                for ( y in 1..3 ) {
                    Box(modifier = Modifier
                        .width(cellSize*3)
                        .height(cellSize*3)
                        .border(3.dp, Color.Black))
                }
            }
        }
    }
}

@Composable
fun GameCell(cellSize: Dp) {

    Box(modifier = Modifier
        .width(cellSize)
        .height(cellSize)
        .border(1.dp, Color.Black)
    ) {
        BasicTextField(
            value = "0",
            onValueChange = {
                //val values = state.ints.value.toMutableList()
                //values[x*9+y] = it.toInt()
                //state.ints.value = values
             },
            singleLine = true,
            modifier = Modifier
                .align(Alignment.Center),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GridsTheme {
        Game()
    }
}

