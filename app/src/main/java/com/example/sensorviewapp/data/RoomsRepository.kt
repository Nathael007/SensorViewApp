package com.example.sensorviewapp.data

import com.example.sensorviewapp.model.Comfort
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.model.GetSensorValues
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.model.TrainIA
import com.example.sensorviewapp.network.SensorApiService
import retrofit2.http.Body

interface RoomsRepository {
    suspend fun getRooms(): List<Room>
    suspend fun getRoomSensors(body: GetRoomSensors): List<Sensor>
    suspend fun getLastValue(body: GetLastValue): Measure
    suspend fun getSensorValues(body: GetSensorValues): List<Measure>
    suspend fun getComfortIndicators(): List<Comfort>
    suspend fun getComfortIndicator(body: GetRoomSensors): Comfort
    suspend fun trainModel(body: TrainIA): List<Measure>
    suspend fun predict(body: TrainIA): List<Measure>
}

class NetworkRoomsRepository(
    private val sensorApiService: SensorApiService
) : RoomsRepository {
    override suspend fun getRooms(): List<Room> = sensorApiService.getRooms()
    override suspend fun getRoomSensors(body: GetRoomSensors): List<Sensor> = sensorApiService.getRoomSensors(body)
    override suspend fun getLastValue(body: GetLastValue): Measure = sensorApiService.getLastValue(body)
    override suspend fun getSensorValues(body: GetSensorValues): List<Measure> = sensorApiService.getSensorValues(body)
    override suspend fun getComfortIndicators(): List<Comfort> = sensorApiService.getComfortIndicators()
    override suspend fun getComfortIndicator(body: GetRoomSensors): Comfort = sensorApiService.getComfortIndicator(body)
    override suspend fun trainModel(body: TrainIA): List<Measure> = sensorApiService.trainModel(body)
    override suspend fun predict(body: TrainIA): List<Measure> = sensorApiService.predict(body)
}