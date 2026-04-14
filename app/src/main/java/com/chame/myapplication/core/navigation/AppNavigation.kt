package com.chame.myapplication.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chame.myapplication.core.location.WaiterLocationManager
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.core.websocket.WaiterWebSocketManager
import com.chame.myapplication.feacture.administrador.presentation.screens.AdminDashboardScreen
import com.chame.myapplication.feacture.administrador.presentation.screens.AdminProfileScreen
import com.chame.myapplication.feacture.auth.presentation.screens.LoginScreen
import com.chame.myapplication.feacturecocina.presentation.screens.CocineroProfileScreen
import com.chame.myapplication.feacturecocina.presentation.screens.CocineroScreen
import com.chame.myapplication.feacture.register.presentation.screens.RegisterScreen
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.HistoryScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderDetailScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.PizzaMenuScreen
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.WaiterProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    sessionManager: SessionManager,
    waiterWebSocketManager: WaiterWebSocketManager,
    waiterLocationManager: WaiterLocationManager
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToMesero = {
                    waiterWebSocketManager.connect()
                    waiterLocationManager.startTracking()
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
                },
                sessionManager = sessionManager
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
                },
                onProfileClick = {
                    navController.navigate("cocinero_profile")
                }
            )
        }

        composable("cocinero_profile") {
            CocineroProfileScreen(
                sessionManager = sessionManager,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("admin_dashboard") {
            val dashboardEntry = it
            AdminDashboardScreen(
                onBackClick = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onManageMenuClick = {
                    navController.navigate("admin")
                },
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable("admin_profile") {
            val dashboardEntry = remember(it) {
                navController.getBackStackEntry("admin_dashboard")
            }
            AdminProfileScreen(
                sessionManager = sessionManager,
                onBackClick = { navController.popBackStack() },
                dashboardEntry = dashboardEntry
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
                onProfileClick = { navController.navigate("waiter_profile") },
                onBackClick = {
                    waiterWebSocketManager.disconnect()
                    waiterLocationManager.stopTracking()
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
            val historyEntry = it
            HistoryScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate("order_detail/$orderId")
                }
            )
        }

        composable(
            route = "order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
            val historyEntry = remember(backStackEntry) {
                navController.getBackStackEntry("history")
            }
            OrderDetailScreen(
                orderId = orderId,
                onBackClick = { navController.popBackStack() },
                historyEntry = historyEntry
            )
        }

        composable("waiter_profile") {
            WaiterProfileScreen(
                sessionManager = sessionManager,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
