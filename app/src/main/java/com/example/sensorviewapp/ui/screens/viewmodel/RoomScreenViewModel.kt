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
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Sensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface RoomScreenUiState {

    data class Success(val sensors: List<Sensor>) : RoomScreenUiState
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

    init {
        getRoomSensors()
    }

    @OptIn(DelicateCoroutinesApi::class)
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

    private fun getRoomSensors() {
        viewModelScope.launch {
            roomScreenUiState =
                RoomScreenUiState.Loading
            roomScreenUiState = try {
                Log.v("TEST API", room.room)
                val result: List<Sensor> = roomsRepository.getRoomSensors(room)
                RoomScreenUiState.Success(result)
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