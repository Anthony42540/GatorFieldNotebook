package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleForm
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

    val fontSizeVal = if (getScreenWidth() <= 360) 20.sp else 25.sp

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Header()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle("Sample Details")
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
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ){
                // EDIT SAMPLE
                ActionButton(
                    "Edit",
                    onClick = {
                        // Navigate to your EditExistingSampleScreen route
                        // e.g. "editSample/$sampleId"
                        navController.navigate("EditExistingSampleScreen/$sampleId")
                    },
                    buttonColor = Color(0xFF0021A5),
                    textColor = Color.White
                )

                // DELETE SAMPLE
                ActionButton(
                    "Delete",
                    onClick = {
                        if (database != null && sample != null) {
                            database.deleteSample(sampleId)
                            // After deleting, navigate back
                            navController.popBackStack()
                        }
                    },
                    buttonColor = Color(0xFF0021A5),
                    textColor = Color.White
                )
            }
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
                    .padding(16.dp)
            ) {
                ActionButton(
                    "Back",
                    onClick = { navController.popBackStack() },
                    Color(0xFF0021A5),
                    Color.White
                )
                ActionButton(
                    "Print",
                    onClick = { navController.navigate("print") },
                    Color(0xFF12BF7A),
                    Color.White
                )
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
    var form = database?.getSampleForm(sample.formId.toLong())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (form != null) {
                DetailRow("Sample from Collection", form.formName)
            }
            DetailRow("Date Collected", datePair[0])
            DetailRow("Time Collected", datePair[1])
            DetailRow("Coordinates", locationPair[0])
            DetailRow("Altitude", locationPair[1])

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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