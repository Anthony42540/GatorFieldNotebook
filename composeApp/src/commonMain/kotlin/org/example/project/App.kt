package org.example.project

import KhandFontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.navDeepLink
import com.dev.database.cache.DatabaseProvider
import org.example.project.RemoteDatabase.SampleSynchronizer
import org.example.project.viewModels.CollectionViewModel
import org.example.project.viewModels.FormViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val database = remember {
        try {
            val db = DatabaseProvider.getInstance().database
            println("Local database initialized successfully")
            db
        } catch (e: Exception) {
            println("Failed to get local database: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    LaunchedEffect(Unit) {
        try {
            println("Initializing Firebase and starting synchronization...")

            database?.let { db ->
                val synchronizer = SampleSynchronizer(db)

                // Perform initial sync
                synchronizer.syncLocalToFirebase()
                println("Successfully synchronized local to remote database")
                synchronizer.syncFirebaseToLocal()
                println("Successfully synchronized remote to local database")

                println("Firebase and synchronization initialized successfully")
            } ?: throw Exception("Database is null")

        } catch (e: Exception) {
            println("Failed to initialize Firebase/sync: ${e.message}")
            e.printStackTrace()
        }
    }

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
            composable(
                route = "print/{sampleId}",
                arguments = listOf(navArgument("sampleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sampleId = backStackEntry.arguments?.getLong("sampleId") ?: return@composable
                PrintScreen(
                    navController = navController,
                    database = database,
                    sampleId = sampleId
                )
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
            composable("viewSampleCollection") {
                ViewSampleCollectionScreen(navController, database)
            }
            composable("QandA_screen") {
                QandAScreen(navController, database)
            }
            composable("viewAllForms") {
                ViewAllFormsScreen(navController, database)
            }
            composable(
                route = "sampleDetail/{sampleId}",
                arguments = listOf(navArgument("sampleId") { type = NavType.LongType }),
                deepLinks = listOf(navDeepLink { uriPattern = "myapp://sample/{sampleId}" })
            ) { backStackEntry ->
                val sampleId = backStackEntry.arguments?.getLong("sampleId") ?: return@composable
                DetailedSampleScreen(
                    navController = navController,
                    database = database,
                    sampleId = sampleId
                )
            }
            composable(
                route = "formDetail/{formId}",
                arguments = listOf(navArgument("formId") { type = NavType.LongType })
            ) { backStackEntry ->
                val formId = backStackEntry.arguments?.getLong("formId") ?: return@composable
                DetailedFormScreen(
                    navController = navController,
                    database = database,
                    formId = formId
                )
            }
            composable(
                route = "sampleImages/{sampleId}",
                arguments = listOf(navArgument("sampleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sampleId = backStackEntry.arguments?.getLong("sampleId") ?: return@composable
                SampleImagesScreen(
                    navController = navController,
                    database = database,
                    sampleId = sampleId
                )
            }
            composable(
                route = "EditExistingSampleScreen/{sampleId}"
            ) { backStackEntry ->
                val sampleIdArg = backStackEntry.arguments
                    ?.getString("sampleId")
                    ?.toLongOrNull() ?: 0L
                EditExistingSampleScreen(
                    navController = navController,
                    database = database,
                    sampleId = sampleIdArg,
                    viewModel = collectionViewModel
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