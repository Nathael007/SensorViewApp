package com.example.sensorviewapp.ui

import android.hardware.SensorPrivacyManager.Sensors
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sensorviewapp.R
import com.example.sensorviewapp.model.GetRoomSensors
import com.example.sensorviewapp.ui.screens.ActuatorScreen
import com.example.sensorviewapp.ui.screens.DataVisualizationScreen
import com.example.sensorviewapp.ui.screens.HomeScreen
import com.example.sensorviewapp.ui.screens.PredictionScreen
import com.example.sensorviewapp.ui.screens.RoomScreen
import com.example.sensorviewapp.ui.screens.viewmodel.DataVisualizationUiState
import com.example.sensorviewapp.ui.screens.viewmodel.RoomScreenViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.RoomsViewModel
import com.example.sensorviewapp.ui.screens.viewmodel.roomScreenViewModelHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorViewApp() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentScreen = backStackEntry?.destination?.route ?: "Home"
    if(currentScreen == "Home") {
        currentScreen = stringResource(R.string.app_name)
    }
    if(currentScreen == "Data Visualization") {
        currentScreen = stringResource(R.string.data_visualization)
    }
    if(currentScreen == "Data Visualization" + "/{roomName}") {
        currentScreen = stringResource(R.string.room)
    }
    if(currentScreen == "Actuator") {
        currentScreen = stringResource(R.string.actuator)
    }
    if(currentScreen == "Prediction") {
        currentScreen = stringResource(R.string.prediction)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SensorTopAppBar(
                scrollBehavior = scrollBehavior,
                navController = navController,
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = currentScreen
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Navigation(
                navController = navController,
                destination = "Home"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navController: NavController,
    currentScreen: String
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = currentScreen,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun Navigation(
    navController: NavHostController,
    destination: String
) {
    NavHost(
        navController = navController,
        startDestination = destination
    ) {
        composable("Home") {
            HomeScreen(
                navController = navController
                )
        }
        composable("Data Visualization") {
            val roomsViewModel: RoomsViewModel = viewModel(factory = RoomsViewModel.Factory)
            DataVisualizationScreen(
                modifier = Modifier,
                navController = navController,
                retryAction = {},
                dataVisualizationUiState = roomsViewModel.dataVisualizationUiState
            )
        }
        composable("Prediction") {
            PredictionScreen(
                navController = navController
            )
        }
        composable(
            route = "Data Visualization" + "/{roomName}",
            arguments = listOf(navArgument("roomName") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("roomName")?.let {roomName ->
                roomScreenViewModelHelper.currentRoom = GetRoomSensors(room = roomName)
                val roomScreenViewModel: RoomScreenViewModel = viewModel(factory = RoomScreenViewModel.Factory)
                RoomScreen(
                    navController = navController,
                    roomName = roomName,
                    retryAction = {},
                    roomScreenViewModel = roomScreenViewModel,
                    roomScreenUiState = roomScreenViewModel.roomScreenUiState
                )
            }
        }
        composable("Actuator") {
            ActuatorScreen(
                navController = navController
            )
        }
        composable("Prediction") {
            PredictionScreen(
                navController = navController
            )
        }
    }
}