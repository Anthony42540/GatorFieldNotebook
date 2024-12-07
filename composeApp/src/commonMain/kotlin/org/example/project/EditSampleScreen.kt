package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import com.dev.database.cache.ftToStr
import com.dev.database.cache.listToJsonString
import com.dev.database.entity.Field
import com.dev.database.entity.FieldNoID
import dev.jordond.compass.Location
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.example.project.viewModels.CollectionViewModel
import org.example.project.viewModels.FormViewModel

@Composable
fun EditSampleScreen(
    navController: NavController,
    database: Database? = null,
    viewModel: CollectionViewModel,
    collectionValueState: Int
) {
    // Add debug logging
    LaunchedEffect(Unit) {
        println("EditSampleScreen launched")
        println("Database is ${if (database == null) "null" else "not null"}")
    }

    // Mutable state variables for input fields
    var collectionName by remember { mutableStateOf("") }
    var fields by remember { mutableStateOf( listOf<Field>() )}
    var collectedData by remember { mutableStateOf( mapOf<Int, String>()) } //map of field id and user input
    var coordinates by remember { mutableStateOf("") }
    var metersAltitude by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf("") }
    var isCancelling by remember { mutableStateOf(false) }
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

    // Load available forms when screen is first displayed
    LaunchedEffect(Unit) {
        database?.let { db ->
            try {
                //get form name using formID
                collectionName = db.getSampleForm(collectionValueState.toLong()).formName
                //get list of fields for the selected form
                fields = db.getFormFields(collectionValueState.toLong())
            } catch (e: Exception) {
                errorMessage = "Failed to load form: ${e.message}"
                println("Error loading form: ${e.message}")
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

        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null
                println("Starting sample save process")

                // Create location string from coordinates and altitude
                val locationString = "$coordinates|$metersAltitude"

                // Insert sample data
                val sampleId = database.insertSampleData(
                    formId = collectionValueState.toLong(),
                    dateCollectedUtc = "${date}T$time",
                    location = locationString
                )

                collectedData.forEach { (fieldId, userInput) ->
                    database.insertDataEntry(
                        sampleId = sampleId,
                        fieldId = fieldId.toLong(),
                        userInput = userInput
                    )
                }
                println("Sample saved successfully")

                viewModel.clearCollectionID()
                navController.navigate("home")

            } catch (e: Exception) {
                errorMessage = "Failed to save sample: ${e.message}"
                println("Error saving sample: ${e.message}")
                e.printStackTrace()
            } finally {
                isSaving = false
            }
        }
    }

    fun cancel() {
        coroutineScope.launch {
            try {
                isCancelling = true
                errorMessage = null

                viewModel.clearCollectionID()
                navController.navigate("home")

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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = collectionName,
                color = Color(0xFF000000),
                fontSize = 30.sp,
            )
        }

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
        // Date/Time
        TextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text( "Date") },
            readOnly = true
        )
        TextField(
            value = time,
            onValueChange = { time = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Time") },
            readOnly = true
        )

        // Location
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = coordinates,
            onValueChange = { coordinates = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Latitude, Longitude") },
            readOnly = true
        )
        TextField(
            value = metersAltitude,
            onValueChange = { metersAltitude = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Altitude") },
            readOnly = true
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { refreshLocationAndAltitude() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .border(1.dp, Color(0xFF888888), RoundedCornerShape(24.dp))
                    .size(width = 170.dp, height = 50.dp),
            ) {
                if (isLoading == "true") {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Refresh Location", color = Color.Black)
                }
            }
        }

        fields.forEach { field ->
            //displays each field for user input
            DisplayField(
                field = field,
                //maps user input to its field ID for submission (on save)
                onChange = { value ->
                    collectedData = collectedData.toMutableMap().apply {
                        this[field.fieldId] = value
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Button(onClick = { cancel() }) {
                    Text("Cancel")
                }
                // Save Button
                Button(
                    onClick = { saveSample() },
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

//function to display added fields
@Composable
fun DisplayField(
    field: Field,
    onChange: (String) -> Unit
) {
    var collectedData by remember { mutableStateOf("") }
    Column() {
        when (ftToStr(field.fieldType)) {
            "SHORT_STRING", "NUMBER" -> {
                TextField(
                    value = collectedData,
                    onValueChange = {
                        collectedData = it
                        onChange(collectedData)
                    },
                    label = { Text(field.fieldName) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "LONG_STRING" -> {
                TextField(
                    value = collectedData,
                    onValueChange = {
                        collectedData = it
                        onChange(collectedData)
                    },
                    label = { Text(field.fieldName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }

            "DROPDOWN" -> {
                var expanded by remember { mutableStateOf(false) }
                TextField(
                    value = collectedData,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(field.fieldName) },
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        expanded = !expanded
                                    }
                                }
                            }
                        }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        field.options?.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    collectedData = type
                                    onChange(collectedData)
                                    expanded = false
                                },
                                text = {
                                    Text(text = type)
                                }
                            )
                        }
                    }
                }
            }

            "MULTI_SELECT" -> {
                var expanded by remember { mutableStateOf(false) }
                var selectedList by remember { mutableStateOf(listOf<String>()) }
                TextField(
                    value = collectedData.replace("{", "").replace("}", ""),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(field.fieldName) },
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        expanded = !expanded
                                    }
                                }
                            }
                        }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        field.options?.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    if (selectedList.contains(type)) {
                                        selectedList = selectedList.filter { it != type }
                                    } else {
                                        selectedList = selectedList + type
                                    }
                                    collectedData = listToJsonString(selectedList).toString()
                                    onChange(collectedData)
                                    expanded = false
                                },
                                text = {
                                    Text(text = type)
                                }
                            )
                        }
                    }
                }
            }
            else -> Text("Unknown field type")
        }
    }
}