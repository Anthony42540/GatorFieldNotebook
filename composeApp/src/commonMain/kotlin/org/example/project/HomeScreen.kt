package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


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

        MenuHeader(content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RecentSubmissionsSection(navController, database)

                Column(
                    modifier = Modifier.fillMaxSize().padding(15.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(380.dp)
                            .clip(RoundedCornerShape(25.dp))
                    ) {
                        GoogleMaps(
                            locationState?.coordinates?.latitude.toString(),
                            locationState?.coordinates?.longitude.toString()
                        )
                    }
                }
            }
        },
            navController = navController,
            database=database
        )
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
            error = "Failed to load recent samples: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
            .padding(horizontal = 10.dp)
    ) {

        Text(
            text = "Recent Samples",
            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
            fontSize = 25.sp,
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No samples yet",
                        style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Light),
                        fontSize = 23.sp,
                        modifier = Modifier.padding(vertical = 25.dp),
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.height(380.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = collectionName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = "#${sample.sampleCollectionId}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val pair = formatDate(sample.dateCollectedUTC).split("T")

            Text(
                text = "Date/Time: ${pair[0]} at ${pair[1]}",
                style = MaterialTheme.typography.bodySmall,
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
                if (localDateTime.hour >= 12) "PM" else "AM"
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