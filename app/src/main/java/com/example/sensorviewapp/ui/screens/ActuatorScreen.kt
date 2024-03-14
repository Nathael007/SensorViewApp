package com.example.sensorviewapp.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.navigation.NavController

@Composable
fun ActuatorScreen(
    navController: NavController,
    scrollState: ScrollState
) {
    Text("Actuator")
}