package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.bluefalcon.ApplicationContext
import org.example.project.BlueFalconApplication
import org.example.project.BluetoothViewModel

class MainActivity : ComponentActivity() {
    private val blueFalconApp : BlueFalconApplication by lazy {
        BlueFalconApplication(context = application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(blueFalconApp)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(BlueFalconApplication(context = ApplicationContext()))
}