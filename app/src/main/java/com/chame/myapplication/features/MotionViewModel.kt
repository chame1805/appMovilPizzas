package com.chame.myapplication.features.motion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.core.AccelerometerData
import com.chame.myapplication.core.AccelerometerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MotionUiState(
    val currentAcceleration: AccelerometerData? = null,
    val isMonitoring: Boolean = false,
    val shakeDetected: Boolean = false,
    val shakeCount: Int = 0,
    val maxMagnitude: Float = 0f
)

@HiltViewModel
class MotionViewModel @Inject constructor(
    private val accelerometerManager: AccelerometerManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MotionUiState())
    val uiState: StateFlow<MotionUiState> = _uiState.asStateFlow()
    
    init {
        collectAccelerometerData()
    }
    
    private fun collectAccelerometerData() {
        viewModelScope.launch {
            accelerometerManager.accelerometerData.collect { accelData ->
                if (accelData != null) {
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        currentAcceleration = accelData,
                        maxMagnitude = maxOf(currentState.maxMagnitude, accelData.magnitude)
                    )
                }
            }
        }
        
        viewModelScope.launch {
            accelerometerManager.shakeDetected.collect { shakeDetected ->
                if (shakeDetected) {
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        shakeDetected = true,
                        shakeCount = currentState.shakeCount + 1
                    )
                }
            }
        }
    }
    
    fun startMonitoring() {
        accelerometerManager.startMonitoring()
        _uiState.value = _uiState.value.copy(isMonitoring = true)
    }
    
    fun stopMonitoring() {
        accelerometerManager.stopMonitoring()
        _uiState.value = _uiState.value.copy(isMonitoring = false)
    }
    
    fun resetShakeCount() {
        _uiState.value = _uiState.value.copy(shakeCount = 0, maxMagnitude = 0f)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
