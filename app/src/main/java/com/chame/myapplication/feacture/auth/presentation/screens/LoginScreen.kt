package com.chame.myapplication.feacture.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chame.myapplication.R
import com.chame.myapplication.feacture.auth.presentation.components.AdminLoginDialog
import com.chame.myapplication.feacture.auth.presentation.viewModel.AuthViewModel
import com.chame.myapplication.feacture.auth.presentation.viewModel.AuthViewModelFactory

@Composable
fun LoginScreen(
    viewModelFactory: AuthViewModelFactory,
    onNavigateToMenu: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToRegister: () -> Unit // ✅ NUEVO
) {
    val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    var showAdminDialog by remember { mutableStateOf(false) }

    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(pizzaYellow, Color.White)))
            .padding(24.dp)
    ) {
        // Botón Administración
        TextButton(
            onClick = { showAdminDialog = true },
            modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding()
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = pizzaOrange)
            Text(" ADMINISTRACIÓN", color = pizzaOrange, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(330.dp)
            )
            Text("Distrito Pizza", fontSize = 40.sp, fontWeight = FontWeight.Black, color = pizzaOrange)

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = { viewModel.onLoginSuccess(onNavigateToMenu) },
                modifier = Modifier.fillMaxWidth(0.9f).height(75.dp),
                shape = RoundedCornerShape(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
            ) {
                Text("ACCEDER AL SISTEMA", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ✅ Botón para ir a Registro
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "¿No tienes cuenta?  Crear cuenta",
                    color = pizzaOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showAdminDialog) {
            AdminLoginDialog(
                onDismiss = { showAdminDialog = false; viewModel.clearError() },
                isLoading = viewModel.isLoading.value,
                errorMessage = viewModel.errorMessage.value,
                onLogin = { email, password ->
                    viewModel.loginAdmin(email, password) {
                        showAdminDialog = false
                        onNavigateToAdmin()
                    }
                }
            )
        }

        Text(
            text = "Sesión activa: Carlos Jaffet",
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 16.dp),
            fontWeight = FontWeight.Bold
        )
    }
}
