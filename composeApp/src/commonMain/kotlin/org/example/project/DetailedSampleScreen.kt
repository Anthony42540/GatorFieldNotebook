package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NavBar(navController)

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
                DetailedSampleContent(sample!!)
            }
        }
    }
}

@Composable
private fun DetailedSampleContent(sample: SampleAndData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionTitle("Sample Details")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Sample ID", sample.sampleId.toString())
                DetailRow("Collection Date", formatDetailedDate(sample.dateCollectedUTC))
                DetailRow("Location", sample.location)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Field Entries",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                sample.dataEntries.forEach { (fieldId, value) ->
                    if(fieldId.toInt() == 1) {
                        DetailRow("Name", value)
                    } else{
                        DetailRow("Info", value)
                    }

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
            text = value,
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