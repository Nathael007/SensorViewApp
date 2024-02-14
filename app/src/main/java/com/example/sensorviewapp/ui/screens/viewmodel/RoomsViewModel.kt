package com.example.sensorviewapp.ui.screens.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sensorviewapp.RoomsApplication
import com.example.sensorviewapp.data.RoomsRepository
import com.example.sensorviewapp.model.Room

sealed interface DataVisualizationUiState {
    data class Success(val rooms: List<Room>) : DataVisualizationUiState
    data object Error : DataVisualizationUiState
    data object Loading : DataVisualizationUiState
}

class RoomsViewModel(
    private val roomsRepository: RoomsRepository
) : ViewModel() {
    var dataVisualizationUiState: DataVisualizationUiState by mutableStateOf(DataVisualizationUiState.Loading)
        private set

    init {
        getAllRooms()
    }

    private fun getAllRooms() {
        viewModelScope.launch {
            dataVisualizationUiState =
                DataVisualizationUiState.Loading
            dataVisualizationUiState = try {
                val result: List<Room> = roomsRepository.getRooms()
                DataVisualizationUiState.Success(result)
            }
            catch (e: IOException) {
                DataVisualizationUiState.Error
            } catch (e: HttpException) {
                DataVisualizationUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RoomsApplication)
                val roomsRepository = application.container.roomsRepository
                RoomsViewModel(roomsRepository = roomsRepository)
            }
        }
    }
}
