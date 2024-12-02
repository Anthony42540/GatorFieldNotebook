package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.database.cache.DatabaseProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.project.ViewSampleCollectionScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}

@Composable
fun NavigationButton(text: String, onClick: () -> Unit, buttonColor: Color, textColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(text, color = textColor)
    }
}

@Composable
fun NavigationImgButton(icon: @Composable () -> Unit, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        icon()
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Get database instance from DatabaseProvider with error logging
    val database = remember {
        try {
            val db = DatabaseProvider.getInstance().database
            println("Database initialized successfully") // Add debug logging
            db
        } catch (e: Exception) {
            println("Failed to get database: ${e.message}") // Add debug logging
            e.printStackTrace()
            null
        }
    }

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, database)
        }
        composable("editSample") {
            EditSampleScreen(navController, database)
        }
        composable("print") { PrintScreen(navController) }
        composable("viewSampleCollection") { ViewSampleCollectionScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}
@Composable
fun ErrorScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Failed to initialize database")
        Button(onClick = { navController.navigateUp() }) {
            Text("Go Back")
        }
    }
}