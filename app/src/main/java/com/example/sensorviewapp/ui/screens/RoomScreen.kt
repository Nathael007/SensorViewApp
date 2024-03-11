package com.example.sensorviewapp.ui.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sensorviewapp.model.GetLastValue
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Room
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.ui.screens.viewmodel.DataVisualizationUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoomScreen(
    navController: NavController,
    roomName: String,
    retryAction: () -> Unit,
    roomScreenViewModel: RoomScreenViewModel,
    modifier: Modifier = Modifier
) {
    val roomUiState by roomScreenViewModel.uiState.collectAsState()
    when (roomScreenViewModel.roomScreenUiState) {
        is RoomScreenUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is RoomScreenUiState.Success -> Dashboard(
            roomUiState,
            roomScreenViewModel,
            navController = navController,
            modifier.fillMaxSize(),
        )
        is RoomScreenUiState.Error -> ErrorScreen(retryAction, modifier.fillMaxSize())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    roomUiState: RoomUiState,
    roomScreenViewModel: RoomScreenViewModel,
    navController: NavController,
    modifier: Modifier
    ){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(roomUiState.sensorList?.get(0)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var lastValue by remember { mutableStateOf(roomUiState.lastValue) }
    DisposableEffect(roomUiState.lastValue) {
        lastValue = roomUiState.lastValue
        return@DisposableEffect onDispose {
        }
    }
    var listMeasures by remember { mutableStateOf(roomUiState.listMeasures) }
    DisposableEffect(roomUiState.listMeasures) {
        listMeasures = roomUiState.listMeasures
        return@DisposableEffect onDispose {
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                selectedItem?.let {
                    TextField(
                        value = it.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roomUiState.sensorList?.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.uom) },
                            onClick = {
                                selectedItem = item
                                expanded = false
                                Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                                CoroutineScope(Dispatchers.Default).launch {
                                    roomUiState.selectedSensor = item
                                    lastValue = roomScreenViewModel.getLastValue(
                                        GetLastValue(
                                            selectedItem!!.name, selectedItem!!.uom
                                        )
                                    )
                                    listMeasures = roomScreenViewModel.getMeasures()
                                }
                            }
                        )
                    }
                }
            }
        }
        Row {
            Box(contentAlignment = Alignment.Center) {
                Button(onClick = { showStartDatePicker = true }) {
                    getDisplayDateFormat(roomUiState.startDate)?.let { Text(text = it) }
                }
            }
            Box(contentAlignment = Alignment.Center) {
                Button(onClick = { showEndDatePicker = true }) {
                    getDisplayDateFormat(roomUiState.endDate)?.let { Text(text = it) }
                }
            }
            if (showStartDatePicker) {
                MyDatePickerDialog(
                    onDateSelected = {
                        roomUiState.startDate = it
                        CoroutineScope(Dispatchers.Default).launch {
                            listMeasures = roomScreenViewModel.getMeasures()
                        }
                    },
                    onDismiss = {
                        showStartDatePicker = false
                    }
                )
            }
            if (showEndDatePicker) {
                MyDatePickerDialog(
                    onDateSelected = {
                        roomUiState.endDate = it
                        CoroutineScope(Dispatchers.Default).launch {
                            listMeasures = roomScreenViewModel.getMeasures()
                        }
                    },
                    onDismiss = {
                        showEndDatePicker = false
                    }
                )
            }
        }
        Text(lastValue?.value.toString())
        listMeasures?.forEach {
            Text(it.value.toString())
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTimeMS by remember { mutableLongStateOf(0) }
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())

    val selectedDate = datePickerState.selectedDateMillis?.let {
        combineDateAndTime(it, convertMillisToDate(selectedTimeMS))
    } ?: ""
    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            selectedTimeMS = ((selectedHour * 60 * 60 * 1000) + (selectedMinute * 60 * 1000)).toLong()
            Log.v("time selected", selectedTimeMS.toString())
        },
        hour,
        minute,
        true
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                timePicker.show()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }

    DisposableEffect(selectedTimeMS) {
        if(selectedTimeMS != 0.toLong()) {
            onDateSelected(selectedDate)
            onDismiss()
        }
        onDispose { }
    }
}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
private fun convertMillisToDate(millis: Long): String {
    val format = SimpleDateFormat("HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("+01:00")
    return format.format(Date(millis))
}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
private fun combineDateAndTime(date: Long, time: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    val stringDate = format.format(Date(date))
    Log.v("date and time", stringDate + time)
    return stringDate + "T" + time + "Z"
}

private fun getDisplayDateFormat(dateNotFormatted: String): String? {
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val targetFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    val date = sourceFormat.parse(dateNotFormatted)

    return date?.let { targetFormat.format(it) }
}