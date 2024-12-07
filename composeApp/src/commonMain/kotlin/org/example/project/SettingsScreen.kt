package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import gatorfieldnotebook.composeapp.generated.resources.Res
import gatorfieldnotebook.composeapp.generated.resources.bluetooth
import org.jetbrains.compose.resources.painterResource




@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavBar(navController) // The navigation bar shows up at the top

        // This is the actual settings screen content
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shows up on screen here
            Text(text = "Settings Screen!")
            Spacer(modifier = Modifier.height(16.dp))
            // Bluetooth devices button
            NavigationImgButton({ BluetoothIcon() }, onClick = { navController.navigate("bluetooth") })
        }

    }
}

@Composable
fun BluetoothIcon() {
    Image(
        painter = painterResource(Res.drawable.bluetooth),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}


