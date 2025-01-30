package org.example.project.bluetooth

import kotlinx.coroutines.flow.StateFlow
import dev.bluefalcon.BluetoothPeripheral
import dev.bluefalcon.BlueFalcon

expect class BluetoothManager {
//    val blueFalconApp: BlueFalconApplication
//    val blueContext: Any
    val blueFalcon: BlueFalcon              // = BlueFalcon(context = ApplicationContext())
//    val blueFalcon: Any
//    val devices: StateFlow<List<Any>> // should be BluetoothPeripheral only as type for list
    val devices: StateFlow<List<BluetoothPeripheral>>
    fun startScanning()
    fun stopScanning()
}