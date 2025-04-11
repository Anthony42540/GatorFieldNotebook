package org.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dev.database.cache.AndroidDatabaseProvider
import com.dev.database.cache.DatabaseProvider

class MainActivity : ComponentActivity() {
    /* Set up Runtime Permissions */
    @RequiresApi(Build.VERSION_CODES.S)
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the permission result
        val bluetoothConnectGranted = permissions[android.Manifest.permission.BLUETOOTH_CONNECT] ?: false
        val bluetoothScanGranted = permissions[android.Manifest.permission.BLUETOOTH_SCAN] ?: false
        val bluetoothAdminGranted = permissions[android.Manifest.permission.BLUETOOTH_ADMIN] ?: false

        if (bluetoothConnectGranted && bluetoothScanGranted && bluetoothAdminGranted) {
            // Both permissions granted
            initializeBluetooth()
        } else {
            // Permissions denied, show a message
            Toast.makeText(this, "Bluetooth permissions are required to proceed.", Toast.LENGTH_SHORT).show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )) {
                AlertDialog.Builder(this)
                    .setTitle("Bluetooth Permissions Required")
                    .setMessage("This app needs Bluetooth permissions to connect to nearby devices.")
                    .setPositiveButton("OK") { _, _ ->
                        requestBluetoothPermissions()
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()
            } else {
                // Request permissions directly
                requestBluetoothPermissions()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkBluetoothPermissions() {
//        println("We are in the check")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check and request permissions for Android 12 and above
            val hasBluetoothConnectPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            println("bluetooth connect Permission: " + hasBluetoothConnectPermission)

            val hasBluetoothScanPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

            val hasBluetoothAdminPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasBluetoothConnectPermission || !hasBluetoothScanPermission || hasBluetoothAdminPermission) {
                permissionsLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_ADMIN,
                    )
                )
            } else {
                initializeBluetooth()
            }
        } else {
            // Check and request permissions for Android 11 and below
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            println("Location Permission: " + hasLocationPermission)
            if (!hasLocationPermission) {
                oldBluetoothPermissionsLauncher.launch(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.BLUETOOTH)
                )
            } else {
                initializeBluetooth()
            }
        }
    }

    private val oldBluetoothPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationGranted) {
            initializeBluetooth()
        } else {
            Toast.makeText(this, "Location permission is required to use Bluetooth features.", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermissions() {
        permissionsLauncher.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )
        )
    }

    private fun initializeBluetooth() {
        Toast.makeText(this, "Bluetooth initialized successfully!", Toast.LENGTH_SHORT).show()
        // Add Bluetooth logic here
    }


    @RequiresApi(Build.VERSION_CODES.S)
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

        // Get Bluetooth Permissions
        checkBluetoothPermissions()

        setContent {
            App()
        }
    }
}
