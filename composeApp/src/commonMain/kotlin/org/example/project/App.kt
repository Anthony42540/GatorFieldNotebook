package org.example.project

import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.BlueFalconApplication
import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.ApplicationContext


@Composable
@Preview
fun App(blueFalconApplication: BlueFalconApplication) {
    MaterialTheme {
        AppNavigation(blueFalconApplication)
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
        modifier = Modifier.padding(horizontal = 4.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        icon()
    }
}

@Composable
fun AppNavigation(blueFalconApplication: BlueFalconApplication) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("editSample") { EditSampleScreen(navController) }
        composable("print") { PrintScreen(navController) }
        composable("viewSampleCollection") { ViewSampleCollectionScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("bluetooth") {
            BluetoothScreen(navController, blueFalconApplication.bluetoothViewModel)
        }
    }
}