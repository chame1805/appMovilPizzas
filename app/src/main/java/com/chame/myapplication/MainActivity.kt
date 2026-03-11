package com.chame.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.chame.myapplication.core.navigation.AppNavigation
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.core.websocket.WaiterWebSocketManager
import com.chame.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//esto es una prueba para el commit
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var waiterWebSocketManager: WaiterWebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    sessionManager = sessionManager,
                    waiterWebSocketManager = waiterWebSocketManager
                )
            }
        }
    }
}
