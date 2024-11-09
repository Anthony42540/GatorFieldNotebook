package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile

suspend fun GetCurrentLocation(): GeolocatorResult {
    val geolocator: Geolocator = Geolocator.mobile()
    return geolocator.current(Priority.HighAccuracy)
}

@Composable
fun HomeScreen(navController: NavController) {
    val locationState = remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        // Run the suspending function and update the state when the composable is launched
        GetCurrentLocation().onSuccess {
            location -> locationState.value = location
        }.onFailed {
            exception -> locationState.value = null
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavBar(navController) // The navigation bar shows up at the top

        // This is the actual home screen content
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Displays recent sample data for quick selection (dummy data for now).
            RecentSubmissionsSection(navController)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GoogleMaps(locationState.value?.coordinates?.latitude.toString(), locationState.value?.coordinates?.longitude.toString())
                    // Quick button to add new selection
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp)
                    ) {
                        NavigationButton("Add new sample", onClick = { navController.navigate("editSample") })
                    }
                }
            }
        }
    }
}


//Recent submission section for home screen
@Composable
fun RecentSubmissionsSection(navController: NavController) {
    // Dummy data for now
    val recentSubmissions = listOf(
        "Sample 1", "Sample 2", "Sample 3", "Sample 4", "Sample 5"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Recent Submissions", modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn {
            items(recentSubmissions) { submission ->
                Text(
                    text = submission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }
        }

        NavigationButton("View all submissions", onClick = { navController.navigate("viewSampleCollection") })

    }
}