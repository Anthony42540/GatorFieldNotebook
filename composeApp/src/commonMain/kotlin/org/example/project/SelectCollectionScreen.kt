package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.example.project.viewModels.CollectionViewModel

@Composable
fun SelectCollectionScreen(
    navController: NavController,
    database: Database? = null,
    viewModel: CollectionViewModel,
    collectionValueState: Int
) {
    // Add debug logging
    LaunchedEffect(Unit) {
        println("SelectCollection launched")
        println("Database is ${if (database == null) "null" else "not null"}")
    }

    var selectedCollection by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isCancelling by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Available forms for selection
    var availableForms by remember { mutableStateOf(emptyMap<Int, String>()) }

    // Load available forms when screen is first displayed
    LaunchedEffect(Unit) {
        database?.let { db ->
            try {
                println("Loading available forms")
                val forms = db.getAllSampleForms()
                //map list of forms to available forms map
                availableForms = forms.associate { it.formId to it.formName }
                selectedCollection = forms.first().formName
                if (forms.isNotEmpty()) {
                    viewModel.updateCollectionID(forms.first().formId)
                }
                println("Loaded ${forms.size} forms")
            } catch (e: Exception) {
                errorMessage = "Failed to load forms: ${e.message}"
                println("Error loading forms: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun onNext() {
        if (database == null) {
            errorMessage = "Database not initialized"
            println("Save failed: Database not initialized")
            return
        }

        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null

                navController.navigate("editSample")

            } catch (e: Exception) {
                errorMessage = "Failed to save sample: ${e.message}"
                println("Error saving sample: ${e.message}")
                e.printStackTrace()
            } finally {
                isSaving = false
            }
        }
    }

    fun onCancel() {
        coroutineScope.launch {
            try {
                isCancelling = true
                errorMessage = null

                viewModel.clearCollectionID()
                navController.navigate("home")

            } catch (e: Exception) {
                errorMessage = "Failed to cancel properly: ${e.message}"
                e.printStackTrace()
            } finally {
                isCancelling = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Collection",
                    color = Color(0x000000).copy(alpha = 1.0f),
                    fontSize = 30.sp,
                )
            }

            // Error message display
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start

            ) {
                TextField(
                    value = selectedCollection,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choose Collection") },
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        expanded = !expanded
                                    }
                                }
                            }
                        }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        availableForms.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.updateCollectionID(type.key)
                                    selectedCollection = type.value
                                    expanded = false
                                },
                                text = {
                                    Text(text = type.value)
                                }
                            )
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // cancel Button
            Button(
                onClick = { onCancel() },
                enabled = !isCancelling
            ) {
                Text(text = "Cancel", color = Color.White)
            }
            // next button
            Button(
                onClick = { onNext() },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Next", color = Color.White)
                }
            }
        }
    }
}