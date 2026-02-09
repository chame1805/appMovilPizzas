package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.OrderViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    pizzaName: String,
    pizzaPrice: Double,
    onBackClick: () -> Unit,
    onPayClick: (String, Double, Double) -> Unit,
    // Inyectamos el ViewModel de la Orden
    orderViewModel: OrderViewModel = viewModel()
) {
    // Obtenemos el estado igual que en el menú de pizzas
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()
    val pizzaOrange = Color(0xFFE65100)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DETALLE DE ORDEN", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TOTAL A COBRAR", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text("$${String.format(Locale.US, "%.2f", pizzaPrice)}", fontSize = 42.sp, fontWeight = FontWeight.Black, color = pizzaOrange)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.clientName,
                onValueChange = { orderViewModel.onClientNameChange(it) },
                label = { Text("Nombre del Cliente") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.address,
                onValueChange = { orderViewModel.onAddressChange(it) },
                label = { Text("Dirección de Entrega") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.amountPaidText,
                onValueChange = { orderViewModel.onAmountPaidChange(it, pizzaPrice) },
                label = { Text("Monto Recibido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$ ") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.amountPaidText.isNotEmpty()) {
                val color = if (state.isPaymentSufficient) Color(0xFF2E7D32) else Color.Red
                Card(
                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (state.isPaymentSufficient) "CAMBIO: $${String.format(Locale.US, "%.2f", state.change)}"
                        else "FALTA: $${String.format(Locale.US, "%.2f", -state.change)}",
                        modifier = Modifier.padding(16.dp),
                        color = color,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onPayClick(state.clientName, state.amountPaidText.toDoubleOrNull() ?: 0.0, state.change) },
                enabled = state.clientName.isNotEmpty() && state.address.isNotEmpty() && state.isPaymentSufficient,
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
            ) {
                Text("FINALIZAR VENTA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}