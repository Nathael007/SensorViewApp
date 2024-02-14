package com.example.sensorviewapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sensorviewapp.model.Room

@Composable
fun RoomScreen(
    navController: NavController,
    roomName: String
) {
    Text(roomName)
}