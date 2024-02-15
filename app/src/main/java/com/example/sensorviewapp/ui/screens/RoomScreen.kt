package com.example.sensorviewapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.ui.screens.viewmodel.DataVisualizationUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

@Composable
fun RoomScreen(
    navController: NavController,
    roomName: String,
    retryAction: () -> Unit,
    roomScreenViewModel: RoomScreenViewModel,
    roomScreenUiState: RoomScreenUiState,
    modifier: Modifier = Modifier
) {
    when (roomScreenUiState) {
        is RoomScreenUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is RoomScreenUiState.Success -> Dashboard(
            roomScreenViewModel,
            roomScreenUiState.sensors,
            navController = navController,
            modifier.fillMaxSize(),
        )
        is RoomScreenUiState.Error -> ErrorScreen(retryAction, modifier.fillMaxSize())
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    roomScreenViewModel: RoomScreenViewModel,
    sensors: List<Sensor>,
    navController: NavController,
    modifier: Modifier
    ){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(sensors[0]) }
    var lastValue by remember {mutableStateOf<Measure?>(null)}

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sensors.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.uom) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.Default).launch {
                                lastValue = roomScreenViewModel.getLastValue(GetLastValue(selectedText.name, selectedText.uom))
                                Log.v("log value", lastValue.toString())
                            }
                        }
                    )
                }
            }
        }
    }
    Text(lastValue?.value.toString())
}