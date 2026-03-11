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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.OrderViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    pizzaName: String,
    pizzaPrice: Double,
    onBackClick: () -> Unit,
    onOrderSent: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var clientName by remember { mutableStateOf("") }
    var tableNumberText by remember { mutableStateOf("") }
    var amountPaidText by remember { mutableStateOf("") }

    val pizzaOrange = Color(0xFFE65100)
    val amountPaid = amountPaidText.toDoubleOrNull() ?: 0.0
    val tableNumber = tableNumberText.toIntOrNull() ?: 0
    val change = amountPaid - pizzaPrice
    val isPaymentSufficient = amountPaid >= pizzaPrice

    LaunchedEffect(state.orderSent) {
        if (state.orderSent) {
            onOrderSent()
        }
    }

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
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = pizzaOrange.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🍕  ${pizzaName}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(Modifier.height(4.dp))
                    Text("TOTAL A COBRAR", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("$${String.format(Locale.US, "%.2f", pizzaPrice)}", fontSize = 44.sp, fontWeight = FontWeight.Black, color = pizzaOrange)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Nombre del Cliente") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tableNumberText,
                onValueChange = { if (it.all { c -> c.isDigit() }) tableNumberText = it },
                label = { Text("Número de Mesa") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountPaidText,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountPaidText = it },
                label = { Text("Monto Recibido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$ ") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (amountPaidText.isNotEmpty()) {
                val color = if (isPaymentSufficient) Color(0xFF2E7D32) else Color.Red
                Card(
                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isPaymentSufficient) "CAMBIO: $${String.format(Locale.US, "%.2f", change)}" else "FALTA: $${String.format(Locale.US, "%.2f", -change)}",
                        modifier = Modifier.padding(16.dp),
                        color = color,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            state.error?.let {
                Text(it, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.createOrder(pizzaName, pizzaPrice, clientName, tableNumber, amountPaid, change)
                },
                enabled = clientName.isNotEmpty() && tableNumber > 0 && isPaymentSufficient && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("FINALIZAR VENTA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}