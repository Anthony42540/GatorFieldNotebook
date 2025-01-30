package org.example.project.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.bluetooth.BluetoothManager
//import dev.bluefalcon.BlueFalcon


actual class BluetoothManager {

    // BlueFalcon does not support wasmJS (BlueFalcon is an unresolved reference)
    actual val blueFalcon: Any = "N/A"

//    actual val devices: StateFlow<List<Any>>

    actual fun startScanning() {
        TODO("Not supported by BlueFalcon")
    }
    actual fun stopScanning() {}
}