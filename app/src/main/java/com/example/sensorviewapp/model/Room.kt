package com.example.sensorviewapp.model

import kotlinx.serialization.Serializable



@Serializable
data class Room(
    val name: String,
    val sensors: List<Sensor>
)