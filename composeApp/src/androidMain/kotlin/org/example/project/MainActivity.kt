package org.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dev.database.cache.AndroidDatabaseProvider
import com.dev.database.cache.DatabaseProvider

class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database provider
        val databaseProvider = AndroidDatabaseProvider(applicationContext)
        DatabaseProvider.initialize(databaseProvider)

        if (Environment.isExternalStorageManager()) {

        }
        else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }

        setContent {
            App()
        }
    }
}