package org.example.project.bluetooth

import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.flow.StateFlow
import dev.bluefalcon.BlueFalcon

actual class BluetoothManager (context: ApplicationContext) {

    // cannot store anything for a specific bluefalcon instance that requires ApplicationContext
    // the package that ApplicationContext depends on is iOS specific (platform.UIKit.UIApplication)
    actual val blueFalcon: BlueFalcon(context = ApplicationContext)
    actual val devices: StateFlow<List<BluetoothPeripheral>>
        get() = TODO("Not yet implemented")

    actual fun startScanning() {
        TODO("")
    }
}