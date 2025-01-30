package org.example.project

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.dev.database.cache.AndroidDatabaseProvider
import com.dev.database.cache.DatabaseProvider
import com.dev.database.cache.DatabaseSetup
import kotlinx.coroutines.launch
import org.example.project.bluetooth.BluetoothManager
import dev.bluefalcon.ApplicationContext
//import dev.bluefalcon.BlueFalcon

class MainActivity : ComponentActivity() {
//    private val blueFalconApp : BlueFalconApplication by lazy {
//        BlueFalconApplication(context = application)
//    }
    private val blueFalconManager : BluetoothManager by lazy {
        BluetoothManager(context = application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database provider
        val databaseProvider = AndroidDatabaseProvider(applicationContext)
        DatabaseProvider.initialize(databaseProvider)

        // Check if we need to create the default form
        lifecycleScope.launch {
            val database = databaseProvider.database
            if (database.getAllSampleForms().isEmpty()) {
                DatabaseSetup.createDefaultSampleForm(database)
            }
        }

        setContent {
            App(blueFalconManager)
        }
    }
}