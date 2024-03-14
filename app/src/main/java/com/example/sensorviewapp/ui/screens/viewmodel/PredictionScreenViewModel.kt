package com.example.sensorviewapp.ui.screens.viewmodel

import PredictionUiState
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sensorviewapp.RoomsApplication
import com.example.sensorviewapp.data.RoomsRepository
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.model.GetSensorValues
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.model.TrainIA
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface PredictionScreenUiState {

    data object Success : PredictionScreenUiState
    data object Error : PredictionScreenUiState
    data object Loading : PredictionScreenUiState
}
class PredictionScreenViewModel(
    private val roomsRepository: RoomsRepository
) : ViewModel() {
    var predictionScreenUiState: PredictionScreenUiState by mutableStateOf(PredictionScreenUiState.Loading)
        private set

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    init {
        initialize()
    }

    suspend fun predict() = coroutineScope {
        try{
            _uiState.value.predictions = roomsRepository.predict(TrainIA(uiState.value.selectedSensor))
        } catch(e: IOException){
            Log.e("getMeasure IO ERROR", e.toString())
        } catch(e: HttpException){
            Log.e("getMeasure HTTP ERROR", e.toString())
        }
    }

    private fun initialize(){
        viewModelScope.launch {
            predictionScreenUiState =
                PredictionScreenUiState.Loading
            predictionScreenUiState = try {
                _uiState.value.losses = roomsRepository.trainModel(TrainIA(_uiState.value.selectedSensor))
                _uiState.value.predictions = roomsRepository.predict(TrainIA(_uiState.value.selectedSensor))
                PredictionScreenUiState.Success
            } catch (e: IOException) {
                PredictionScreenUiState.Error
            } catch (e: HttpException) {
                PredictionScreenUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RoomsApplication)
                val roomsRepository = application.container.roomsRepository
                PredictionScreenViewModel(roomsRepository = roomsRepository)
            }
        }
    }
}