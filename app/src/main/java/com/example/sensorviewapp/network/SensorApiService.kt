package com.example.sensorviewapp.network;

import com.example.sensorviewapp.model.Comfort
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.model.GetSensorValues
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.model.TrainIA
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

    @POST("getSensorValues")
    suspend fun getSensorValues(@Body body: GetSensorValues): List<Measure>

    @POST("getComfortIndicators")
    suspend fun getComfortIndicators(): List<Comfort>

    @POST("getComfortIndicator")
    suspend fun getComfortIndicator(@Body body: GetRoomSensors): Comfort

    @POST("trainModel")
    suspend fun trainModel(@Body body: TrainIA): List<Measure>

    @POST("predict")
    suspend fun predict(@Body body: TrainIA): List<Measure>
}