package org.example.project

import KhandFontFamily
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState  // Add this import
import androidx.compose.foundation.verticalScroll      // Add this import
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleForm
import com.dev.database.entity.SampleImage
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.mohamedrejeb.calf.core.LocalPlatformContext
import androidx.compose.ui.layout.ContentScale


@Composable
fun SampleImagesScreen(navController: NavController, database: Database?, sampleId: Long) {


    var images by remember { mutableStateOf<List<SampleImage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<SampleImage?>(null) }

    val scrollState = rememberScrollState()
    // Load images when the screen is first displayed
    LaunchedEffect(sampleId) {
        try {
            if (database == null) {
                error = "Database not initialized"
                return@LaunchedEffect
            }

            images = database.getSampleImagesForSample(sampleId)
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load images: ${e.message}"
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp)
                .verticalScroll(scrollState),  // Add this modifier to make it scrollable
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            SectionTitle("Sample Images")
            var count = 0
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between items
            ) {
                for(item in images){
                    print("Image ")
                    print(count)
                    count += 1

                    // Wrap each image in a Card for better visibility
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            AsyncImage(
                                model = item.imageData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Sample image ${count}"
                            )

                            // Optional: Add an image number indicator
                            Text(
                                text = "Image ${count}",
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black)
                                    .padding(4.dp)
                            )
                        }
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
                        .padding(16.dp)
                ) {
                    ActionButton(
                        "Back",
                        onClick = { navController.popBackStack() },
                        Color(0xFF0021A5),
                        Color.White
                    )

                }
            }


        }
    }
}