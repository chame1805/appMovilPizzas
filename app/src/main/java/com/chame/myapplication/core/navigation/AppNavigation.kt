package com.chame.myapplication.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chame.myapplication.feacture.auth.presentation.screens.LoginScreen
import com.chame.myapplication.feacture.register.presentation.screens.RegisterScreen
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminScreen
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.local.OrderStorage
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.Order
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.HistoryScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.PizzaMenuScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToMenu = {
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate("admin")
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    Toast.makeText(context, "Cuenta creada ✅", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        composable("admin") {
            AdminScreen()
        }

        composable("menu") {
            PizzaMenuScreen(
                onPizzaClick = { name, price -> navController.navigate("order/$name/$price") },
                onHistoryClick = { navController.navigate("history") }
            )
        }

        composable(
            route = "order/{name}/{price}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("price") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val pizzaName = backStackEntry.arguments?.getString("name") ?: ""
            val pizzaPrice = backStackEntry.arguments?.getFloat("price")?.toDouble() ?: 0.0

            OrderScreen(
                pizzaName = pizzaName,
                pizzaPrice = pizzaPrice,
                onBackClick = { navController.popBackStack() },
                onPayClick = { client, paid, change ->
                    val newOrder = Order(pizzaName, pizzaPrice, client, paid, change)
                    OrderStorage.addOrder(newOrder)
                    Toast.makeText(context, "¡Venta Guardada!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
