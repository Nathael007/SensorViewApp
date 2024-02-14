package com.example.sensorviewapp.network;

import com.example.sensorviewapp.model.Room
import retrofit2.http.GET;

interface SensorApiService {
    @GET("getAllInfos")
    suspend fun getRooms(): List<Room>
}