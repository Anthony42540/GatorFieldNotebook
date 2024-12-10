package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.bluefalcon.ApplicationContext

fun main() = application {
    val blueFalconApp : BlueFalconApplication by lazy {
        BlueFalconApplication(context = ApplicationContext())
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "GatorFieldNotebook",
    ) {
        App(blueFalconApp)
    }
}