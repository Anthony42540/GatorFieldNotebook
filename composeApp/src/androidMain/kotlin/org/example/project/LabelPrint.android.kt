package org.example.project

import android.R
import android.util.DisplayMetrics
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg


@Composable
actual fun LabelPrint(navController: NavController, bt: BluetoothHandler) {

    // place holder

    var sampleName by remember { mutableStateOf("") }
    var sampleDetails by remember { mutableStateOf("Sample details will appear here...") }
    var selectedPrinter by remember { mutableStateOf("Sample Printer 223442") }
    var printerStatus by remember { mutableStateOf("online") }

    val scrollState = rememberScrollState(0)

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
                .verticalScroll(scrollState)
//            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Choose Sample Section
            SectionTitle("Label Design")
            TextField(
                value = sampleName,
                onValueChange = { sampleName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Sample Name") }
            )

            // Print Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    ActionButton(
                        "Back",
                        onClick = { navController.popBackStack() },
                        Color(0xFF0021A5),
                        Color.White
                    )
                    ActionButton("Print", onClick = {
                        if ((bt as BluetoothHandlerImp).getConnection() == "Printooth") {
                            bt.printoothBT.printLabel("Hello World")
                        }
                        else {
                            val printer = EscPosPrinter(
//                                BluetoothPrintersConnections.selectFirstPaired(),
                                (bt.printer as BluetoothConnection),
                                300,
                                48f,
                                20
                            )
                            printer.printFormattedText(
                                    """
                                    [L]<b>HELLO WORLD </b>[R]
                                    """.trimIndent()
                                )
                        }
                    }, Color(0xFF12BF7A), Color.White)
                }
            }
        }
    }
}