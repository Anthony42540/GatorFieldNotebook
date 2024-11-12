package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.jordond.compass.Location
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@Composable
fun EditSampleScreen(navController: NavController) {
    // Mutable state variables for input fields
    var collection by remember { mutableStateOf("") }
    var sampleName by remember { mutableStateOf("") }
    var sampleInfo by remember { mutableStateOf("") }
    var coordinates  by remember { mutableStateOf("") }
    var metersAltitude  by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf("") }

    val locationState = remember { mutableStateOf<Location?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val currentDateAndTime = Clock.System.now()
    val localDateTime = currentDateAndTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateTimeString = localDateTime.toString()
    val pair = dateTimeString.split("T")

    var date by remember { mutableStateOf(pair[0]) }
    var time by remember { mutableStateOf(pair[1].substring(0,8)) }

    fun refreshLocationAndAltitude() {
        isLoading = true.toString()

        coroutineScope.launch {
            GetCurrentLocation().onSuccess { location ->
                locationState.value = location
                coordinates =
                    (locationState.value!!.coordinates.latitude.toString()) + ", " + (locationState.value!!.coordinates.longitude.toString())
                metersAltitude = locationState.value!!.altitude!!.meters.toString() + " meters"
                isLoading = false.toString()
            }.onFailed { exception ->
                locationState.value = null
                isLoading = false.toString()
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshLocationAndAltitude()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavBar(navController) // The navigation bar at the top

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
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
                placeholder = { Text("Date") },
                readOnly = true
            )
            TextField(
                value = time,
                onValueChange = { time = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Time") },
                readOnly = true
            )

            // Location
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
                Text(
                    text = "Latitude/Longitude And Altitude",
                    fontSize = 18.sp,
                    color = Color(0xFFFFFFFF)
                )
                Button( onClick = { refreshLocationAndAltitude() }, colors = ButtonDefaults.buttonColors (containerColor = Color.White)) {
                    if (isLoading == "true") {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    }
                    else {
                        Text("Refresh", color = Color.Black)
                    }
                }
            }

            TextField(
                value = coordinates,
                onValueChange = { coordinates = it },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            TextField(
                value = metersAltitude,
                onValueChange = { metersAltitude = it },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
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

            Spacer(modifier = Modifier.height(12.dp))

            // Save Button
            Button(
                onClick = { /* Handle save logic here */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors (
                    containerColor = Color(0xFF12BF7A)
                )
            ) {
                Text(text = "Save", color = Color.White)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color(0xFFFFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0021A5))
            .padding(vertical = 6.dp)
            .padding(horizontal = 8.dp)
    )
}
