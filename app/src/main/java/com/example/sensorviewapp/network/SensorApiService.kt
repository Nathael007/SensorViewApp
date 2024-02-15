package com.example.sensorviewapp.network;

import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import retrofit2.http.GET;
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface SensorApiService {
    @GET("getAllInfos")
    suspend fun getRooms(): List<Room>

    @POST("getRoomSensors")
    suspend fun getRoomSensors(@Body body: GetRoomSensors): List<Sensor>

    @POST("getLastValue")
    suspend fun getLastValue(@Body body: GetLastValue): Measure
}