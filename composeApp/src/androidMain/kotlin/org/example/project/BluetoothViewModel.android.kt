package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bluefalcon.BluetoothPeripheral
import org.example.project.BluetoothViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
actual fun DevicesView(viewModel: BluetoothViewModel) {
    val devices = viewModel.devices.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Scan Button
        Button(onClick = {
            try {
                viewModel.startScanning()
            }
            catch (e: SecurityException) {
                println("Security Exception: ${e.message}; check permissions")
                //request the ACCESS_COARSE_LOCATION permission
                // coroutineScope.launch {

                // }
            }

             },
            modifier = Modifier.fillMaxWidth()) {
            Text("Scan for Devices")

        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Devices
        LazyColumn {
            items(devices.value.size) { index ->
                val device = devices.value[index]
                DeviceRow(device)
            }
        }
    }
}

@Composable
fun DeviceRow(device: BluetoothPeripheral) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "Name: ${device.name ?: "Unknown"}")
        Text(text = "UUID: ${device.uuid}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}
