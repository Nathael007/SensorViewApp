    package com.example.sensorviewapp.data

import okhttp3.MediaType.Companion.toMediaType
import com.example.sensorviewapp.network.SensorApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json

interface AppContainer {
    val roomsRepository : RoomsRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://192.168.166.205:5000/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: SensorApiService by lazy {
        retrofit.create(SensorApiService::class.java)
    }

    override val roomsRepository: RoomsRepository by lazy {
        NetworkRoomsRepository(retrofitService)
    }
}
