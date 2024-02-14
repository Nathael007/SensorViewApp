package com.example.sensorviewapp

import android.app.Application
import com.example.sensorviewapp.data.AppContainer
import com.example.sensorviewapp.data.DefaultAppContainer

class RoomsApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate()  {
        super.onCreate()
        container = DefaultAppContainer()
    }
}