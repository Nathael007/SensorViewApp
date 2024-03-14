    package com.example.sensorviewapp.ui.screens.viewmodel

import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import com.example.sensorviewapp.model.Comfort
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Sensor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class RoomUiState (
    var selectedSensor: Sensor? = null,
    var lastValue: Measure? = null,
    var listMeasures: List<Measure>? = null,
    var sensorList: List<Sensor>? = null,
    var startDate: String = getYesterdayFormatted(),
    var endDate: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date()),
    var comfortIndicator: Comfort? = null,
    var graphData: Collection<Number>? = null
)

fun getYesterdayFormatted(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, -1) // Soustraire un jour

    val yesterday = calendar.time
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    return formatter.format(yesterday)
}