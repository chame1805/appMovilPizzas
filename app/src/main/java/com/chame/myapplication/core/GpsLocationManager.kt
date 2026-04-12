package com.chame.myapplication.core.sensors

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class GpsLocationManager @Inject constructor(
    private val context: Context
) : LocationListener {
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    private val _currentLocation = MutableStateFlow<UserLocation?>(null)
    val currentLocation: Flow<UserLocation?> = _currentLocation.asStateFlow()
    
    private val _isLocationAvailable = MutableStateFlow(false)
    val isLocationAvailable: Flow<Boolean> = _isLocationAvailable.asStateFlow()
    
    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: Flow<String?> = _locationError.asStateFlow()
    
    companion object {
        private const val TAG = "GpsLocationManager"
        private const val MIN_TIME_BETWEEN_UPDATES = 1000L // 1 segundo
        private const val MIN_DISTANCE_CHANGE = 5f // 5 metros
    }
    
    init {
        Log.d(TAG, "GPS Location Manager initialized")
    }
    
    /**
     * Inicia el seguimiento de ubicación
     */
    fun startLocationUpdates() {
        try {
            // Verificar si LocationManager tiene proveedores habilitados
            if (!locationManager.isLocationEnabled) {
                _locationError.value = "Ubicación deshabilitada en el dispositivo"
                _isLocationAvailable.value = false
                Log.w(TAG, "Location is disabled on device")
                return
            }
            
            // Intentar con GPS provider
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE,
                        this
                    )
                    Log.d(TAG, "GPS provider requested")
                } catch (e: SecurityException) {
                    _locationError.value = "Permiso de ubicación denegado"
                    Log.e(TAG, "GPS permission denied", e)
                }
            }
            
            // Intentar con Network provider (WiFi/Datos)
            if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE,
                        this
                    )
                    Log.d(TAG, "Network provider requested")
                } catch (e: SecurityException) {
                    _locationError.value = "Permiso de ubicación denegado"
                    Log.e(TAG, "Network permission denied", e)
                }
            }
            
            _isLocationAvailable.value = true
        } catch (e: Exception) {
            _locationError.value = "Error al iniciar ubicación: ${e.message}"
            _isLocationAvailable.value = false
            Log.e(TAG, "Error starting location updates", e)
        }
    }
    
    /**
     * Detiene el seguimiento de ubicación
     */
    fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
            _isLocationAvailable.value = false
            Log.d(TAG, "Location updates stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping location updates", e)
        }
    }
    
    /**
     * Obtiene la última ubicación conocida
     */
    fun getLastKnownLocation(): UserLocation? {
        return try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            
            location?.let {
                UserLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy,
                    altitude = it.altitude
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied getting last known location", e)
            null
        }
    }
    
    // ========== LocationListener Implementation ==========
    
    override fun onLocationChanged(location: Location) {
        val userLocation = UserLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            altitude = location.altitude,
            timestamp = location.time
        )
        
        _currentLocation.value = userLocation
        _locationError.value = null
        
        Log.d(TAG, "Location updated: Lat=${location.latitude}, Lon=${location.longitude}")
    }
    
    override fun onProviderEnabled(provider: String) {
        Log.d(TAG, "Location provider enabled: $provider")
        _locationError.value = null
    }
    
    override fun onProviderDisabled(provider: String) {
        Log.d(TAG, "Location provider disabled: $provider")
        _locationError.value = "Proveedor de ubicación deshabilitado: $provider"
    }
    
    @Deprecated("Deprecated in API level 29")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, "Status changed for provider: $provider, status: $status")
    }
}
