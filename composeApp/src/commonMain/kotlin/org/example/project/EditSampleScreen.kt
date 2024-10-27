package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EditSampleScreen(navController: NavController) {
    // Mutable state variables for input fields
    var collection by remember { mutableStateOf("") }
    var sampleName by remember { mutableStateOf("") }
    var sampleInfo by remember { mutableStateOf("") }
    var latitudeLongitude by remember { mutableStateOf("") }
    var altitude by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavBar(navController) // The navigation bar at the top

        Spacer(modifier = Modifier.height(16.dp))

        // Choose Collection
        SectionTitle("Choose Collection")
        TextField(
            value = collection,
            onValueChange = { collection = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Date/Time
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Date/Time")
        TextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Date") }
        )
        TextField(
            value = time,
            onValueChange = { time = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Time") }
        )

        // Location
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Location")
        TextField(
            value = latitudeLongitude,
            onValueChange = { latitudeLongitude = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Latitude/Longitude") }
        )
        TextField(
            value = altitude,
            onValueChange = { altitude = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Altitude") }
        )

        // Sample Name/Info
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Sample Name/Sample Info")
        TextField(
            value = sampleName,
            onValueChange = { sampleName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Sample Name") }
        )
        TextField(
            value = sampleInfo,
            onValueChange = { sampleInfo = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Sample Info") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = { /* Handle save logic here */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color(0xFF0021A5),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0021A5).copy(alpha = 0.1f))
            .padding(vertical = 8.dp)
    )
}
