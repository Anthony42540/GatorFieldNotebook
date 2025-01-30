package org.example.project

import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BlueFalcon

class BlueFalconApplication(context: ApplicationContext) {
    private val blueFalcon = BlueFalcon(log = null, context = context)
    val bluetoothViewModel = BluetoothViewModel(blueFalcon)
}