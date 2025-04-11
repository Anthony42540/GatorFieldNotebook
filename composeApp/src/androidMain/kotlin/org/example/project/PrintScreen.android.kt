package org.example.project

import KhandFontFamily
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.io.IOException
import java.io.OutputStream
import java.util.UUID
import androidx.compose.ui.platform.LocalContext

class TSPLPrinter {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            0 -> Log.i("TSPLPrinter", "Bluetooth connection successful")
            1 -> Log.e("TSPLPrinter", "Bluetooth connection failed")
            2 -> Log.i("TSPLPrinter", "Bluetooth disconnected successfully")
        }
        true
    }

    @SuppressLint("MissingPermission")
    fun connectAndPrint(macAddress: String, message: String = "Hello World") {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothDevice = bluetoothAdapter?.getRemoteDevice(macAddress)

            bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()

            outputStream = bluetoothSocket?.outputStream

            // Example TSPL Commands
            val labelConfig = "SIZE 50 mm,30 mm\nGAP 2.5 mm,0 mm\nCLS\n"
            val textCommand = "TEXT 10,10,\"3\",0,1,1,\"$message\"\n"
            val printCommand = "PRINT 1\n"

            outputStream?.apply {
                write(labelConfig.toByteArray())
                write(textCommand.toByteArray())
                write(printCommand.toByteArray())
                flush()
            }

            bluetoothSocket?.close()
            handler.obtainMessage(0).sendToTarget()

        } catch (e: IOException) {
            Log.e("TSPLPrinter", "Error during printing: ${e.message}")
            handler.obtainMessage(1).sendToTarget()
        }
    }

    fun disconnectBluetooth() {
        try {
            bluetoothSocket?.close()
            handler.obtainMessage(2).sendToTarget()
        } catch (e: IOException) {
            Log.e("TSPLPrinter", "Error disconnecting: ${e.message}")
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
actual fun PrintScreen(navController: NavController) {
    val context = LocalContext.current
    var sampleName by remember { mutableStateOf("") }
    var sampleDetails by remember { mutableStateOf("Sample details will appear here...") }
    var isLoading by remember { mutableStateOf("") }
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null && !discoveredDevices.contains(device)) {
                        discoveredDevices.add(device)
                    }
                }
            }
        }
    }

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    DisposableEffect(Unit) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        bluetoothAdapter?.startDiscovery()

        onDispose {
            bluetoothAdapter?.cancelDiscovery()
            context.unregisterReceiver(receiver)
        }
    }

    var pairedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()

    fun refreshDeviceList(context: Context, bluetoothAdapter: BluetoothAdapter?, discoveredDevices: MutableList<BluetoothDevice>) {
        isLoading = true.toString()
        discoveredDevices.clear()

        bluetoothAdapter?.startDiscovery()

        // Register the receiver to get newly discovered devices
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (!discoveredDevices.contains(it)) {
                            discoveredDevices.add(it)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        // Cancel discovery when done
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothAdapter?.cancelDiscovery()
            context.unregisterReceiver(receiver)
            isLoading = false.toString()
        }, 12000)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Header()

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Choose Sample Section
            SectionTitle("Choose Sample")
            TextField(
                value = sampleName,
                onValueChange = { sampleName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Sample Name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sample Details Section
            SectionTitle("Sample Details")
            TextField(
                value = sampleDetails,
                onValueChange = { sampleDetails = it },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0021A5))
                    .padding(vertical = 6.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                ) {
                    // Printer Options Section
                    Text(
                        style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                        text = "Printer Options",
                        fontSize = 25.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                Box (
                    modifier = Modifier.offset(x = 18.dp)
                ) {
                    Button(
                        onClick = {
                            refreshDeviceList(context, bluetoothAdapter, discoveredDevices)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(width = 140.dp, height = 45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        if (isLoading == "true") {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Refresh", color = Color.White, fontSize = 22.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0021A5))
                    .padding(vertical = 6.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                    text = "Choose Printer",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                    text = "Status",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 70.dp)
                ) {
                    items(discoveredDevices) { device ->
                        val isPaired = pairedDevices.contains(selectedDevice)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selectedDevice == device) Color(0xFFCCE5FF) else Color.White)
                                .padding(8.dp)
                                .clickable { selectedDevice = device }
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = device.name ?: device.address,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isPaired && device == selectedDevice) "connected" else "not connected",
                                    fontSize = 16.sp,
                                    color = if (isPaired && device == selectedDevice) Color.Green else Color.Red,
                                )
                            }
                        }
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    ActionButton("Back", onClick = { navController.popBackStack() }, Color(0xFF0021A5), Color.White)
                    ActionButton("Print", onClick = {
                        val printer = TSPLPrinter()
                        val printerMacAddress = selectedDevice?.address
                        if (printerMacAddress != null) {
                            printer.connectAndPrint(printerMacAddress, "Hello World")
                        }
                    },
                        Color(0xFF12BF7A), Color.White)
                }
            }
        }
    }


}
