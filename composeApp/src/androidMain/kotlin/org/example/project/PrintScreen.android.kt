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
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.unit.LayoutDirection
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.io.IOException
import java.io.OutputStream
import java.util.UUID
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.Density
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleData
import com.dev.database.entity.SampleForm
import qrgenerator.qrkitpainter.rememberQrKitPainter
import kotlin.experimental.or

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
    fun connectAndPrint(macAddress: String, message: List<String>, qrImage: Bitmap?) {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothDevice = bluetoothAdapter?.getRemoteDevice(macAddress)
            bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream

            val labelConfig = "SIZE 57 mm,100 mm\nGAP 2.5 mm,0 mm\nCLS\n"
            var baseX = 10
            var baseY = 10
            val lineSpacing = 35

            val textCommands = message.mapIndexed { index, line ->
                val y = baseY + index * lineSpacing
                "TEXT $baseX,$y,\"3\",0,1,1,\"${line}\"\n"
            }

            val imageY = baseY + message.size * lineSpacing + 10
            val bitmapCommand = qrImage?.let {
                val monoBitmap = it
                bitmapToTSPLCommand(monoBitmap, 10, imageY)
            }

            val printCommand = "PRINT 1\n"

            outputStream?.apply {
                write(labelConfig.toByteArray())
                textCommands.forEach { write(it.toByteArray()) }
                bitmapCommand?.let { write(it) }
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
actual fun PrintScreen(navController: NavController, database: Database?, sampleId: Long) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf("true") }
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var formName by remember { mutableStateOf<String?>(null) }
    var sample by remember { mutableStateOf<SampleAndData?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf<String?>(null)}

    val painter = rememberQrKitPainter(data = "myapp://sample/$sampleId")
    val size = painter.intrinsicSize
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val QRBitmap = painter.toBitmap(size, density, direction)

    LaunchedEffect(sampleId) {
        try {
            if (database == null) {
                errorMessage = "Database not initialized"
                return@LaunchedEffect
            }
            sample = database.getSampleAndData(sampleId)
            formName = database.getSampleForm(sampleId).formName
            isLoading = false.toString()
        } catch (e: Exception) {
            errorMessage = "Failed to load sample: ${e.message}"
            isLoading = false.toString()
        }
    }

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
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            SectionTitle("Print Sample")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                ) {
                    Text(
                        text = "$formName: #${sample?.sampleCollectionId}",
                        fontSize = 25.sp,
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(100.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
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
                            val splitLocation = sample?.location?.split(",")
                            val altitudeSplit = splitLocation?.get(1)?.split("|")
                            val message = listOf (
                                ("Form: ${formName ?: "Unknown"}"),
                                ("Collector: ${sample?.collectorName ?: "Unknown"}"),
                                ("Sample ID: ${sample?.sampleCollectionId ?: "Unknown"}"),
                                ("Date: ${sample?.dateCollectedUTC ?: "Unknown"}"),
                                ("Location:"),
                                ("${splitLocation?.get(0) ?: "Unknown"},${altitudeSplit?.get(0) ?: "Unknown"}"),
                                (altitudeSplit?.get(1) ?: ""),
                                ("---------------------------")
                            )

                            printer.connectAndPrint(printerMacAddress, message, QRBitmap)
                        }
                    },
                        Color(0xFF12BF7A), Color.White)
                }
            }
        }
    }
}

fun Painter.toBitmap(
    size: Size,
    density: Density,
    layoutDirection: LayoutDirection,
): Bitmap {
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(density, layoutDirection, canvas, size) {
        draw(size)
    }
    return bitmap.asAndroidBitmap()
}

fun bitmapToTSPLCommand(bitmap: Bitmap, x: Int, y: Int): ByteArray {
    val width = bitmap.width
    val height = bitmap.height
    val widthBytes = (width + 7) / 8

    val imageData = ByteArray(widthBytes * height)

    for (j in 0 until height) {
        for (i in 0 until width) {
            val pixel = bitmap.getPixel(i, j)
            val isBlack = pixel != 0xFF000000.toInt()
            if (isBlack) {
                val byteIndex = j * widthBytes + i / 8
                imageData[byteIndex] = imageData[byteIndex] or (0x80 shr (i % 8)).toByte()
            }
        }
    }

    val header = "BITMAP $x,$y,$widthBytes,$height,0,"
    val headerBytes = header.toByteArray()
    val combined = ByteArray(headerBytes.size + imageData.size + 1)
    System.arraycopy(headerBytes, 0, combined, 0, headerBytes.size)
    System.arraycopy(imageData, 0, combined, headerBytes.size, imageData.size)
    combined[combined.lastIndex] = '\n'.code.toByte()
    return combined
}