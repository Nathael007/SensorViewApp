package com.example.sensorviewapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sensor (
    @SerialName(value = "UOM")
    val uom: String,
    val name: String
)