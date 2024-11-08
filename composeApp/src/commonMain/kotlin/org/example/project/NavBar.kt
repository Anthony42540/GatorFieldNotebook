package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gatorfieldnotebook.composeapp.generated.resources.Res
import gatorfieldnotebook.composeapp.generated.resources.addSample
import gatorfieldnotebook.composeapp.generated.resources.folders
import gatorfieldnotebook.composeapp.generated.resources.home
import gatorfieldnotebook.composeapp.generated.resources.printer
import gatorfieldnotebook.composeapp.generated.resources.setting
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFF).copy(alpha = 1.0f))
            .padding(8.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gator Field Notebook",
            color = Color(0x000000).copy(alpha = 1.0f),
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

//Icon functions
//source: https://www.youtube.com/watch?v=vnHAT95p0JA&list=PL7W-WmzNxofK8lWAlb-v_6V1d3AOq0kub&index=18
@Composable
fun HomeIcon() {
    Image(
        painter = painterResource(Res.drawable.home),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun AddSampleIcon() {
    Image(
        painter = painterResource(Res.drawable.addSample),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun PrintIcon() {
    Image(
        painter = painterResource(Res.drawable.printer),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun ViewSamplesIcon() {
    Image(
        painter = painterResource(Res.drawable.folders),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun SettingsIcon() {
    Image(
        painter = painterResource(Res.drawable.setting),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}