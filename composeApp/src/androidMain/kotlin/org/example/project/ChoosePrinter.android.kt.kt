package org.example.project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection



@Composable
actual fun ChoosePrinter(navController: NavController, bt: BluetoothHandler) {

    // get our list of nearby printers
    var printersList : Array<out BluetoothConnection>? = (bt as BluetoothHandlerImp).getPrinters()

//    var counter by remember { mutableStateOf(0) }

    // scroll bar
    val scrollState = rememberScrollState(0)

    Column(
        modifier = Modifier.verticalScroll(scrollState)
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
            SectionTitle("Nearby Printers")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (printersList != null) {
                println("The size of the list of nearby printers: " + printersList.size)
                repeat(printersList.size) { index ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(
                                bt.getContext(),
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            println("Permissions denied 12+")
                            Text("Please enable bluetooth permissions")
//                                return
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                bt.getContext(),
                                Manifest.permission.BLUETOOTH
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            println("Permissions denied 11-")
                            Text("Please enable bluetooth and location permissions")
//                            return
                        }
                    }
                    println(printersList[index].device.getName())
                    Button(onClick = {
                        bt.setPrinter(printersList[index])
                        bt.startPrinter()
                    }

                    ) {
                        Text(printersList[index].device.getName())
                    }

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    ActionButton(
                        "Done",
                        onClick = {  navController.popBackStack() },
                        Color(0xFF0021A5),
                        Color.White
                    )
                }
            }
        }
    }
}