package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.HistoryViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val orders = state.orders
    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val successColor = Color(0xFF2E7D32)
    val deliveredColor = Color(0xFF757575)
    val totalSales = orders.sumOf { it.price }

    // --- DIÁLOGO DE EDICIÓN ---
    state.editingOrder?.let { order ->
        val paid = state.editPaidText.toDoubleOrNull() ?: 0.0
        val change = paid - order.price
        val sufficient = paid >= order.price

        AlertDialog(
            onDismissRequest = { viewModel.closeEditDialog() },
            title = { Text("Editar Pedido #${order.id}", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🍕 ${order.pizzaName} — $${String.format(Locale.US, "%.2f", order.price)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = state.editClientName,
                        onValueChange = { viewModel.setEditClientName(it) },
                        label = { Text("Nombre del cliente") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.editTableText,
                        onValueChange = { viewModel.setEditTableText(it) },
                        label = { Text("Mesa") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.editPaidText,
                        onValueChange = { viewModel.setEditPaidText(it) },
                        label = { Text("Monto recibido") },
                        singleLine = true,
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.editPaidText.isNotEmpty()) {
                        val color = if (sufficient) successColor else Color.Red
                        Text(
                            if (sufficient) "Cambio: $${String.format(Locale.US, "%.2f", change)}"
                            else "Falta: $${String.format(Locale.US, "%.2f", -change)}",
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    state.editError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val table = state.editTableText.toIntOrNull() ?: order.tableNumber
                        viewModel.updateOrder(order.id, state.editClientName, table, paid, change)
                        viewModel.closeEditDialog()
                    },
                    enabled = state.editClientName.isNotEmpty() && sufficient,
                    colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
                ) { Text("GUARDAR") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeEditDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MIS PEDIDOS", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("${orders.size} pedidos", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                state.isLoading && orders.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = pizzaOrange
                    )
                }

                state.error != null && orders.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error al cargar pedidos", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "", color = Color.Red, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadOrders() }) {
                            Text("Reintentar")
                        }
                    }
                }

                orders.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, Modifier.size(72.dp), Color.LightGray)
                        Spacer(Modifier.height(16.dp))
                        Text("Sin pedidos aún", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Tus pedidos aparecerán aquí", color = Color.LightGray, fontSize = 14.sp)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.horizontalGradient(listOf(pizzaOrange, Color(0xFFFF7043))))
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("TOTAL DEL TURNO", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                        Text("$${String.format(Locale.US, "%.2f", totalSales)}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("PEDIDOS", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                        Text("${orders.size}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                        }

                        items(orders) { order ->
                            val statusColor = when (order.status) {
                                "COMPLETED" -> successColor
                                "IN_PROGRESS" -> Color(0xFF1565C0)
                                "DELIVERED" -> deliveredColor
                                else -> pizzaYellow
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(3.dp)
                            ) {
                                Row(Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                                            .background(statusColor)
                                    )
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                                            Text(order.pizzaName, fontWeight = FontWeight.Black, fontSize = 17.sp, modifier = Modifier.weight(1f))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (order.status == "PENDING" || order.status == "IN_PROGRESS") {
                                                    IconButton(
                                                        onClick = { viewModel.openEditDialog(order) },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = pizzaOrange, modifier = Modifier.size(16.dp))
                                                    }
                                                    Spacer(Modifier.width(4.dp))
                                                }
                                                Surface(
                                                    color = statusColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        when (order.status) {
                                                            "PENDING" -> "Pendiente"
                                                            "IN_PROGRESS" -> "Preparando"
                                                            "COMPLETED" -> "Lista"
                                                            "DELIVERED" -> "Entregada"
                                                            else -> order.status
                                                        },
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        color = statusColor,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        if (order.status == "COMPLETED") {
                                            Button(
                                                onClick = { viewModel.markAsDelivered(order.id) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = successColor)
                                            ) {
                                                Text("MARCAR COMO ENTREGADA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(Modifier.height(4.dp))
                                        }
                                        if (order.status == "PENDING" || order.status == "IN_PROGRESS") {
                                            Text(
                                                if (order.status == "PENDING") "⏳ Esperando que cocina la tome" else "👨‍🍳 Cocina preparando la orden",
                                                fontSize = 11.sp,
                                                color = statusColor.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(Modifier.height(4.dp))
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Person, null, Modifier.size(13.dp), Color.Gray)
                                            Spacer(Modifier.width(4.dp))
                                            Text(order.clientName, color = Color.Gray, fontSize = 13.sp)
                                        }
                                        Text("Mesa: ${order.tableNumber}", color = Color.Gray, fontSize = 13.sp)
                                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
                                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                            Column {
                                                Text("PAGÓ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                                Text(
                                                    if (order.totalPaid > 0.0) "$${String.format(Locale.US, "%.2f", order.totalPaid)}" else "—",
                                                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("CAMBIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = successColor)
                                                Text(
                                                    if (order.totalPaid > 0.0) "$${String.format(Locale.US, "%.2f", order.changeReturned)}" else "—",
                                                    fontSize = 22.sp, fontWeight = FontWeight.Black, color = successColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}