package com.chame.myapplication.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * LocationManager handles GPS/Localización tracking
 * Provides current location via StateFlow
 */
@ViewModelScoped
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var locationCallback: LocationCallback? = null

    /**
     * Start tracking user location
     * Updates are delivered every 10 seconds or 100 meters
     */
    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (_isTracking.value) return

        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                .setMinUpdateDistanceMeters(100f)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        _currentLocation.value = location
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            _isTracking.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stop tracking user location
     */
    fun stopTracking() {
        if (!_isTracking.value) return

        try {
            if (locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback!!)
                locationCallback = null
            }
            _isTracking.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get last known location synchronously
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation(callback: (Location?) -> Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                callback(location)
            }.addOnFailureListener {
                callback(null)
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopTracking()
    }
}
