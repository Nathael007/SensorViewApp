package com.example.sensorviewapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Comfort (
    val co2: Double,
    val comfort: String,
    val humidity: Double,
    val indicators: String,
    val lum: Double,
    val noise: Double?,
    val roomName: String,
    val temp: Double,
    val presence: Boolean?,
    val presenceProba: Double
)