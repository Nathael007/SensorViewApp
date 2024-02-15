package com.example.sensorviewapp.data

import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.network.SensorApiService
import retrofit2.http.Body

interface RoomsRepository {
    suspend fun getRooms(): List<Room>
    suspend fun getRoomSensors(body: GetRoomSensors): List<Sensor>
    suspend fun getLastValue(body: GetLastValue): Measure
}

class NetworkRoomsRepository(
    private val sensorApiService: SensorApiService
) : RoomsRepository {
    override suspend fun getRooms(): List<Room> = sensorApiService.getRooms()
    override suspend fun getRoomSensors(body: GetRoomSensors): List<Sensor> = sensorApiService.getRoomSensors(body)
    override suspend fun getLastValue(body: GetLastValue): Measure = sensorApiService.getLastValue(body)
}