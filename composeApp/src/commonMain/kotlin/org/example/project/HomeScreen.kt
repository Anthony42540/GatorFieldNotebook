package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.foundation.clickable

suspend fun GetCurrentLocation(): GeolocatorResult {
    val geolocator: Geolocator = Geolocator.mobile()
    return geolocator.current(Priority.HighAccuracy)
}

@Composable
fun HomeScreen(navController: NavController, database: Database? = null) {
    var locationState by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        GetCurrentLocation().onSuccess { location ->
            locationState = location
        }.onFailed { exception ->
            locationState = null
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavBar(navController)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecentSubmissionsSection(navController, database)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GoogleMaps(locationState?.coordinates?.latitude.toString(), locationState?.coordinates?.longitude.toString())
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionButton(
                                "New Sample",
                                onClick = { navController.navigate("selectCollection") },
                                Color(0xFF12BF7A),
                                Color.White
                            )
                            ActionButton(
                                "New Form",
                                onClick = { navController.navigate("newForm") },
                                Color(0xFF12BF7A),
                                Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSubmissionsSection(
    navController: NavController,
    database: Database? = null
) {
    var recentSamples by remember { mutableStateOf<List<SampleAndData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (database == null) {
            isLoading = false
            error = "Database not initialized"
            return@LaunchedEffect
        }

        try {
            val allSamples = database.getAllSampleData()
            val recentSampleIds = allSamples
                .sortedByDescending { it.dateCollectedUTC }
                .take(5)
                .map { it.sampleId }

            recentSamples = recentSampleIds.map { sampleId ->
                database.getSampleAndData(sampleId.toLong())
            }
        } catch (e: Exception) {
            error = "Failed to load recent submissions: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recent Submissions",
            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            recentSamples.isEmpty() -> {
                Text(
                    text = "No submissions yet",
                    style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Light),
                    fontSize = 23.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(recentSamples) { sample ->
                        database?.getSampleForm(sample.formId.toLong())
                            ?.let {
                                SampleCard(sample,
                                    it.formName,
                                    onSampleClick = { sampleId ->
                                        navController.navigate("sampleDetail/$sampleId")
                                })
                            }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    onClick = {
                        navController.navigate("viewSampleCollection")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.padding(0.dp),
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Text("View all submissions", color = Color.Black, fontSize = 23.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

@Composable
private fun SampleCard(
    sample: SampleAndData,
    collectionName: String,
    onSampleClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSampleClick(sample.sampleId.toLong()) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Debug print to see what's in dataEntries
            println("DataEntries: ${sample.dataEntries}")

            Text(
                text = "Collection: $collectionName",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            val pair = formatDate(sample.dateCollectedUTC).split("T")

            Text(
                text = "${pair[0]} at ${pair[1]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Text(
                text = "Location: ${formatLocation(sample.location)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        "${localDateTime.month.name.take(3)} ${localDateTime.dayOfMonth}, ${localDateTime.year} " +
                "${localDateTime.hour % 12}:${localDateTime.minute.toString().padStart(2, '0')} " +
                "${if (localDateTime.hour >= 12) "PM" else "AM"}"
    } catch (e: Exception) {
        dateString
    }
}

private fun formatLocation(location: String): String {
    return try {
        val parts = location.split("|")
        parts[0]
    } catch (e: Exception) {
        location
    }
}