package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PrintScreen(navController: NavController) {
    var sampleName by remember { mutableStateOf("") }
    var sampleDetails by remember { mutableStateOf("Sample details will appear here...") }
    var selectedPrinter by remember { mutableStateOf("Sample Printer 223442") }
    var printerStatus by remember { mutableStateOf("online") }

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

            // Printer Options Section
            SectionTitle("Printer Options")
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedPrinter,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = if (printerStatus == "online") "online" else "offline",
                    fontSize = 16.sp,
                    color = if (printerStatus == "online") Color.Green else Color.Red
                )
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
                    ActionButton("Back", onClick = { navController.navigate("sampleDetail/"+GlobalState.sampleId) }, Color(0xFF0021A5), Color.White)
                    ActionButton("Print", onClick = {  }, Color(0xFF12BF7A), Color.White)
                }
            }
        }
    }
}
