package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
//import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
actual fun PrintScreen(navController: NavController, bt: BluetoothHandler){

    // This page can be cleaned up a lot since I tinkered around to test different libraries

    var sampleName by remember { mutableStateOf("") }
    var sampleDetails by remember { mutableStateOf("Sample details will appear here...") }
    var printerStatus by remember { mutableStateOf("offline") }
    var isConnected by remember { mutableStateOf(false) }

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
            modifier = Modifier.verticalScroll(scrollState)
//            modifier = Modifier
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

            // Printer Options Section
            SectionTitle("Printer Options")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0021A5))
                    .padding(vertical = 6.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        navController.navigate("choosePrinter")
//                        printerStatus = "online"
                    },
                    modifier = Modifier
                        .width(150.dp) // Set the width
                        .height(100.dp) // Set the height
                        .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        style = TextStyle(
//                        fontFamily = KhandFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        text = "Choose Printer",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        bt.disconnectPrinter()
                        isConnected = false
                        printerStatus = "offline"
                    },
                    modifier = Modifier
                        .width(150.dp) // Set the width
                        .height(100.dp) // Set the height
                        .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        style = TextStyle(
//                        fontFamily = KhandFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        text = "Disconnect Printer",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if ((bt as BluetoothHandlerImp).printer != null) {
                    isConnected = true
                }
                Text(
                    // put the name of connected printer
//                    text = if (printerStatus == "online") "online" else "offline",
                    text = if(isConnected) { "Current Printer: " + (bt as BluetoothHandlerImp).getPrinterName()} else { "Current Printer: No connection"},
                    fontSize = 20.sp,
                    color = Color.Black
//                    color = if (printerStatus == "online") Color.Green else Color.Red
                )
            }

//            // Connections Options Section
//            SectionTitle("Connection Type Options")
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFF0021A5))
//                    .padding(vertical = 10.dp)
//                    .padding(horizontal = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column (
//                    modifier = Modifier
//                        .padding(vertical = 10.dp)
//                        .padding(horizontal = 8.dp),
//                    verticalArrangement = Arrangement.SpaceBetween,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        style = TextStyle(
////                        fontFamily = KhandFontFamily(),
//                            fontWeight = FontWeight.Medium
//                        ),
//                        text = "Status",
//                        fontSize = 20.sp,
//                        color = Color.White
//                    )
//                }
//                Row (
//                    modifier = Modifier
//                        .padding(vertical = 10.dp)
//                        .padding(horizontal = 8.dp),
//                        horizontalArrangement = Arrangement.End,
//                        verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        style = TextStyle(
////                        fontFamily = KhandFontFamily(),
//                            fontWeight = FontWeight.Medium
//                        ),
//                        // put in the currently connected printer
//                        text = if(isConnected) { (bt as BluetoothHandlerImp).getPrinterName()} else { "No connection"},
//                        fontSize = 16.sp,
//                        color = Color.White
//                    )
//                }
//            }
            // Print Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    ActionButton("Back", onClick = { navController.popBackStack() }, Color(0xFF0021A5), Color.White)
                    ActionButton("Print", onClick = { navController.navigate("LabelPrint")}, Color(0xFF12BF7A), Color.White)
                }
            }
        }
    }
}