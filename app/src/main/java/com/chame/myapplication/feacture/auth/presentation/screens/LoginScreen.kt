package com.chame.myapplication.feacture.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.chame.myapplication.feacture.auth.presentation.viewModel.AuthViewModel
import com.chame.myapplication.feacture.auth.presentation.viewModel.AuthViewModelFactory

@Composable
fun LoginScreen(
    viewModelFactory: AuthViewModelFactory,
    onNavigateToMenu: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)

    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(pizzaYellow, Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp)
    ) {
        TextButton(
            onClick = { viewModel.onAdminAccess(onNavigateToAdmin) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = pizzaOrange,
                modifier = Modifier.size(28.dp) // Icono más grande
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ADMINISTRACIÓN",
                color = pizzaOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Distrito Pizza",
                modifier = Modifier
                    .size(330.dp) // Tamaño aumentado a 250dp
                    .padding(bottom = 20.dp)
            )

            Text(
                text = "Distrito Pizza",
                fontSize = 40.sp, // Fuente aumentada
                fontWeight = FontWeight.Black,
                color = pizzaOrange,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Terminal de Punto de Venta",
                fontSize = 15.sp, // Fuente aumentada
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 56.dp)
            )

            Button(
                onClick = { viewModel.onLoginSuccess(onNavigateToMenu) },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(75.dp), // Botón más alto
                shape = RoundedCornerShape(38.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pizzaOrange,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
            ) {
                Text(
                    text = "ACCEDER AL SISTEMA",
                    fontSize = 22.sp, // Texto del botón más grande
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }

        Text(
            text = "Sesión activa",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}