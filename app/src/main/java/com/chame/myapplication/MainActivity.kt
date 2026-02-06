package com.chame.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.feacture.auth.di.AuthModule
import com.chame.myapplication.feactures.Admin.di.AdminModule
import com.chame.myapplication.features.pizzeriadistrito.di.PizzeriaModule
import com.chame.myapplication.ui.theme.MyApplicationTheme
import com.chame.myapplication.core.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = AppContainer(applicationContext)
        val pizzeriaModule = PizzeriaModule(appContainer)
        val authModule = AuthModule(appContainer)
        val adminModule = AdminModule(appContainer) // Inicializamos adminModule

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                AppNavigation(
                    navController = navController,
                    authModule = authModule,
                    pizzeriaModule = pizzeriaModule,
                    adminModule = adminModule // Pasamos el nuevo módulo
                )
            }
        }
    }
}