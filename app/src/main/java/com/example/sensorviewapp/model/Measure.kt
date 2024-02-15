package com.example.sensorviewapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Measure (
    @SerialName(value = "UOM")
    val uom: String,
    val date: String,
    val field: String,
    val name: String,
    val value: Double
)