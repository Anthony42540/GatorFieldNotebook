package org.example.project

import KhandFontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dev.database.entity.FieldNoID
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import com.dev.database.cache.readableToFT
import gatorfieldnotebook.composeapp.generated.resources.Res
import gatorfieldnotebook.composeapp.generated.resources.close
import gatorfieldnotebook.composeapp.generated.resources.home
import kotlinx.coroutines.launch
import org.example.project.viewModels.FormViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddFieldScreen(
    navController: NavController,
    database: Database? = null
) {
    var fieldName by remember { mutableStateOf("") }
    var isRequired by remember { mutableStateOf(false) }
    var fieldType by remember { mutableStateOf<String?>(null) }
    var options by remember { mutableStateOf<List<String>>(emptyList()) }
    var newOption by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val fieldTypes = listOf("small text box", "large text box", "numerical", "dropdown", "multi-select")
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    fun cancel() {
        coroutineScope.launch {
            try {
                navController.navigate("newForm")
            } catch (e: Exception) {
                errorMessage = "Failed to cancel field creation: ${e.message}"
                println("Error cancelling field creation: ${e.message}")
                navController.navigate("newForm")
            } finally {
                isSaving = false
            }
        }
    }

    fun saveField() {
        if (database == null) {
            errorMessage = "Database not initialized"
            println("Save failed: Database not initialized")
            return
        }
        if (fieldName.isBlank()) {
            errorMessage = "Field Name is required"
            println("Save failed: Field name is blank")
            return
        }
        coroutineScope.launch {
            try {
                isSaving = true
                errorMessage = null

                if (fieldName.isNotBlank() && fieldType != null) {

                    database.newFieldsList.add(
                        FieldNoID(
                            fieldName = fieldName,
                            fieldType = readableToFT(fieldType!!),
                            isRequired = isRequired,
                            options = if (fieldType == "dropdown" || fieldType == "multi-select") options else null
                        )
                    )
                }
                navController.navigate("newForm")

            } catch (e: Exception) {
                errorMessage = "Failed to save field: ${e.message}"
                println("Error saving field: ${e.message}")
                navController.navigate("newForm")
            } finally {
                isSaving = false
            }
        }
    }

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
                style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                text = "Add New Field",
                color = Color(0x000000).copy(alpha = 1.0f),
                fontSize = 40.sp,
            )
        }

        TextField(
            value = fieldName,
            onValueChange = { fieldName = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0021A5),
                unfocusedBorderColor = Color(0xFF0021A5),
                focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
            ),
            label = { Text("Field Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Required")
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = isRequired,
                onCheckedChange = { isRequired = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = fieldType ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Field Type") },
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
                fieldTypes.forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            fieldType = type
                            expanded = false
                        },
                        text = {
                            Text(text = type)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (fieldType == "dropdown" || fieldType == "multi-select") {
            TextField(
                value = newOption,
                onValueChange = { newOption = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0021A5),
                    unfocusedBorderColor = Color(0xFF0021A5),
                    focusedContainerColor = Color(0xFF0021A5).copy(0.1f),
                    unfocusedContainerColor = Color(0xFF0021A5).copy(0.1f)
                ),
                label = { Text("Add $fieldType option") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    options = options + newOption
                    newOption = ""
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp)
                    .size(width = 160.dp, height = 45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Add Option", color = Color.White, fontSize = 25.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
            }

            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(24.dp)),
                ) {
                    Button(
                        onClick = {
                            options = options.filter { it != option }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    )
                    {
                        Image(
                            painter = painterResource(Res.drawable.close),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = option,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                ActionButton("Cancel", onClick = { cancel() }, Color(0xFF0021A5), Color.White)
                Button(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(width = 160.dp, height = 45.dp),
                    onClick = { saveField() },
                    enabled = !isSaving && fieldName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "Save", color = Color.White, fontSize = 25.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}