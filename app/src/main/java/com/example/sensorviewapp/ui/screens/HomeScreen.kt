package com.example.sensorviewapp.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sensorviewapp.R
import com.example.sensorviewapp.model.HomeScreenElement

@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeElement = listOf(
        HomeScreenElement(stringResource(R.string.data_visualization), R.drawable.data_visualization),
        HomeScreenElement(stringResource(R.string.actuator), R.drawable.actuator),
        HomeScreenElement(stringResource(R.string.prediction), R.drawable.prediction),
    )
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(homeElement) { item ->
            ScreenCard(item = item, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCard(
    item: HomeScreenElement,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 200.dp)
            .padding(8.dp),
        onClick = { navController.navigate(item.text) },
    ) {
        AsyncImage(
            modifier = Modifier
                .size(width = 140.dp, height = 140.dp)
                .padding(start = 6.dp, end = 6.dp),
            model = item.image,
            contentDescription = "null"
        )
        Text(
            text = item.text,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}