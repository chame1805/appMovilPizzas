package com.chame.myapplication

import android.os.Bundle
import android.widget.Toast // Importante para el mensaje de "Pedido enviado"
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.features.pizzeriadistrito.di.PizzeriaModule
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderScreen // Asegúrate de haber creado este archivo
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.PizzaMenuScreen
import com.chame.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = AppContainer(applicationContext)
        val pizzeriaModule = PizzeriaModule(appContainer)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // 1. Crear el controlador de navegación (el "chofer" de la app)
                val navController = rememberNavController()

                // 2. Definir el "Mapa" de pantallas
                NavHost(navController = navController, startDestination = "menu") {

                    // --- PANTALLA 1: MENÚ ---
                    composable("menu") {
                        PizzaMenuScreen(
                            viewModelFactory = pizzeriaModule.providePizzaViewModelFactory(),
                            // Aquí definimos qué pasa al dar clic en una pizza
                            onPizzaClick = { nombre, precio ->
                                // Navegamos a la ruta "order" y le pegamos los datos en la URL
                                navController.navigate("order/$nombre/$precio")
                            }
                        )
                    }

                    // --- PANTALLA 2: FORMULARIO DE ORDEN ---
                    composable(
                        route = "order/{name}/{price}",
                        arguments = listOf(
                            navArgument("name") { type = NavType.StringType },
                            navArgument("price") { type = NavType.FloatType }
                        )
                    ) { backStackEntry ->
                        // Recuperamos los datos que venían en la URL
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        val price = backStackEntry.arguments?.getFloat("price")?.toDouble() ?: 0.0

                        // Mostramos la pantalla de Orden
                        OrderScreen(
                            pizzaName = name,
                            pizzaPrice = price,
                            onBackClick = { navController.popBackStack() }, // Volver atrás
                            onPayClick = {
                                Toast.makeText(applicationContext, "¡Pedido enviado!", Toast.LENGTH_LONG).show()
                                navController.popBackStack() // Volver al menú
                            }
                        )
                    }
                }
            }
        }
    }
}