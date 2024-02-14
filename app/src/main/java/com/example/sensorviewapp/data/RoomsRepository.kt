package com.example.sensorviewapp.data

import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.network.SensorApiService

interface RoomsRepository {
    suspend fun getRooms(): List<Room>
}

class NetworkRoomsRepository(
    private val sensorApiService: SensorApiService
) : RoomsRepository {
    override suspend fun getRooms(): List<Room> = sensorApiService.getRooms()
}