package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
            // Shows up on screen here
            Text(text = "Home Screen!")
            // Displays recent sample data for quick selection (dummy data for now).
            RecentSubmissionsSection(navController)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Quick button to add new selection
                NavigationButton("Add new sample", onClick = { navController.navigate("editSample") })
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