package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit

data class Sample(val name: String, val id: String, val date: String)

@Composable
fun ViewSampleCollectionScreen(navController: NavController) {
    var collectionName by remember { mutableStateOf("") }
    val sampleList = listOf(
        Sample("Moth 1", "1234", "09 - 25 - 2024"),
        Sample("Moth 2", "4432", "09 - 02 - 2024"),
        Sample("Moth 3", "5322", "07 - 18 - 2024"),
        Sample("Moth 4", "7987", "06 - 05 - 2024"),
        Sample("Moth 5", "6543", "09 - 25 - 2024"),
        Sample("Moth 6", "9088", "09 - 02 - 2024"),
        Sample("Moth 7", "9435", "07 - 18 - 2024"),
        Sample("Moth 8", "1663", "06 - 05 - 2024"),
        Sample("Moth 9", "9345", "09 - 25 - 2024"),
        Sample("Moth 10", "4873", "09 - 02 - 2024"),
        Sample("Moth 11", "8445", "07 - 18 - 2024")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NavBar(navController)

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Choose Collection Section
            SectionTitle("Choose Collection")
            TextField(
                value = collectionName,
                onValueChange = { collectionName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Collection") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Samples List Section
            SectionTitle("Samples")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sample ID",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Date",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(sampleList) { sample ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sample.name,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                        Text(
                            text = sample.id,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                        Text(
                            text = sample.date,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).padding(8.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}