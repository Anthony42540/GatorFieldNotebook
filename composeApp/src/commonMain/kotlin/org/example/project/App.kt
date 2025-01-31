package org.example.project

import KhandFontFamily
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.database.cache.DatabaseProvider
import org.example.project.viewModels.CollectionViewModel
import org.example.project.viewModels.FormViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}

@Composable
expect fun getScreenWidth(): Int

@Composable
fun ActionButton(text: String, onClick: () -> Unit, buttonColor: Color, textColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(width = 160.dp, height = 45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, color = textColor, fontSize = 25.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
    }
}

@Composable
fun NavigationImgButton(icon: @Composable () -> Unit, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(24.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        icon()
    }
}
@Composable
fun AppNavigation() {
    KoinContext{
        val formViewModel = koinViewModel<FormViewModel>()
        val formValueState by formViewModel.formName.collectAsState()

        val collectionViewModel = koinViewModel<CollectionViewModel>()
        val collectionValueState by collectionViewModel.collectionID.collectAsState()

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
            composable("selectCollection") {
                SelectCollectionScreen(
                    navController,
                    database,
                    collectionViewModel,
                    collectionValueState
                )
            }
            composable("editSample") {
                EditSampleScreen(navController, database, collectionViewModel, collectionValueState)
            }
            composable("print") {
                PrintScreen(navController)
            }
            composable("viewSampleCollection") {
                ViewSampleCollectionScreen(navController)
            }
            composable("newForm") {
                NewFormScreen(navController, database, formViewModel, formValueState)
            }
            composable("addField") {
                AddFieldScreen(navController, database)
            }
            composable("print") {
                PrintScreen(navController)
            }
            composable("viewSampleCollection") {
                ViewSampleCollectionScreen(navController, database)
            }
            composable(
                route = "sampleDetail/{sampleId}",
                arguments = listOf(navArgument("sampleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sampleId = backStackEntry.arguments?.getLong("sampleId") ?: return@composable
                DetailedSampleScreen(
                    navController = navController,
                    database = database,
                    sampleId = sampleId
                )
            }
        }
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