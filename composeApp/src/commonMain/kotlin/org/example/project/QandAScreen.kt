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
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleForm
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun QandAScreen(navController: NavController, database: Database? = null) {



    // scroll state to manage the scrolling
    val scrollState = rememberScrollState()

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

            SectionTitle("Q: What is a sample?")
            Text(
                text = "A sample is a single data collection point recorded in the field. It contains specific information about a specimen or observation, including automatic GPS coordinates (latitude, longitude, altitude), date, time, and any additional custom fields defined in your form. Each sample can be tagged with a unique identifier and printed to a label for physical specimen tracking.",
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            SectionTitle("Q: What is a form?")
            Text(
                text = "A form is a group of related samples that share the same fields, typically gathered during a specific research project or field expedition. All samples are associated with one form, and a form can have multiple samples.",
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Q: How do I print labels?")
            Text(
                text = "After creating a sample, you can print a label by selecting the sample and choosing the 'Print' option. The app will connect to your paired Bluetooth printer and generate a label with the sample's key information.",
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Q: What types of fields can I add to a form?")
            Text(
                text = "• Small text box: For short text entries\n• Large text box: For longer descriptions\n• Numerical: For measurements and other numeric data\n• Dropdown: For selecting one option from a predefined list\n• Multi-select: For choosing multiple options from a list\n\nYou can mark any field as 'Required' to ensure it's filled out for every sample.",
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Q: Can I use the app offline?")
            Text(
                text = "Yes! Gator Field Notebook is designed for field use where internet connectivity may be limited. All data is stored locally on your device first. When you regain internet connectivity, the app will sync your data with the remote Firestore database automatically.",
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5))
            ) {
                Text("Return to Home")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}