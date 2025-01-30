package org.example.project

import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
import androidx.compose.runtime.rememberCoroutineScope
//import kotlinx.coroutines.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.SharingStarted


class BluetoothViewModel(private val blueFalcon: BlueFalcon) {
    private val _devices = MutableStateFlow<List<BluetoothPeripheral>>(emptyList())
    val devices: StateFlow<List<BluetoothPeripheral>> = _devices

    // Custom CoroutineScope for this ViewModel
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            blueFalcon.peripherals.collect { peripherals ->
                // Use tryEmit to avoid suspending, or emit within this coroutine
                _devices.value = peripherals.toList() // Updates MutableStateFlow directly
            }
        }
    }

    fun startScanning() {
        blueFalcon.scan()
    }
}