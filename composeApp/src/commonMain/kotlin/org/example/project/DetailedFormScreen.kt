package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.dev.database.entity.Field
import com.dev.database.entity.FieldNoID
import kotlinx.coroutines.launch
import org.example.project.viewModels.FormViewModel

@Composable
fun DetailedFormScreen(
    navController: NavController,
    database: Database? = null,
    formId: Long
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var fields by remember { mutableStateOf<List<Field>>(emptyList()) }

    LaunchedEffect(formId) {
        try {
            if (database == null) {
                error = "Database not initialized"
                return@LaunchedEffect
            }

            fields = database.getFormFields(formId)

            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load fields: ${e.message}"
            isLoading = false
        }
    }

    fun cancel() {
        coroutineScope.launch {
        errorMessage = null

        navController.navigate("viewAllForms")
        }
    }

    val fontSizeVal = if (getScreenWidth() <= 360) 20.sp else 25.sp

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 70.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Text(
                    style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                    text = "Form: ",
                    color = Color(0x000000).copy(alpha = 1.0f),
                    fontSize = 40.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Date/Time
            item {
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
            }
            item {
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
            }

            // Location
            item {
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
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
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
            }

            //displays the added fields
            items(fields) { field ->
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
        }
    }
}