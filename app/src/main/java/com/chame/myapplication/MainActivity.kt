package com.chame.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController

import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.core.navigation.AppNavigation
import com.chame.myapplication.ui.theme.MyApplicationTheme

import com.chame.myapplication.feacture.auth.di.AuthModule
import com.chame.myapplication.feacture.register.di.RegisterModule
import com.chame.myapplication.feactures.Admin.di.AdminModule
import com.chame.myapplication.features.pizzeriadistrito.di.PizzeriaModule

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = AppContainer(applicationContext)

        val pizzeriaModule = PizzeriaModule(appContainer)
        val authModule = AuthModule(appContainer)              // LOGIN (se queda)
        val registerModule = RegisterModule(appContainer)      // REGISTER (nuevo feature)
        val adminModule = AdminModule(appContainer)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                AppNavigation(
                    navController = navController,
                    authModule = authModule,
                    registerModule = registerModule,
                    pizzeriaModule = pizzeriaModule,
                    adminModule = adminModule
                )
            }
        }
    }
}
