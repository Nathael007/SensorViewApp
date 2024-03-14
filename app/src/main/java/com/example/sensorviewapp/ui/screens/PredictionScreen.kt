package com.example.sensorviewapp.ui.screens

import PredictionUiState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.sensorviewapp.ui.screens.viewmodel.PredictionScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.PredictionScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomUiState

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
    modifier: Modifier = Modifier
) {
    val predictionUiState by predictionScreenViewModel.uiState.collectAsState()
    when (predictionScreenViewModel.predictionScreenUiState) {
        is PredictionScreenUiState.Loading -> LoadingPrediction(modifier.fillMaxSize())
        is PredictionScreenUiState.Success -> Main(
            predictionUiState,
            predictionScreenViewModel,
            navController = navController,
            modifier.fillMaxSize()
        )
        is PredictionScreenUiState.Error -> ErrorPrediction(modifier.fillMaxSize(), retryAction)
    }
}

@Composable
fun Main(
    predictionUiState: PredictionUiState,
    predictionScreenViewModel: PredictionScreenViewModel,
    navController: NavController,
    modifier: Modifier
) {
    Column {
        predictionUiState.predictions?.forEach{
            Text(it.value.toString())
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