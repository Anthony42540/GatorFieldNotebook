package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import com.dev.database.cache.ftToStr
import com.dev.database.entity.FieldNoID
import kotlinx.coroutines.launch
import org.example.project.viewModels.FormViewModel

@Composable
fun NewFormScreen(
    navController: NavController,
    database: Database? = null,
    viewModel: FormViewModel,
    formValueState: String
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    //add debug logging
    LaunchedEffect(Unit) {
        println("NewFormScreen launched")
        println("Form name is $formValueState")
        println("Database is ${if (database == null) "null" else "not null"}")
    }

    fun saveForm() {
        if (database == null) {
            errorMessage = "Database not initialized"
            println("Save failed: Database not initialized")
            return
        }
        if (formValueState.isBlank()) {
            errorMessage = "Form name is required"
            println("Save failed: Form name is blank")
            return
        }
        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null

                //insert new form
                val formId = database.insertSampleForm(
                    formName = formValueState,
                    formActive = 1
                )
                //insert fields for new form, also clears fields list
                database.insertFieldsFromList(formId)
                viewModel.clearFormName()
                navController.navigate("home")
            } catch (e: Exception) {
                errorMessage = "Failed to save sample: ${e.message}"
                println("Error saving sample: ${e.message}")
                navController.navigate("home")
            } finally {
                isSaving = false
            }
        }
    }

    fun cancel() {
        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null

                database?.clearFieldsList()
                viewModel.clearFormName()

                navController.navigate("home")
            } catch (e: Exception) {
                errorMessage = "Failed to cancel form creation: ${e.message}"
                println("Error cancelling form creation: ${e.message}")
                navController.navigate("home")
            } finally {
                isSaving = false
            }
        }
    }

    val fontSizeVal = if (getScreenWidth() <= 360) 20.sp else 25.sp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 70.dp)
                .verticalScroll(state = scrollState),
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
                    style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                    text = "Add New Form",
                    fontSize = 40.sp,
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
            // Form name
            TextField(
                value = formValueState,
                onValueChange = { viewModel.updateFormName(it)
                    println("Form name is $formValueState") },
                label = { Text("Form Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Date/Time
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = "date",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                readOnly = true
            )
            TextField(
                value = "time",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Time") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                readOnly = true
            )

            // Location
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = "coordinates",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Coordinates") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                readOnly = true
            )
            TextField(
                value = "altitude",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Altitude") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                readOnly = true
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                ) {
                    Text("Refresh Location", color = Color.White, fontSize = fontSizeVal, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
                }
            }

            //displays the added fields
            database?.newFieldsList?.forEach { field ->
                FieldDisplay(field)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {

            //cancel button
            Button(
                onClick = { cancel() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5))
            ) {
                Text("Cancel", color = Color.White, fontSize = fontSizeVal, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
            }
            //add new field button
            Button(
                onClick = {
                    navController.navigate("addField")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5))
            ) {
                Text(text = "Add New Field", color = Color.White, fontSize = fontSizeVal, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
            }
            //save button
            Button(
                onClick = { saveForm() },
                enabled = !isSaving && formValueState.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Save", color = Color.White, fontSize = fontSizeVal, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

//function to display added fields
@Composable
fun FieldDisplay(field: FieldNoID) {
    Column {
        when (ftToStr(field.fieldType)) {
            "SHORT_STRING", "NUMBER" -> {
                TextField(
                    value = field.fieldName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text( field.fieldName ) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }
            "LONG_STRING" -> {
                TextField(
                    value = field.fieldName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text( field.fieldName ) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    )
                )
            }
            "DROPDOWN", "MULTI_SELECT" -> {
                var expanded by remember { mutableStateOf(false) }
                TextField(
                    value = field.fieldName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text( field.fieldName ) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0021A5),
                        unfocusedBorderColor = Color(0xFF0021A5),
                        focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                        unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                    ),
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
                            .background(Color(0xFFFFFFFF))
                            .border(1.dp, Color(0xFF0021A5))
                    ) {
                        field.options?.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                },
                                text = {
                                    Text(text = type)
                                }
                            )
                        }
                    }
                }
            }
            else -> Text("Unknown field type")
        }
    }
}