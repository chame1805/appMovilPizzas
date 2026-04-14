package com.chame.myapplication.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.chame.myapplication.core.database.dao.WaiterLocationDao
import com.chame.myapplication.core.database.entity.WaiterLocationEntity
import com.chame.myapplication.core.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaiterLocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val waiterLocationDao: WaiterLocationDao,
    private val sessionManager: SessionManager
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10_000L   // cada 10 segundos
    ).setMinUpdateDistanceMeters(100f) // o cada 100 metros
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val waiterId = sessionManager.userId
            if (waiterId <= 0) return

            scope.launch {
                waiterLocationDao.insert(
                    WaiterLocationEntity(
                        waiterId = waiterId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = location.time,
                        syncStatus = "PENDING"
                    )
                )
            }
        }
    }

    fun startTracking() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) return

        fusedClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopTracking() {
        fusedClient.removeLocationUpdates(locationCallback)
    }
}
