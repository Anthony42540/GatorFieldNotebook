package org.example.project

import KhandFontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.getName
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


@Composable
fun ImageUploadScreen(navController: NavController, database: Database, sampleId: Int) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var imageData by remember { mutableStateOf(ByteArray(0))}


    var uploadStatus by remember { mutableStateOf("") }

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { file ->
                    try {

                        // Read the file as a ByteArray
                        imageData = file.readByteArray(context)

                        // Get the file name and type
                        // Get the file name and type
                        val imageName = file.getName(context) ?: "image"
                        val imageType = when {
                            imageName.toString().lowercase().endsWith("jpg") ||
                                    imageName.toString().lowercase().endsWith("jpeg") -> "image/jpeg"
                            imageName.toString().lowercase().endsWith("png") -> "image/png"
                            imageName.toString().lowercase().endsWith("gif") -> "image/gif"
                            else -> "image/unknown"
                        }



                        // Get the current timestamp
                        val timestamp = Clock.System.now().toEpochMilliseconds().toString()

                        // Insert the image into the database
                        database.insertSampleImage(
                            sampleId = sampleId.toLong(),
                            imageData = imageData,
                            imageName = imageName,
                            imageType = imageType,
                            timestamp = timestamp
                        )
                        print("Sample ID for the image: ")
                        print(sampleId)

                        uploadStatus = "Image uploaded successfully"

                    } catch (e: Exception) {
                        uploadStatus = "Error uploading image: ${e.message}"
                        println("Didnt upload image")
                    }
                }
            }
        }
    )

    print("entered image upload screen")
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                pickerLauncher.launch()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5)),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text("Upload Image", fontSize = 25.sp, style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium))
        }



        if (uploadStatus.isNotEmpty()) {
            Text(
                text = uploadStatus,
                color = if (uploadStatus.startsWith("Error")) Color.Red else Color.Green,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}