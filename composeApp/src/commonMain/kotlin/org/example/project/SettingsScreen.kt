package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text


@Composable
fun SettingsScreen(navController: NavController) {
    var temperatureUnit by remember { mutableStateOf("Celsius") }
    var distanceUnit by remember { mutableStateOf("Feet") }
    var fontSize by remember { mutableStateOf("Medium") }
    var fontStyle by remember { mutableStateOf("Regular") }
    var orientation by remember { mutableStateOf("Portrait") }
    var labelMargins by remember { mutableStateOf("Normal") }
    var theme by remember { mutableStateOf("Light") }
    var darkMode by remember { mutableStateOf("Off") }
    val appVersion = "1.1.1"
    val userGuideLink = "link"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavBar(navController)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // General Settings Section
            SettingsSectionTitle("General Settings")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownField("Temperature", temperatureUnit, listOf("Celsius", "Fahrenheit")) { temperatureUnit = it }
                DropdownField("Distance/Altitude", distanceUnit, listOf("Feet", "Meters")) { distanceUnit = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Printer Settings Section
            SettingsSectionTitle("Printer Settings")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownField("Font Size", fontSize, listOf("Small", "Medium", "Large")) { fontSize = it }
                DropdownField("Font Style", fontStyle, listOf("Regular", "Bold", "Italic")) { fontStyle = it }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownField("Orientation", orientation, listOf("Portrait", "Landscape")) { orientation = it }
                DropdownField("Label Margins", labelMargins, listOf("Small", "Normal", "Large")) { labelMargins = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Appearance Section
            SettingsSectionTitle("App Appearance")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownField("Theme", theme, listOf("Light", "Dark", "System Default")) { theme = it }
                DropdownField("Dark Mode", darkMode, listOf("On", "Off")) { darkMode = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            SettingsSectionTitle("About")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = appVersion,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    label = { Text("App Version") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = userGuideLink,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    label = { Text("User Guide") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = { /* Handle save action */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0021A5)
                )
            ) {
                Text(text = "Save", color = Color.White)
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0021A5))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    )
}

@Composable
fun DropdownField(label: String, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.width(160.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
                .clickable { expanded = true }
        ) {
            Text(text = selectedOption, fontSize = 16.sp, color = Color.Black)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) }, // Pass 'text' parameter here as a lambda
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


