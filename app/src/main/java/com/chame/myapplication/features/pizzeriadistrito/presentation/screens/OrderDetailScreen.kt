package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit,
    historyEntry: NavBackStackEntry,
    viewModel: HistoryViewModel = hiltViewModel(historyEntry)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val order = remember(state.orders, orderId) {
        state.orders.find { it.id == orderId }
    }

    val pizzaOrange = Color(0xFFE65100)
    val successColor = Color(0xFF2E7D32)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DETALLE DE ORDEN", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        order?.let {
                            Text("# ${it.id}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (order == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Orden no encontrada", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- ESTADO ---
            val (statusColor, statusLabel) = statusInfo(order.status)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(pizzaOrange, Color(0xFFFF7043))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🍕", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        order.pizzaName,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            statusLabel,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // --- INFORMACIÓN GENERAL ---
            DetailCard(title = "Información General") {
                DetailRow(icon = Icons.Default.Tag, label = "ID de Orden", value = "# ${order.id}")
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Default.Person, label = "Cliente", value = order.clientName.ifBlank { "—" })
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Default.TableRestaurant, label = "Mesa", value = order.tableNumber.toString())
            }

            // --- INFORMACIÓN DE PAGO ---
            DetailCard(title = "Resumen de Pago") {
                PaymentRow(label = "Precio de la orden", value = order.price, color = pizzaOrange)
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                PaymentRow(label = "Monto recibido", value = order.totalPaid, color = MaterialTheme.colorScheme.onSurface)
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                PaymentRow(label = "Cambio entregado", value = order.changeReturned, color = successColor)
            }

            // --- ESTADO VISUAL ---
            DetailCard(title = "Estado del Pedido") {
                val steps = listOf(
                    "PENDING" to "Pedido recibido",
                    "IN_PROGRESS" to "En preparación",
                    "COMPLETED" to "Listo para entrega",
                    "DELIVERED" to "Entregado"
                )
                val currentIndex = steps.indexOfFirst { it.first == order.status }

                steps.forEachIndexed { index, (_, label) ->
                    val isDone = index <= currentIndex
                    val isCurrent = index == currentIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDone) pizzaOrange else Color(0xFFEEEEEE),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    if (isDone) "✓" else "${index + 1}",
                                    color = if (isDone) Color.White else Color.LightGray,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            label,
                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                            fontSize = if (isCurrent) 15.sp else 14.sp
                        )
                        if (isCurrent) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = pizzaOrange.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Actual",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = pizzaOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // --- FECHA ---
            if (order.createdAt.isNotBlank()) {
                DetailCard(title = "Fecha y Hora") {
                    Text(
                        formatDate(order.createdAt),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable () -> Unit) {
    val pizzaOrange = Color(0xFFE65100)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = pizzaOrange,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = Color.Gray, fontSize = 14.sp)
        }
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun PaymentRow(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.AttachMoney,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(label, color = Color.Gray, fontSize = 14.sp)
        }
        Text(
            if (value > 0.0) "$${String.format(Locale.US, "%.2f", value)}" else "—",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = color
        )
    }
}

private fun statusInfo(status: String): Pair<Color, String> = when (status) {
    "PENDING" -> Color(0xFFF9A825) to "Pendiente"
    "IN_PROGRESS" -> Color(0xFF1565C0) to "En preparación"
    "COMPLETED" -> Color(0xFF2E7D32) to "Listo para entrega"
    "DELIVERED" -> Color(0xFF757575) to "Entregado"
    else -> Color.Gray to status
}

private fun formatDate(raw: String): String {
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(raw) ?: return raw
        SimpleDateFormat("dd/MM/yyyy  •  HH:mm", Locale.getDefault()).format(date)
    }.getOrDefault(raw)
}
