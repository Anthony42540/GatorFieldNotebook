package org.example.project

import KhandFontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.dev.database.entity.SampleImage
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
    var imageCount by remember { mutableStateOf(0) }
    var images by remember { mutableStateOf<List<SampleImage>>(emptyList()) }

    LaunchedEffect(sampleId) {
        try {
            if (database == null) {
                error = "Database not initialized"
                return@LaunchedEffect
            }

            print("Sample ID for detailed sample: ")
            print(sampleId)
            sample = database.getSampleAndData(sampleId)
            images = database.getSampleImages(sampleId)
          //  database.getImageById()

            // Get image count for this sample
            imageCount = database.getSampleImagesForSample(sampleId).size
            print("Image size:")
            print(images.size)

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

                    // Add Image Button with count

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate("sampleImages/$sampleId")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "View Images ($imageCount)",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                                fontSize = 18.sp
                            )

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
    val datePair = formatDetailedDate(sample.dateCollectedUTC).split("T")
    val locationPair = sample.location.split("|")
    val form = database?.getSampleForm(sample.formId.toLong())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // If the form is known, show the name:
            if (form != null) {
                DetailRow("Sample from Collection", form.formName)
            }
            DetailRow("Collector Name", sample.collectorName)
            // Show the local ID for this collection
            DetailRow("Sample ID", sample.sampleCollectionId.toString())

            // Then show date/time
            DetailRow("Date Collected", datePair[0])
            DetailRow("Time Collected", datePair[1])

            // Coordinates and altitude
            DetailRow("Coordinates", locationPair[0])
            DetailRow("Altitude", locationPair[1])

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Show all data entries for each field
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