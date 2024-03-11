package com.example.sensorviewapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.ui.screens.viewmodel.DataVisualizationUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomsViewModel



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.sensorviewapp.model.Comfort

enum class ComfortIndicator(val emoji: String) {
    GOOD("\uD83D\uDE00"),
    NORMAL("\uD83D\uDE42"),
    BAD("\uD83D\uDE16");

    fun switchComfort(comfort: String) {
        when (comfort) {
            "good" -> GOOD.emoji
            "normal" -> NORMAL.emoji
            "bad" -> BAD.emoji
        }
    }
}

@Composable
fun DataVisualizationScreen(
    modifier: Modifier,
    navController: NavController,
    retryAction: () -> Unit,
    dataVisualizationUiState: DataVisualizationUiState
) {
    when (dataVisualizationUiState) {
        is DataVisualizationUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is DataVisualizationUiState.Success -> RoomsScreen(
            dataVisualizationUiState.rooms,
            dataVisualizationUiState.comforts,
            navController = navController,
            modifier.fillMaxSize(),
        )
        is DataVisualizationUiState.Error -> ErrorScreen( retryAction, modifier.fillMaxSize())
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    /*Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )*/
    Text("Load")
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }*/
        Text("Error")
    }
}

@Composable
fun RoomsScreen(
    rooms: List<Room>,
    comforts: List<Comfort>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(4.dp)
    ) {

        items(items = rooms, key = { room -> room.name }) { room ->
            var indicator: Comfort? = null
            comforts.forEach { comfort ->
                if (comfort.roomName == room.name) {
                    indicator = comfort
                }
            }

            RoomCard(navController = navController, room = room, indicator = indicator)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    room: Room,
    indicator: Comfort?
) {
    Card(
        modifier = Modifier
            .size(width = 200.dp, height = 140.dp)
            .padding(8.dp)
            .padding(horizontal = 32.dp),
        onClick = { navController.navigate("Data Visualization/" + room.name) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = room.name,
                fontSize = 32.sp
            )
            // Smiley Bonne Temperature
            Text(
                text =
                    if (indicator != null) {
                        when (indicator.comfort) {
                            "good" -> ComfortIndicator.GOOD.emoji
                            "normal" -> ComfortIndicator.NORMAL.emoji
                            "bad" -> ComfortIndicator.BAD.emoji
                            else -> ""
                        }
                    } else {
                           "Indicator not available"
                   },
                fontSize = 48.sp,
            )
        }
    }
}