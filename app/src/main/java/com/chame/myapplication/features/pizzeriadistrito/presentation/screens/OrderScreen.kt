package com.chame.myapplication.features.pizzeriadistrito.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    pizzaName: String,
    pizzaPrice: Double,
    onBackClick: () -> Unit,
    onPayClick: () -> Unit
) {
    // Variables para guardar lo que escribe el usuario
    var clientName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden: $pizzaName") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // Flecha para regresar
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Muestra el precio
            Text(
                text = "Total a pagar: $$pizzaPrice",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo para el nombre
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Tu Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para la dirección
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección de entrega") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de pagar (Solo se activa si escribiste nombre y dirección)
            Button(
                onClick = onPayClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = clientName.isNotEmpty() && address.isNotEmpty()
            ) {
                Text("PAGAR AHORA")
            }
        }
    }
}