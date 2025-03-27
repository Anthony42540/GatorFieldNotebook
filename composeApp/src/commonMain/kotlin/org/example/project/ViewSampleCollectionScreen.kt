package org.example.project


import KhandFontFamily
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleForm
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object GlobalState {
    var sampleId: Long? = null
}

@Composable
fun ViewSampleCollectionScreen(navController: NavController, database: Database? = null) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var samples by remember { mutableStateOf<List<SampleAndData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var expandedGroups by remember { mutableStateOf(setOf<String>()) }

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

        Header()

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

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Samples") },
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
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Delete All", color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirmation = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0021A5)
                        )
                    ) {
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SectionTitle("Samples")

            Box (
                modifier = Modifier.fillMaxSize()
            ) {
                val groupedSamples = samples.groupBy { database?.getSampleForm(it.formId.toLong())?.formName ?: "Unknown" }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                ) {
                    groupedSamples.forEach { (formName, sampleList) ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable {
                                        expandedGroups = if (expandedGroups.contains(formName)) {
                                            expandedGroups - formName
                                        } else {
                                            expandedGroups + formName
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .animateContentSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Collection: $formName",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Icon(
                                            imageVector = if (expandedGroups.contains(formName)) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand/Collapse"
                                        )
                                    }

                                    if (expandedGroups.contains(formName)) {
                                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                                        sampleList.forEach { sample ->
                                            SampleRow(
                                                sample = sample,
                                                form = database!!.getSampleForm(sample.formId.toLong()),
                                                onSampleClick = { sampleId ->
                                                    GlobalState.sampleId = sampleId
                                                    navController.navigate("sampleDetail/$sampleId")
                                                }
                                            )
                                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionButton("Back", onClick = { navController.navigate("home") }, Color(0xFF0021A5), Color.White)
                    ActionButton("Delete All", onClick = { showDeleteConfirmation = true }, Color.Red, Color.White)
                }
            }
        }
    }
}

private fun clearAllSamples(database: Database?) {
    if (database == null) return

    try {
        database.deleteAllSamples()
    } catch (e: Exception) {
        println("Error clearing samples: ${e.message}")
    }
}

private fun loadAllSamples(
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
private fun SampleRow(sample: SampleAndData, form: SampleForm, onSampleClick: (Long) -> Unit) {
    val pair = formatDate(sample.dateCollectedUTC).split("T")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onSampleClick(sample.sampleId.toLong()) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = form.formName,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = "#${sample.sampleCollectionId}",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
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