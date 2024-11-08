package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun GoogleMaps(lat: String, long: String) {
    val defaultLocation = LatLng(0.0, 0.0)

    var isLoading by remember { mutableStateOf(true)}
    var userLocation: LatLng? by remember { mutableStateOf(defaultLocation) }

    val latitude = lat.toDoubleOrNull()
    val longitude = long.toDoubleOrNull()

    if (latitude != null && longitude != null) {
        userLocation = LatLng(latitude, longitude)
        isLoading = false
    }
    else {
        isLoading = true
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
    }

    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)

    Box(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions().mapColorScheme(MapColorScheme.FOLLOW_SYSTEM)
            }
        ) {
            if (!isLoading) {
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = userLocation!!),
                    title = "Your Location",
                    snippet = "Lat: ${userLocation!!.latitude}, Long: ${userLocation!!.longitude}"
                )
            }
        }
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