package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(title: String) {
    Text(
        style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
        text = title,
        fontSize = 25.sp,
        color = Color(0xFFFFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0021A5))
            .padding(vertical = 6.dp)
            .padding(horizontal = 8.dp)
    )
}