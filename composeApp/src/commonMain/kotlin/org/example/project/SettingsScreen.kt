package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings Screen")
        NavigationButton("Back to Home",onClick = { navController.navigate("home") })
        NavigationButton("Edit Sample",onClick = { navController.navigate("editSample") })
        NavigationButton("Print",onClick = { navController.navigate("print") })
        NavigationButton("View Sample Collection",onClick = { navController.navigate("viewSampleCollection") })
        NavigationButton("Settings",onClick = { navController.navigate("settings") })
    }
}