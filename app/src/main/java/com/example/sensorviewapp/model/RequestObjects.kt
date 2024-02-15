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
