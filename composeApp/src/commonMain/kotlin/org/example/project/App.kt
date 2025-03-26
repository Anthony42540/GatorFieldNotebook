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
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.RemoteDatabase.FirebaseDatabase
import org.example.project.RemoteDatabase.SampleSynchronizer

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