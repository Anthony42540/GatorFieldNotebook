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
import com.dev.database.entity.SampleAndData
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DetailedSampleScreen(
    navController: NavController,
    database: Database?,
    sampleId: Long
) {
    var sample by remember { mutableStateOf<SampleAndData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sampleId) {
        try {
            if (database == null) {
                error = "Database not initialized"
                return@LaunchedEffect
            }

            sample = database.getSampleAndData(sampleId)
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load sample: ${e.message}"
            isLoading = false
        }
    }

    fun back() {
        navController.navigate("viewSampleCollection")
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
                text = "Sample Details",
                color = Color(0xFF000000),
                fontSize = 30.sp,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                error != null -> {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                sample != null -> {
                    DetailedSampleContent(sample!!, database)
                }
            }
        }
        Button(
            onClick = {}, //TODO: Add view all samples for specified collection screen
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
        ) {
            Text("View all samples for this collection", color = Color.Black)
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
                Button(onClick = { back() }) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
private fun DetailedSampleContent(
    sample: SampleAndData,
    database: Database?
) {
    var datePair = formatDetailedDate(sample.dateCollectedUTC).split("T")
    var locationPair = sample.location.split("|")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailRow("Sample ID", sample.sampleId.toString())
            DetailRow("Date Collected", datePair[0])
            DetailRow("Time Collected", datePair[1])
            DetailRow("Coordinates", locationPair[0])
            DetailRow("Altitude", locationPair[1])

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            sample.dataEntries.forEach { (fieldId, value) ->
                val field = database?.getFieldByID(fieldId)
                if (field != null) {
                    DetailRow(field.fieldName, value)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value.replace("{", "").replace("}", ""),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDetailedDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        "${localDateTime.month} ${localDateTime.dayOfMonth}, ${localDateTime.year} at " +
                "${localDateTime.hour}:${localDateTime.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        dateString
    }
}