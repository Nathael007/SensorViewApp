package com.example.sensorviewapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRoomSensors(
    val room: String
)

@Serializable
data class GetLastValue(
    val sensor: String,
    @SerialName(value = "UOM")
    val uom: String
)

@Serializable
data class GetSensorValues(
    val sensor: String,
    val start: String,
    val end: String
)

@Serializable
data class TrainIA(
    val sensor: String
)