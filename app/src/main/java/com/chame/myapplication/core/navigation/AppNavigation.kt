package com.chame.myapplication.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chame.myapplication.feacture.auth.di.AuthModule
import com.chame.myapplication.feacture.auth.presentation.screens.LoginScreen
import com.chame.myapplication.feacture.register.di.RegisterModule
import com.chame.myapplication.feacture.register.presentation.screens.RegisterScreen
import com.chame.myapplication.feactures.Admin.di.AdminModule
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminScreen
import com.chame.myapplication.feactures.Admin.presentation.viewModel.AdminViewModel
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.local.OrderStorage
import com.chame.myapplication.features.pizzeriadistrito.di.PizzeriaModule
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.Order
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    authModule: AuthModule,
    pizzeriaModule: PizzeriaModule,
    adminModule: AdminModule,
    registerModule: RegisterModule
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModelFactory = authModule.provideAuthViewModelFactory(),
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

        // ✅ RUTA REGISTER
        composable("register") {
            RegisterScreen(
                viewModelFactory = registerModule.provideRegisterViewModelFactory(),
                onBack = { navController.popBackStack() },
                onSuccess = {
                    Toast.makeText(context, "Cuenta creada ✅", Toast.LENGTH_SHORT).show()
                    navController.popBackStack() // regresa a login
                }
            )
        }

        // --- RUTA ADMINISTRACIÓN ---
        composable("admin") {
            val adminViewModel: AdminViewModel = viewModel(
                factory = adminModule.provideAdminViewModelFactory()
            )
            AdminScreen(viewModel = adminViewModel)
        }

        composable("menu") {
            PizzaMenuScreen(
                viewModelFactory = pizzeriaModule.providePizzaViewModelFactory(),
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
