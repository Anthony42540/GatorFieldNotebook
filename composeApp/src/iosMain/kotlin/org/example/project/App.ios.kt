package org.example.project

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getScreenWidth(): Int {
    val screenWidth = UIScreen.mainScreen.bounds.useContents { size.width }
    return screenWidth.toInt()
}