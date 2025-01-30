package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.example.project.bluetooth.BluetoothManager
import org.example.project.bluetooth.DevicesView

@Composable
fun BluetoothScreen(navController: NavController, viewModel: BluetoothManager) {

    // scroll implemented with lazycolumn in devicesview
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        NavBar(navController) // Navigation bar at the top

        // Title
        Text(text = "Bluetooth Devices", color = Color.Black, modifier = Modifier.padding(8.dp))

        DevicesView(viewModel)
    }
}