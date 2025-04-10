package org.example.project


import KhandFontFamily
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.dev.database.cache.Database
import com.dev.database.entity.SampleForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ViewAllFormsScreen(navController: NavController, database: Database? = null) {
    var check by remember { mutableStateOf<Boolean?>(null) }
    var formNameVar by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var forms by remember { mutableStateOf<List<SampleForm>>(emptyList()) } //List of all forms

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    //used to update forms in real time when db changes (i.e. form deleted)
    LaunchedEffect(Unit) {
        loadAllForms(database) { newForms, errorMessage ->
            forms = newForms
            error = errorMessage
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Header()

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
            return@Column
        }

        if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Forms") },
                text = { Text("Are you sure you want to delete all forms? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                deactivateAllForms(database)
                                showDeleteConfirmation = false
                                loadAllForms(database) { newForms, errorMessage ->
                                    forms = newForms
                                    error = errorMessage
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)

                    ) {
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Delete All", color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirmation = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0021A5)
                        )
                    ) {
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            SectionTitle("Forms")

            Spacer(modifier = Modifier.height(8.dp))

            Box (
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                ) {

                    forms.forEach { form ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable {
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .animateContentSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Form: ${form.formName}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                check?.let { success ->
                    LaunchedEffect(check) {
                        delay(2000)
                        check = null
                    }

                    Dialog(onDismissRequest = { check = null }) {
                        Card(
                            modifier = Modifier
                                .height(100.dp)
                                .padding(20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = if (success) "$formNameVar was successfully exported to your downloads folder." else "$formNameVar could not be exported to your downloads folder.",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionButton("Back", onClick = { navController.navigate("home") }, Color(0xFF0021A5), Color.White)
                    ActionButton("Delete All", onClick = { showDeleteConfirmation = true }, Color.Red, Color.White)
                }
            }
        }
    }
}

private fun deactivateAllForms(database: Database?) {
    if (database == null) return
    try {
        database.deactivateAllForms()
    } catch (e: Exception) {
        println("Error clearing Forms: ${e.message}")
    }
}

private fun loadAllForms(
    database: Database?,
    onComplete: (List<SampleForm>, String?) -> Unit
) {
    try {
        if (database == null) {
            onComplete(emptyList(), "Database not initialized")
            return
        }

        val allForms = database.getActiveSampleForms()
        onComplete(allForms, null)
    } catch (e: Exception) {
        onComplete(emptyList(), "Error loading forms: ${e.message}")
    }
}
