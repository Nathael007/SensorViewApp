package com.example.sensorviewapp.ui.screens.viewmodel

import android.text.format.DateFormat
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Sensor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

data class RoomUiState (
    var selectedSensor: Sensor? = null,
    var lastValue: Measure? = null,
    var sensorList: List<Sensor>? = null,
    var startDate: Date = Date(),
    var endDate: Date = Date(System.currentTimeMillis() - (60*60*24*1000))
    )