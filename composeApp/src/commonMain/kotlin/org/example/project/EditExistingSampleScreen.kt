package org.example.project

import KhandFontFamily
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import com.dev.database.cache.ftToStr
import com.dev.database.cache.listToJsonString
import com.dev.database.entity.Field
import dev.jordond.compass.Location
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.viewModels.CollectionViewModel

@Composable
fun EditExistingSampleScreen(
    navController: NavController,
    database: Database?,
    sampleId: Long,  // pass this via navController.navigate("editSample/$sampleId")
    viewModel: CollectionViewModel
) {
    var collectionName by remember { mutableStateOf("") }
    var fields by remember { mutableStateOf(listOf<Field>()) }
    // Map<fieldId, userInput>
    var collectedData by remember { mutableStateOf(mapOf<Int, String>()) }
    var coordinates by remember { mutableStateOf("") }
    var metersAltitude by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // We’ll also store date/time in separate strings for convenience
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    // 1) On launch, load the existing sample from DB
    LaunchedEffect(sampleId) {
        if (database == null) {
            errorMessage = "Database not initialized"
            return@LaunchedEffect
        }
        try {
            // load existing sample info
            val sampleAndData = database.getSampleAndData(sampleId)
            val form = database.getSampleForm(sampleAndData.formId.toLong())
            collectionName = form.formName

            // parse date/time if you store them in an ISO format like "YYYY-MM-DDTHH:MM:SS"
            val dateTimeParts = sampleAndData.dateCollectedUTC.split("T")
            date = dateTimeParts.getOrNull(0) ?: ""
            time = dateTimeParts.getOrNull(1) ?: ""

            // set location fields
            // sampleAndData.location might be something like "lat,long|alt"
            val locationParts = sampleAndData.location.split("|")
            coordinates = locationParts.getOrNull(0) ?: ""
            metersAltitude = locationParts.getOrNull(1) ?: ""

            // load fields for that form
            fields = database.getFormFields(sampleAndData.formId.toLong())

            // existing data from DB -> stored in sampleAndData.dataEntries
            // dataEntries is a Map<Long, String>
            // we want Map<Int, String> for Compose
            collectedData = sampleAndData
                .dataEntries
                .mapKeys { it.key.toInt() }  // convert Long -> Int
        } catch (e: Exception) {
            errorMessage = "Failed to load sample: ${e.message}"
        }
    }

    // 2) Save changes (update DB)
    fun saveEdits() {
        if (database == null) {
            errorMessage = "Database not initialized"
            return
        }
        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null

                // Recombine date/time into something like "2023-09-30T12:30:00"
                val dateTimeString = "${date}T${time}"
                val locationString = "$coordinates|$metersAltitude"

                // 2A) Update the sample in SampleData
                database.updateSampleData(
                    sampleId = sampleId,
                    newDateCollectedUtc = dateTimeString,
                    newLocation = locationString
                )

                // 2B) Update each changed DataEntry
                collectedData.forEach { (fieldId, userInput) ->
                    database.updateDataEntry(
                        sampleId = sampleId,
                        fieldId = fieldId.toLong(),
                        newUserInput = userInput
                    )
                }
                // Optionally clear your viewModel or do any needed post-update logic
                viewModel.clearCollectionID()
                // navigate back to Home or wherever
                navController.navigate("home")

            } catch (e: Exception) {
                errorMessage = "Error saving edits: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }

    // 3) Cancel or "Back" button logic
    fun cancel() {
        navController.popBackStack()  // Just go back
    }

    // 4) UI
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 70.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Sample\n$collectionName",
                        style = TextStyle(
                            fontFamily = KhandFontFamily(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 30.sp
                        ),
                        color = Color(0xFF000000)
                    )
                }
            }

            // Display any error
            item {
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Date / Time
            item {
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }
            item {
                TextField(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Time") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }

            // Coordinates
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TextField(
                    value = coordinates,
                    onValueChange = { coordinates = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Latitude, Longitude") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }
            item {
                TextField(
                    value = metersAltitude,
                    onValueChange = { metersAltitude = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Altitude") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }

            // Display each field with existing userInput
            items(fields) { field ->
                val currentValue = collectedData[field.fieldId] ?: ""
                EditField(
                    field = field,
                    initialValue = currentValue,
                    onValueChange = { newValue ->
                        collectedData = collectedData.toMutableMap().apply {
                            this[field.fieldId] = newValue
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Buttons at bottom
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // BACK BUTTON with custom color
            Button(
                onClick = { cancel() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                modifier = Modifier.size(width = 160.dp, height = 45.dp)
            ) {
                Text("Back", color = Color.White, fontSize = 20.sp)
            }

            // SAVE BUTTON with custom color
            Button(
                onClick = { saveEdits() },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                modifier = Modifier.size(width = 160.dp, height = 45.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun EditField(
    field: Field,
    initialValue: String,
    onValueChange: (String) -> Unit
) {
    var localValue by remember { mutableStateOf(initialValue) }

    Column {
        when (ftToStr(field.fieldType)) {
            "SHORT_STRING", "NUMBER" -> {
                TextField(
                    value = localValue,
                    onValueChange = {
                        localValue = it
                        onValueChange(it)
                    },
                    readOnly = false,
                    label = { Text(field.fieldName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    ),
                )
            }

            "LONG_STRING" -> {
                TextField(
                    value = localValue,
                    onValueChange = {
                        localValue = it
                        onValueChange(it)
                    },
                    label = { Text(field.fieldName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    readOnly = false,
                    // Same custom colors:
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }

            "DROPDOWN" -> {
                var expanded by remember { mutableStateOf(false) }

                TextField(
                    value = localValue,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(field.fieldName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            // Toggle 'expanded' when user taps the text field
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect { interaction ->
                                    if (interaction is PressInteraction.Release) {
                                        expanded = !expanded
                                    }
                                }
                            }
                        }
                )

                // Show menu when 'expanded' is true
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    field.options?.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                // Update localValue with the user’s selection
                                localValue = option
                                onValueChange(option) // tell the parent composable
                                expanded = false
                            },
                            text = { Text(text = option) }
                        )
                    }
                }
            }
        }
    }
}
