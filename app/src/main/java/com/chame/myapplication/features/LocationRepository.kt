package com.chame.myapplication.features.location.data

import com.chame.myapplication.core.sensors.GpsLocationManager
import com.chame.myapplication.core.sensors.UserLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocationRepository {
    fun getCurrentLocation(): Flow<UserLocation?>
    fun isLocationAvailable(): Flow<Boolean>
    fun getLocationError(): Flow<String?>
    fun startTracking()
    fun stopTracking()
    fun getLastKnownLocation(): UserLocation?
}

class LocationRepositoryImpl @Inject constructor(
    private val gpsLocationManager: GpsLocationManager
) : LocationRepository {
    
    override fun getCurrentLocation(): Flow<UserLocation?> {
        return gpsLocationManager.currentLocation
    }
    
    override fun isLocationAvailable(): Flow<Boolean> {
        return gpsLocationManager.isLocationAvailable
    }
    
    override fun getLocationError(): Flow<String?> {
        return gpsLocationManager.locationError
    }
    
    override fun startTracking() {
        gpsLocationManager.startLocationUpdates()
    }
    
    override fun stopTracking() {
        gpsLocationManager.stopLocationUpdates()
    }
    
    override fun getLastKnownLocation(): UserLocation? {
        return gpsLocationManager.getLastKnownLocation()
    }
}
