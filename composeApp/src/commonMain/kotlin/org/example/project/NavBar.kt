package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NavBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x400021A5))
            .padding(8.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gator Field Notebook",
            color = Color(0xFF0021A5),
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationImgButton({ HomeIcon() }, onClick = { navController.navigate("home") })
            NavigationImgButton({ AddSampleIcon() }, onClick = { navController.navigate("editSample") })
            NavigationImgButton({ PrintIcon() }, onClick = { navController.navigate("print") })
            NavigationImgButton({ ViewSamplesIcon() }, onClick = { navController.navigate("viewSampleCollection") })
            NavigationImgButton({ SettingsIcon() }, onClick = { navController.navigate("settings") })
        }
    }
}