package com.chame.myapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.chame.myapplication.core.navigation.AppNavigation
import com.chame.myapplication.core.notifications.NotificationChannels
import com.chame.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido - obtener token de FCM
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    println("Firebase Token: $token")
                }
            }
        }
    }
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            println("Permisos de ubicación concedidos")
        } else {
            println("Permisos de ubicación denegados")
        }
    }
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("Permiso de cámara concedido")
        } else {
            println("Permiso de cámara denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        
        // Crear canales de notificación
        NotificationChannels.createNotificationChannels(this)
        
        // Solicitar permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // Solicitar permisos de ubicación (Android 6+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
        
        // Solicitar permiso de cámara (Android 6+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}


