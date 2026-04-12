package com.chame.myapplication.features.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.core.sensors.UserLocation
import com.chame.myapplication.features.location.data.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationUiState(
    val currentLocation: UserLocation? = null,
    val isTracking: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastKnownLocation: UserLocation? = null
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()
    
    init {
        collectLocationUpdates()
        loadLastKnownLocation()
    }
    
    private fun collectLocationUpdates() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation().collect { location ->
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    error = null
                )
            }
        }
        
        viewModelScope.launch {
            locationRepository.getLocationError().collect { error ->
                _uiState.value = _uiState.value.copy(error = error)
            }
        }
    }
    
    private fun loadLastKnownLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val lastLocation = locationRepository.getLastKnownLocation()
                _uiState.value = _uiState.value.copy(
                    lastKnownLocation = lastLocation,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error cargando última ubicación: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun startTracking() {
        if (!_uiState.value.isTracking) {
            locationRepository.startTracking()
            _uiState.value = _uiState.value.copy(isTracking = true)
        }
    }
    
    fun stopTracking() {
        if (_uiState.value.isTracking) {
            locationRepository.stopTracking()
            _uiState.value = _uiState.value.copy(isTracking = false)
        }
    }
    
    fun getFormattedLocation(): String {
        val location = _uiState.value.currentLocation
        return if (location != null) {
            "Lat: ${String.format("%.4f", location.latitude)}, " +
            "Lon: ${String.format("%.4f", location.longitude)}, " +
            "Precisión: ${String.format("%.1f", location.accuracy)}m"
        } else {
            "Ubicación no disponible"
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}
