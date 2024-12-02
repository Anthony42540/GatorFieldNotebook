package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import dev.jordond.compass.Location
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@Composable
fun EditSampleScreen(navController: NavController, database: Database? = null) {
    // Add debug logging
    LaunchedEffect(Unit) {
        println("EditSampleScreen launched")
        println("Database is ${if (database == null) "null" else "not null"}")
    }

    // Mutable state variables for input fields
    var collection by remember { mutableStateOf("") }
    var sampleName by remember { mutableStateOf("") }
    var sampleInfo by remember { mutableStateOf("") }
    var coordinates by remember { mutableStateOf("") }
    var metersAltitude by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val locationState = remember { mutableStateOf<Location?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val currentDateAndTime = Clock.System.now()
    val localDateTime = currentDateAndTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateTimeString = localDateTime.toString()
    val pair = dateTimeString.split("T")

    var date by remember { mutableStateOf(pair[0]) }
    var time by remember { mutableStateOf(pair[1].substring(0,8)) }

    // Available forms for selection
    var availableForms by remember { mutableStateOf(emptyList<Long>()) }
    var selectedFormId by remember { mutableStateOf<Long?>(null) }

    // Load available forms when screen is first displayed
    LaunchedEffect(Unit) {
        database?.let { db ->
            try {
                println("Loading available forms")
                val forms = db.getAllSampleForms()
                availableForms = forms.map { it.formId.toLong() }
                if (forms.isNotEmpty()) {
                    selectedFormId = forms.first().formId.toLong()
                    collection = forms.first().formName
                }
                println("Loaded ${forms.size} forms")
            } catch (e: Exception) {
                errorMessage = "Failed to load forms: ${e.message}"
                println("Error loading forms: ${e.message}")
                e.printStackTrace()
            }
        }
    }

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
                errorMessage = "Failed to get location: ${exception.message}"
            }
        }
    }

    fun saveSample() {
        if (database == null) {
            errorMessage = "Database not initialized"
            println("Save failed: Database not initialized")
            return
        }

        if (selectedFormId == null) {
            errorMessage = "Please select a form"
            println("Save failed: No form selected")
            return
        }

        if (sampleName.isBlank()) {
            errorMessage = "Sample name is required"
            println("Save failed: Sample name is blank")
            return
        }

        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null
                println("Starting sample save process")

                // Create location string from coordinates and altitude
                val locationString = "$coordinates|$metersAltitude"

                // Insert sample data
                val sampleId = database.insertSampleData(
                    formId = selectedFormId!!,
                    dateCollectedUtc = "${date}T$time",
                    location = locationString
                )
                println("Created sample with ID: $sampleId")

                // Get form fields
                val fields = database.getFormFields(selectedFormId!!)
                println("Retrieved ${fields.size} fields for form")

                // Find name and info fields
                val nameField = fields.find { it.fieldName.equals("Sample Name", ignoreCase = true) }
                val infoField = fields.find { it.fieldName.equals("Sample Info", ignoreCase = true) }

                // Insert data entries
                nameField?.let {
                    println("Inserting Sample Name field")
                    database.insertDataEntry(
                        sampleId = sampleId,
                        fieldId = it.fieldId.toLong(),
                        userInput = sampleName
                    )
                }

                infoField?.let {
                    println("Inserting Sample Info field")
                    database.insertDataEntry(
                        sampleId = sampleId,
                        fieldId = it.fieldId.toLong(),
                        userInput = sampleInfo
                    )
                }

                println("Sample saved successfully")
                // Navigate back
                navController.navigateUp()

            } catch (e: Exception) {
                errorMessage = "Failed to save sample: ${e.message}"
                println("Error saving sample: ${e.message}")
                e.printStackTrace()
            } finally {
                isSaving = false
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
        NavBar(navController)

        Spacer(modifier = Modifier.height(4.dp))

        // Error message display
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
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
                Button(
                    onClick = { refreshLocationAndAltitude() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    if (isLoading == "true") {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black
                        )
                    } else {
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
                onClick = { saveSample() },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF12BF7A)
                ),
                enabled = !isSaving && selectedFormId != null
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Save", color = Color.White)
                }
            }
        }
    }
}