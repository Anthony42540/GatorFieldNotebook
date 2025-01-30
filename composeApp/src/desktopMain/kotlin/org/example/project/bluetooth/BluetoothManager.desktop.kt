package org.example.project.bluetooth

import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.flow.StateFlow
import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BlueFalcon

actual class BluetoothManager (context: ApplicationContext){
    actual val blueFalcon = BlueFalcon(context = context)
//    actual val blueFalconApp: BlueFalconApplication by lazy {
//        BlueFalconApplication(context = context)
//    }
    actual val devices: StateFlow<List<BluetoothPeripheral>>
        get() = TODO("Not yet implemented")

    actual fun startScanning() {
    }
    actual fun stopScanning(){}
}