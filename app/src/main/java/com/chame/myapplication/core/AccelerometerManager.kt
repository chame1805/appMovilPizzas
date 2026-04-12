package com.chame.myapplication.core

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val magnitude: Float = sqrt(x * x + y * y + z * z),
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class AccelerometerManager @Inject constructor(
    context: Context
) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val _accelerometerData = MutableStateFlow<AccelerometerData?>(null)
    val accelerometerData: Flow<AccelerometerData?> = _accelerometerData.asStateFlow()
    
    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: Flow<Boolean> = _isMonitoring.asStateFlow()
    
    private val _shakeDetected = MutableStateFlow(false)
    val shakeDetected: Flow<Boolean> = _shakeDetected.asStateFlow()
    
    private var lastShakeTime = 0L
    private val SHAKE_THRESHOLD = 25f
    private val SHAKE_COOLDOWN = 1000L
    
    companion object {
        private const val TAG = "AccelerometerManager"
    }
    
    fun startMonitoring() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            _isMonitoring.value = true
            Log.d(TAG, "Accelerometer monitoring started")
        } else {
            Log.w(TAG, "Accelerometer not available")
        }
    }
    
    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        _isMonitoring.value = false
        Log.d(TAG, "Accelerometer monitoring stopped")
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val accelData = AccelerometerData(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )
            
            _accelerometerData.value = accelData
            
            // Detectar movimientos abruptos (shake)
            if (accelData.magnitude > SHAKE_THRESHOLD) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > SHAKE_COOLDOWN) {
                    _shakeDetected.value = true
                    lastShakeTime = currentTime
                    Log.d(TAG, "Shake detected! Magnitude: ${accelData.magnitude}")
                    
                    // Reset después de 500ms
                    Thread {
                        Thread.sleep(500)
                        _shakeDetected.value = false
                    }.start()
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: $accuracy")
    }
}
