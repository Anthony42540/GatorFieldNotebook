package org.example.project.bluetooth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bluefalcon.BluetoothPeripheral
import android.Manifest
import android.os.Build


@Composable
actual fun DevicesView(viewModel: BluetoothManager) {
//actual fun DevicesView(viewModel: BluetoothViewModel) {

    /*
     TO DO
     We also need to remove devices from the list if they are not nearby
     (a better method might be to simply clear the list, and rescan every
     time the scan for devices button is pressed?)
    */

    val devices = viewModel.devices.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    val bluetoothPermissions: List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else listOf(Manifest.permission.BLUETOOTH)
//
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Scan Button
        Row {
            Button(
                onClick = {
                    try {
                        viewModel.startScanning()
                    } catch (e: SecurityException) {
                        println("Security Exception: ${e.message}; check permissions")
                        //request the ACCESS_COARSE_LOCATION permission
                        // when permissions
                        // coroutineScope.launch {
                        // }
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan for Devices")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Devices nearby: " +  (devices.value.size).toString())
        LazyColumn {
            items(devices.value.size) { index ->
                val device = devices.value[index]
                DeviceRow(device)
            }
        }

        // do not continuously scan forever
//        viewModel.stopScanning()

        // when using lazyColumn -> BEWARE the vertical scroller error
        // lazy column already has a scroller, and having a vertical scroller
        // used with lazy column will cause a crash/errors
    }
}

@Composable
fun DeviceRow(device: BluetoothPeripheral) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        var name = device.name
        if (device.name != null) {
            if (device.name == device.uuid) {
                name = "N/A"
            }
        }
        Text(text = "Name: ${name ?: "Unknown"}")
        Text(text = "UUID: ${device.uuid}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}