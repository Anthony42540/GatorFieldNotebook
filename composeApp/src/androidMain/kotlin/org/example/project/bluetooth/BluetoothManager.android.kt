package org.example.project.bluetooth

import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import dev.bluefalcon.BlueFalcon

actual class BluetoothManager(context: ApplicationContext) {
    actual val blueFalcon = BlueFalcon(log = null, context = context)

//    actual val blueFalcon: Any = BlueFalcon(log = null, context = context)
    private val _devices = MutableStateFlow<List<BluetoothPeripheral>>(emptyList())
    actual val devices: StateFlow<List<BluetoothPeripheral>> = _devices

//    private val localBlue = blueFalcon as BlueFalcon

    actual fun startScanning() {
        blueFalcon.scan()
    }

    actual fun stopScanning() {
        blueFalcon.stopScanning()
    }

    // Custom CoroutineScope for the ViewModel
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            blueFalcon.peripherals.collect { peripherals ->
                // Use tryEmit to avoid suspending, or emit within this coroutine
                _devices.value = peripherals.toList() // Updates MutableStateFlow directly
            }
        }
    }

}