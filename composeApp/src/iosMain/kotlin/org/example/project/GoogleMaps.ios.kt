package org.example.project

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSMapView
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMaps(lat: String, long: String) {
    val mapView = remember { GMSMapView()}
    val cameraPosition = GMSCameraPosition.cameraWithLatitude(
        latitude = 1.35,
        longitude = 103.87,
        zoom = 14.0f
    )

    val cameraUpdate = GMSCameraUpdate.setCamera(cameraPosition)
    mapView.moveCamera(cameraUpdate)

    UIKitView(
        modifier = Modifier.fillMaxWidth(),
        factory = { mapView }
    )
}