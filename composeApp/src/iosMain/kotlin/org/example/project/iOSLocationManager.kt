import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class iOSLocationManager : NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager = CLLocationManager()
    private var locationPermissionStatusCancellableContinuation: CancellableContinuation<LocationPermissionStatus>? =
        null
    private var locationResultContinuation: (CancellableContinuation<Result<CLLocation>>)? = null

    init {
        locationManager.delegate = this
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }

    suspend fun requestCurrentLocation(): Result<CLLocation> =
        suspendCancellableCoroutine { continuation ->
            locationResultContinuation = continuation
            locationManager.requestLocation()

        }

    suspend fun requestLocationPermission(): LocationPermissionStatus =
        suspendCancellableCoroutine { continuation ->
            locationPermissionStatusCancellableContinuation = continuation
            when (CLLocationManager.authorizationStatus()) {
                kCLAuthorizationStatusNotDetermined -> {
                    locationManager.requestWhenInUseAuthorization()
                }

                kCLAuthorizationStatusRestricted, kCLAuthorizationStatusDenied -> {
                    continuation.resume(LocationPermissionStatus.RESTRICTED_OR_DENIED)
                }

                kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                    continuation.resume(LocationPermissionStatus.ACCEPTED)
                }
            }
        }


    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        locationPermissionStatusCancellableContinuation?.let {
            // Ensure that continuation is resumed only once
            if (it.isActive) {
                when (didChangeAuthorizationStatus) {
                    kCLAuthorizationStatusRestricted,
                    kCLAuthorizationStatusDenied -> it.resume(
                        LocationPermissionStatus.RESTRICTED_OR_DENIED
                    )

                    kCLAuthorizationStatusAuthorizedAlways,
                    kCLAuthorizationStatusAuthorizedWhenInUse -> it.resume(
                        LocationPermissionStatus.ACCEPTED
                    )

                    kCLAuthorizationStatusNotDetermined -> it.resume(LocationPermissionStatus.NOT_DETERMINED)
                }
                locationPermissionStatusCancellableContinuation = null
            }

        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        locationPermissionStatusCancellableContinuation?.let {
            if (it.isActive) {
                it.resume(LocationPermissionStatus.RESTRICTED_OR_DENIED)
                locationPermissionStatusCancellableContinuation = null
            }

        }
        locationResultContinuation?.let {
            if (it.isActive) {
                it.resumeWithException(Exception("Failed to get location,description:${didFailWithError.localizedDescription},code:${didFailWithError.code}"))
                locationResultContinuation = null
            }
        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.firstOrNull() as? CLLocation?
        locationResultContinuation?.let {
            if (it.isActive) {
                if (location != null) {
                    it.resume(Result.success(location))
                } else {
                    it.resumeWithException(Exception("No valid location found"))
                }
                locationResultContinuation = null
            }
        }
    }

}

enum class LocationPermissionStatus {
    RESTRICTED_OR_DENIED,
    NOT_DETERMINED,
    ACCEPTED
}