package com.example.sensorviewapp.ui.screens

import PredictionUiState
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.ui.screens.viewmodel.PredictionScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.PredictionScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomUiState
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class SensorsAvailable(val value: String, val friendlyName: String){
    TMPD251_1(value="d251_1_co2_air_temperature", friendlyName="D251 TEMP 1"),
    TMPD251_2(value="d251_1_multisensor_air_temperature", friendlyName="D251 TEMP 2"),

    TMPD351_1(value="d351_1_co2_air_temperature", friendlyName="D351 TEMP 1"),
    TMPD351_2(value="d351_1_multisensor9_air_temperature", friendlyName="D351 TEMP 2"),
    TMPD351_3(value="d351_1_multisensor_air_temperature", friendlyName="D351 TEMP 3"),
    TMPD351_4(value="d351_2_co2_air_temperature", friendlyName="D351 TEMP 4"),
    TMPD351_5(value="d351_2_multisensor_air_temperature", friendlyName="D351 TEMP 5"),
    TMPD351_6(value="d351_3_co2_air_temperature", friendlyName="D351 TEMP 6"),

    TMPD360_1(value="d360_1_co2_air_temperature", friendlyName="D360 TEMP 1"),
    TMPD360_2(value="d360_1_multisensor_air_temperature_2", friendlyName="D360 TEMP 2"),

}

@Composable
fun PredictionScreen(
    navController: NavController,
    retryAction: () -> Unit,
    predictionScreenViewModel: PredictionScreenViewModel,
    modifier: Modifier = Modifier,
    scrollState: ScrollState
) {
    val predictionUiState by predictionScreenViewModel.uiState.collectAsState()
    when (predictionScreenViewModel.predictionScreenUiState) {
        is PredictionScreenUiState.Loading -> LoadingPrediction(modifier.fillMaxSize())
        is PredictionScreenUiState.Success -> Main(
            predictionUiState,
            predictionScreenViewModel,
            navController = navController,
            modifier.fillMaxSize(),
            scrollState = scrollState
        )
        is PredictionScreenUiState.Error -> ErrorPrediction(modifier.fillMaxSize(), retryAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    predictionUiState: PredictionUiState,
    predictionScreenViewModel: PredictionScreenViewModel,
    navController: NavController,
    modifier: Modifier,
    scrollState: ScrollState
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(SensorsAvailable.TMPD251_1) }
    val modelProducer = remember{ CartesianChartModelProducer.build() }
    var counter = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedItem.friendlyName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SensorsAvailable.entries.forEach() { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.friendlyName) },
                            onClick = {
                                selectedItem = item
                                expanded = false
                                Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                                CoroutineScope(Dispatchers.Default).launch {
                                    predictionUiState.selectedSensor = item.value
                                }
                            }
                        )
                    }
                }
            }
        }
        LaunchedEffect(predictionUiState.predictions) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    val data: MutableList<Double> = mutableListOf()
                    predictionUiState.predictions?.forEach() {
                        data.add(it.value)
                    }
                    series(data)
                }
            }
        }
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            modelProducer,
        )
        predictionUiState.predictions?.forEach {
            Text(text = "Prediction h+" + counter.toString() + " : " + it.value)
            counter += 1
        }
    }
}

@Composable
fun ErrorPrediction(
    modifier: Modifier,
    retryAction: () -> Unit
) {
    Text("An error occured while trying to load prediction screen ! please retry")
    Button(onClick = retryAction) {
        Text(text = "Retry")
    }
}

@Composable
fun LoadingPrediction(
    modifier: Modifier
) {
    Text("Loading prediction screen ... Training Model ...")
}