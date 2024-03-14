package com.example.sensorviewapp.ui.screens.viewmodel

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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface RoomScreenUiState {

    data object Success : RoomScreenUiState
    data object Error : RoomScreenUiState
    data object Loading : RoomScreenUiState
}

class RoomScreenViewModelHelper {
    var currentRoom: GetRoomSensors = GetRoomSensors(room = "")
}

val roomScreenViewModelHelper = RoomScreenViewModelHelper()
class RoomScreenViewModel(
    private val roomsRepository: RoomsRepository
) : ViewModel() {
    var roomScreenUiState: RoomScreenUiState by mutableStateOf(RoomScreenUiState.Loading)
        private set

    var room: GetRoomSensors = roomScreenViewModelHelper.currentRoom

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        getRoomSensors()
    }

    suspend fun getLastValue(body: GetLastValue): Measure? = coroutineScope{
        var result: Measure? = null
        try {
            result = roomsRepository.getLastValue(body)
        } catch (e: IOException) {
            Log.v("GetLastValue IOE exception", e.toString())
        } catch (e: HttpException) {
            Log.v("GetLastValue HTTP exception", e.toString())
        }
        return@coroutineScope result
    }

    suspend fun getMeasures(): List<Measure>? = coroutineScope {
        var result: List<Measure>? = null
        try{
            result = roomsRepository.getSensorValues(
                GetSensorValues(
                    _uiState.value.selectedSensor?.name ?: "",
                    _uiState.value.startDate,
                    _uiState.value.endDate)
            )
        } catch(e: IOException){
            Log.e("getMeasure IO ERROR", e.toString())
        } catch(e: HttpException){
            Log.e("getMeasure HTTP ERROR", e.toString())
        }
        return@coroutineScope result
    }

    private fun getRoomSensors() {
        viewModelScope.launch {
            roomScreenUiState =
                RoomScreenUiState.Loading
            roomScreenUiState = try {
                val result: List<Sensor> = roomsRepository.getRoomSensors(room)
                _uiState.value.sensorList = result
                _uiState.value.selectedSensor = result[0]
                _uiState.value.lastValue = roomsRepository.getLastValue(GetLastValue(result[0].name, result[0].uom))
                _uiState.value.comfortIndicator = roomsRepository.getComfortIndicator(room)
                Log.v("parameters : ", result[0].name + " " + _uiState.value.startDate + " " + _uiState.value.endDate)
                _uiState.value.listMeasures = roomsRepository.getSensorValues(GetSensorValues(result[0].name, _uiState.value.startDate, _uiState.value.endDate))
                Log.v("measures : ", _uiState.value.listMeasures.toString())
                val measureValue: MutableList<Double> = mutableListOf()
                _uiState.value.listMeasures?.forEach {
                    measureValue.add(it.value)
                }
                _uiState.value.graphData = measureValue.map { it as Number }
                RoomScreenUiState.Success
            } catch (e: IOException) {
                RoomScreenUiState.Error
            } catch (e: HttpException) {
                RoomScreenUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RoomsApplication)
                val roomsRepository = application.container.roomsRepository
                RoomScreenViewModel(roomsRepository = roomsRepository)
            }
        }
    }
}