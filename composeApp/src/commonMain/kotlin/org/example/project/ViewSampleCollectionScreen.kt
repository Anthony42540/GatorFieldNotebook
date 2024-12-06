package org.example.project


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Sample(val name: String, val id: String, val date: String)

@Composable
fun ViewSampleCollectionScreen(navController: NavController, database: Database? = null) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var samples by remember { mutableStateOf<List<SampleAndData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loadAllSamples(database) { newSamples, errorMessage ->
            samples = newSamples
            error = errorMessage
            isLoading = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavBar(navController)



        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
            return@Column
        }

        if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        Button(
            onClick = { showDeleteConfirmation = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Text("Clear All Samples", color = Color.White)
        }


        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Clear All Samples") },
                text = { Text("Are you sure you want to delete all samples? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                clearAllSamples(database)
                                showDeleteConfirmation = false
                                // Refresh the screen after deletion
                                loadAllSamples(database) { newSamples, errorMessage ->
                                    samples = newSamples
                                    error = errorMessage
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete All", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SectionTitle("Samples")

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(samples) { sample ->
                    SampleRow(
                        sample = sample,
                        onSampleClick = { sampleId ->
                            navController.navigate("sampleDetail/$sampleId")
                        }
                    )
                }
            }
        }
    }
}

private suspend fun clearAllSamples(database: Database?) {
    if (database == null) return

    try {
        database.deleteAllSamples()
    } catch (e: Exception) {
        println("Error clearing samples: ${e.message}")
    }
}

private suspend fun loadAllSamples(
    database: Database?,
    onComplete: (List<SampleAndData>, String?) -> Unit
) {
    try {
        if (database == null) {
            onComplete(emptyList(), "Database not initialized")
            return
        }

        val allSamples = database.getAllSampleData()
        val sampleDetails = allSamples.map { sample ->
            database.getSampleAndData(sample.sampleId.toLong())
        }
        onComplete(sampleDetails, null)
    } catch (e: Exception) {
        onComplete(emptyList(), "Error loading samples: ${e.message}")
    }
}

@Composable
private fun SampleRow(sample: SampleAndData, onSampleClick: (Long) -> Unit) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                .clickable { onSampleClick(sample.sampleId.toLong()) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        val sampleName = sample.dataEntries.values.firstOrNull() ?: "Unnamed Sample"

        Text(
            text = sampleName,
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )
        Text(
            text = sample.sampleId.toString(),
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )
        Text(
            text = formatDate(sample.dateCollectedUTC),
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit sample",
            modifier = Modifier
                .size(24.dp)
                .padding(8.dp),
            tint = Color.Black
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        "${localDateTime.monthNumber.toString().padStart(2, '0')} - " +
                "${localDateTime.dayOfMonth.toString().padStart(2, '0')} - " +
                "${localDateTime.year}"
    } catch (e: Exception) {
        dateString
    }
}