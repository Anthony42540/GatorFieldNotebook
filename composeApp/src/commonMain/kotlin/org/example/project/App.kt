package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import gatorfieldnotebook.composeapp.generated.resources.Res
import gatorfieldnotebook.composeapp.generated.resources.addSample
import gatorfieldnotebook.composeapp.generated.resources.folders
import gatorfieldnotebook.composeapp.generated.resources.home
import gatorfieldnotebook.composeapp.generated.resources.printer
import gatorfieldnotebook.composeapp.generated.resources.setting
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}

@Composable
fun NavigationButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray
        )
    ) {
        Text(text)
    }
}

@Composable
fun NavigationImgButton(icon: @Composable () -> Unit, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray
        )
    ) {
        icon()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("editSample") { EditSampleScreen(navController) }
        composable("print") { PrintScreen(navController) }
        composable("viewSampleCollection") { ViewSampleCollectionScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}


//Icon functions TODO: add to separate file
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