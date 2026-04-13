package com.chame.myapplication.core.location

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for location tracking
 * Manages location service lifecycle
 */
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    val currentLocation: StateFlow<Location?> = locationManager.currentLocation
    val isTracking: StateFlow<Boolean> = locationManager.isTracking

    /**
     * Start tracking location
     */
    fun startTracking() {
        viewModelScope.launch {
            locationManager.startTracking()
        }
    }

    /**
     * Stop tracking location
     */
    fun stopTracking() {
        viewModelScope.launch {
            locationManager.stopTracking()
        }
    }

    /**
     * Get last known location
     */
    fun getLastLocation(callback: (Location?) -> Unit) {
        locationManager.getLastLocation(callback)
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.cleanup()
    }
}
