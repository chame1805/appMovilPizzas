package com.chame.myapplication

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.chame.myapplication.core.location.WaiterLocationManager
import com.chame.myapplication.core.navigation.AppNavigation
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.core.websocket.WaiterWebSocketManager
import com.chame.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var waiterWebSocketManager: WaiterWebSocketManager
    @Inject lateinit var waiterLocationManager: WaiterLocationManager

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) waiterLocationManager.startTracking()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permiso de ubicación en runtime
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        enableEdgeToEdge()

        setContent {
            val isDarkMode by sessionManager.isDarkModeFlow.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    sessionManager = sessionManager,
                    waiterWebSocketManager = waiterWebSocketManager,
                    waiterLocationManager = waiterLocationManager
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // La ubicación sigue corriendo en background via WorkManager
    }

    override fun onDestroy() {
        super.onDestroy()
        waiterLocationManager.stopTracking()
    }
}
