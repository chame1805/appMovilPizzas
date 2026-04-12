package com.chame.myapplication.core

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class CameraManager @Inject constructor(
    private val context: Context
) {
    
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    
    private val _photoSaved = MutableStateFlow<String?>(null)
    val photoSaved: StateFlow<String?> = _photoSaved.asStateFlow()
    
    private val _cameraError = MutableStateFlow<String?>(null)
    val cameraError: StateFlow<String?> = _cameraError.asStateFlow()
    
    companion object {
        private const val TAG = "CameraManager"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
    
    fun initializeCamera(lifecycleOwner: LifecycleOwner) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture?.addListener({
            try {
                cameraProvider = cameraProviderFuture?.get()
                bindCameraUseCases()
                Log.d(TAG, "Camera initialized successfully")
            } catch (e: Exception) {
                _cameraError.value = "Error al inicializar cámara: ${e.message}"
                Log.e(TAG, "Error initializing camera", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            Log.d(TAG, "Camera use cases bound successfully")
        } catch (e: Exception) {
            _cameraError.value = "Error al vincular cámara: ${e.message}"
            Log.e(TAG, "Error binding camera use cases", e)
        }
    }
    
    fun takePhoto(outputDirectory: File, onPhotoSaved: (String) -> Unit) {
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    _cameraError.value = "Error capturando foto: ${exception.message}"
                    Log.e(TAG, "Photo capture failed", exception)
                }
                
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = photoFile.absolutePath
                    _photoSaved.value = savedUri
                    onPhotoSaved(savedUri)
                    Log.d(TAG, "Photo saved: $savedUri")
                }
            }
        )
    }
    
    fun releaseCamera() {
        cameraProvider?.unbindAll()
        Log.d(TAG, "Camera released")
    }
}
