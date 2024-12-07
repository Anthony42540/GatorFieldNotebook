package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GatorFieldNotebook",
    ) {
        App()
        initKoin()
    }
}