package com.chame.myapplication.feacture.administrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.feacture.administrador.presentation.viewModel.AdminDashboardViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit,
    onManageMenuClick: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val successColor = Color(0xFF2E7D32)
    val totalEarnings = state.sales.sumOf { it.price }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ADMINISTRACIÓN", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "${state.sales.size} ventas registradas",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onManageMenuClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Gestionar menú", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.loadSales() }) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
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
                            .background(Brush.horizontalGradient(listOf(pizzaOrange, pizzaYellow)))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GANANCIAS TOTALES", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                Text(
                                    "$${String.format(Locale.US, "%.2f", totalEarnings)}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("VENTAS", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                Text("${state.sales.size}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }

                if (state.sales.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val statusGroups = state.sales.groupBy { it.status }
                                val chartStatuses = listOf("PENDING", "IN_PROGRESS", "COMPLETED", "DELIVERED")
                                val statusLabels = mapOf(
                                    "PENDING" to "Pendiente",
                                    "IN_PROGRESS" to "En prep.",
                                    "COMPLETED" to "Completado",
                                    "DELIVERED" to "Entregado"
                                )
                                val statusBarColors = mapOf(
                                    "PENDING" to Color(0xFFF9A825),
                                    "IN_PROGRESS" to Color(0xFF1565C0),
                                    "COMPLETED" to successColor,
                                    "DELIVERED" to Color(0xFF757575)
                                )

                                Text(
                                    "PEDIDOS POR ESTADO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = pizzaOrange
                                )
                                Spacer(Modifier.height(14.dp))

                                val maxCount = chartStatuses.maxOf { (statusGroups[it]?.size ?: 0).coerceAtLeast(1) }
                                chartStatuses.forEach { status ->
                                    val count = statusGroups[status]?.size ?: 0
                                    val fraction = count.toFloat() / maxCount
                                    val barColor = statusBarColors[status] ?: Color.Gray
                                    val label = statusLabels[status] ?: status
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, modifier = Modifier.width(76.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier.weight(1f).height(28.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                        ) {
                                            if (count > 0) {
                                                Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(barColor, RoundedCornerShape(8.dp)))
                                            }
                                            Text(
                                                "$count",
                                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                                fontSize = 11.sp,
                                                color = if (count > 0 && fraction > 0.6f) Color.White else Color.Gray,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))
                                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    "INGRESOS POR ESTADO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = pizzaOrange
                                )
                                Spacer(Modifier.height(14.dp))

                                val maxRevenue = chartStatuses.maxOf {
                                    (statusGroups[it]?.sumOf { r -> r.price } ?: 0.0).coerceAtLeast(0.01)
                                }
                                chartStatuses.forEach { status ->
                                    val revenue = statusGroups[status]?.sumOf { it.price } ?: 0.0
                                    val revFraction = (revenue / maxRevenue).toFloat().coerceIn(0f, 1f)
                                    val barColor = statusBarColors[status] ?: Color.Gray
                                    val label = statusLabels[status] ?: status
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, modifier = Modifier.width(76.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier.weight(1f).height(28.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                        ) {
                                            if (revenue > 0.0) {
                                                Box(Modifier.fillMaxWidth(revFraction).fillMaxHeight().background(barColor, RoundedCornerShape(8.dp)))
                                            }
                                            Text(
                                                "$${String.format(Locale.US, "%.0f", revenue)}",
                                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                                fontSize = 11.sp,
                                                color = if (revenue > 0.0 && revFraction > 0.6f) Color.White else Color.Gray,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = pizzaOrange)
                        }
                    }
                } else if (state.sales.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ShoppingCart, null, Modifier.size(72.dp), Color.LightGray)
                            Spacer(Modifier.height(16.dp))
                            Text("Sin ventas registradas", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                } else {
                    items(state.sales) { sale ->
                        val statusColor = when (sale.status) {
                            "COMPLETED" -> successColor
                            "IN_PROGRESS" -> Color(0xFF1565C0)
                            else -> Color(0xFFF9A825)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween,
                                    Alignment.Top
                                ) {
                                    Text(
                                        "Orden #${sale.id}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        when (sale.status) {
                                            "PENDING" -> "Pendiente"
                                            "IN_PROGRESS" -> "Preparando"
                                            "COMPLETED" -> "Completado"
                                            "DELIVERED" -> "Entregado"
                                            else -> sale.status
                                        },
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("🍕  ${sale.pizzaName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Cliente: ${sale.clientName}", color = Color.Gray, fontSize = 13.sp)
                                Text("Mesa: ${sale.tableNumber}", color = Color.Gray, fontSize = 12.sp)

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween,
                                    Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("PRECIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(
                                            "$${String.format(Locale.US, "%.2f", sale.price)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = pizzaOrange
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PAGÓ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(
                                            "$${String.format(Locale.US, "%.2f", sale.totalPaid)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("CAMBIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = successColor)
                                        Text(
                                            "$${String.format(Locale.US, "%.2f", sale.changeReturned)}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = successColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
