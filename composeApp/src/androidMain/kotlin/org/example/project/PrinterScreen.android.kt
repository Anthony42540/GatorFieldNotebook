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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label

@Composable
actual fun PrintScreen(navController: NavController, bt: BluetoothHandler){

    // This page can be cleaned up a lot since I tinkered around to test different libraries

    var sampleName by remember { mutableStateOf("") }
    var sampleDetails by remember { mutableStateOf("Sample details will appear here...") }
    var selectedPrinter by remember { mutableStateOf("Sample Printer 223442") }
    var printerStatus by remember { mutableStateOf("offline") }
    var isConnected by remember { mutableStateOf(false) }


    val scrollState = rememberScrollState(0)
    var libOptions: Array<String> =
        arrayOf(
            "Printooth",
            "DantSu (ESCPOS)",
            "N/A"
        )


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

            // Printer Options Section --> make the choose printer a button
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
                        printerStatus = "online"
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
                Text(
                    text = if (printerStatus == "online") "online" else "offline",
                    fontSize = 20.sp,
                    color = if (printerStatus == "online") Color.Green else Color.Red
                )
            }

            // Connections Options Section
            SectionTitle("Connection Type Options")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0021A5))
                    .padding(vertical = 10.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        style = TextStyle(
//                        fontFamily = KhandFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        text = "Status",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
                Row (
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = TextStyle(
//                        fontFamily = KhandFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        // put in the currently connected printer
                        text = if(isConnected) { (bt as BluetoothHandlerImp).getPrinterName()} else { "No connection"},
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            repeat(libOptions.size) { index ->
                Row (
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(
                        onClick = {
                            bt.setConnection(libOptions[index])
                            isConnected = true

                            println("CONNECTION TYPE SET")
                            println(libOptions[index])
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue, // Changes the entire button background color
                            contentColor = Color.White   // Sets the color of the text or content inside the button
                        )
                    ) {
                        Text(text = libOptions[index], color = Color.White, fontSize = 16.sp)
                    }
                    Text(
                        text = if (isConnected && bt.getConnection() == libOptions[index]) "selected" else "",
//                        text = "",
                        fontSize = 16.sp,
                        color = if (printerStatus == "online") Color.Green else Color.Red,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(horizontal = 70.dp)
                    )
                }
            }
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