package org.example.project

import androidx.compose.foundation.layout.Box
import iOSLocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMaps(lat: String, long: String) {
    val locationManager = remember { iOSLocationManager() }
    var location by remember { mutableStateOf<CLLocation?>(null) }
    var isLoading by remember { mutableStateOf(true)}

    LaunchedEffect(Unit) {
        val permissionStatus = locationManager.requestLocationPermission()

        if (permissionStatus == LocationPermissionStatus.ACCEPTED) {
            try {
                val result = locationManager.requestCurrentLocation()
                result.onSuccess {
                    location = it
                    isLoading = false
                }
            } catch (e: Exception) {
                println("Error")
                isLoading = false
            }
        }
        else {
            isLoading = false
        }
    }

    val latitude = location?.coordinate?.useContents { latitude } ?: 0.0
    val longitude = location?.coordinate?.useContents { longitude } ?: 0.0

    val mapView = remember { GMSMapView() }
    val cameraPosition = GMSCameraPosition.cameraWithLatitude(
        latitude = latitude,
        longitude = longitude,
        zoom = 15.0f
    )

    location?.let {
        val marker = GMSMarker()
        val cameraUpdate = GMSCameraUpdate.setCamera(cameraPosition)
        mapView.moveCamera(cameraUpdate)
        marker.position = location!!.coordinate
        marker.map = mapView
    }

    mapView.settings.setZoomGestures(true)

    Box(modifier = Modifier.fillMaxWidth()) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = Color.White
            )
        }
    }
}