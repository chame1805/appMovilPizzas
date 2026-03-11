package com.chame.myapplication.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.core.websocket.WaiterWebSocketManager
import com.chame.myapplication.feacture.administrador.presentation.screens.AdminDashboardScreen
import com.chame.myapplication.feacture.auth.presentation.screens.LoginScreen
import com.chame.myapplication.feacturecocina.presentation.screens.CocineroScreen
import com.chame.myapplication.feacture.register.presentation.screens.RegisterScreen
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.HistoryScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.PizzaMenuScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    sessionManager: SessionManager,
    waiterWebSocketManager: WaiterWebSocketManager
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToMesero = {
                    waiterWebSocketManager.connect()
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToCocinero = {
                    navController.navigate("cocinero") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
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
                    Toast.makeText(context, "Cuenta creada", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        composable("cocinero") {
            CocineroScreen(
                onBackClick = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onBackClick = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onManageMenuClick = {
                    navController.navigate("admin")
                }
            )
        }

        composable("admin") {
            AdminScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("menu") {
            LaunchedEffect(Unit) {
                waiterWebSocketManager.events.collect { event ->
                    if (event.event == "ORDER_COMPLETED") {
                        Toast.makeText(
                            context,
                            "Orden #${event.id} lista! Mesa ${event.tableNumber}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            PizzaMenuScreen(
                onPizzaClick = { name, price -> navController.navigate("order/$name/$price") },
                onHistoryClick = { navController.navigate("history") },
                onBackClick = {
                    waiterWebSocketManager.disconnect()
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                onOrderSent = {
                    Toast.makeText(context, "Orden enviada a cocina!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
