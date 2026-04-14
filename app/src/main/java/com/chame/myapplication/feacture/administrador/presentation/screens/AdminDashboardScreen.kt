package com.chame.myapplication.feacture.administrador.presentation.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.feacture.administrador.presentation.viewModel.AdminDashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit,
    onManageMenuClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val successColor = Color(0xFF2E7D32)

    val filteredSales = remember(state.sales, state.filterStatus, state.searchQuery) {
        state.sales
            .filter { sale -> state.filterStatus == null || sale.status == state.filterStatus }
            .filter { sale -> state.searchQuery.isBlank() || sale.pizzaName.contains(state.searchQuery, ignoreCase = true) || sale.clientName.contains(state.searchQuery, ignoreCase = true) }
    }
    val totalEarnings = filteredSales.sumOf { it.price }

    // --- STATS HOY/AYER ---
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) }
    fun parseDate(s: String): Date? = runCatching { sdf.parse(s) }.getOrNull()
    val cal = Calendar.getInstance()
    val todayStart = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val yesterdayStart = todayStart - 86_400_000L
    val todaySales = state.sales.filter { (parseDate(it.createdAt)?.time ?: 0L) >= todayStart }
    val yesterdaySales = state.sales.filter { val t = parseDate(it.createdAt)?.time ?: 0L; t in yesterdayStart until todayStart }

    val filterOptions = listOf(
        null to "Todas",
        "PENDING" to "Pendiente",
        "IN_PROGRESS" to "En prep.",
        "COMPLETED" to "Completada",
        "DELIVERED" to "Entregada"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ADMINISTRACIÓN", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "${filteredSales.size} ventas registradas",
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
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi perfil", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val csv = viewModel.exportCsv()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, csv)
                            putExtra(Intent.EXTRA_SUBJECT, "Historial de ventas — Pizzería")
                        }
                        context.startActivity(Intent.createChooser(intent, "Exportar historial"))
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Exportar CSV", tint = Color.White)
                    }
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- BUSCADOR ---
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Buscar por pizza o cliente...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = pizzaOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = pizzaOrange,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }

                // --- FILTROS ---
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterOptions) { (status, label) ->
                            FilterChip(
                                selected = state.filterStatus == status,
                                onClick = { viewModel.setFilterStatus(status) },
                                label = { Text(label, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = pizzaOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // --- GANANCIAS TOTALES ---
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
                                    fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("VENTAS", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                Text("${filteredSales.size}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }

                // --- HOY vs AYER ---
                if (state.sales.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("RESUMEN DIARIO", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = pizzaOrange)
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("HOY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${todaySales.size}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = pizzaOrange)
                                        Text("$${String.format(Locale.US, "%.2f", todaySales.sumOf { it.price })}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = successColor)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(60.dp)
                                            .background(Color(0xFFEEEEEE))
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("AYER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${yesterdaySales.size}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1565C0))
                                        Text("$${String.format(Locale.US, "%.2f", yesterdaySales.sumOf { it.price })}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = successColor)
                                    }
                                }
                            }
                        }
                    }

                    // --- PEDIDOS POR ESTADO ---
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val statusGroups = state.sales.groupBy { it.status }
                                val chartStatuses = listOf("PENDING", "IN_PROGRESS", "COMPLETED", "DELIVERED")
                                val statusLabels = mapOf("PENDING" to "Pendiente", "IN_PROGRESS" to "En prep.", "COMPLETED" to "Completado", "DELIVERED" to "Entregado")
                                val statusBarColors = mapOf("PENDING" to Color(0xFFF9A825), "IN_PROGRESS" to Color(0xFF1565C0), "COMPLETED" to successColor, "DELIVERED" to Color(0xFF757575))

                                Text("PEDIDOS POR ESTADO", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = pizzaOrange)
                                Spacer(Modifier.height(14.dp))

                                val maxCount = chartStatuses.maxOf { (statusGroups[it]?.size ?: 0).coerceAtLeast(1) }
                                chartStatuses.forEach { status ->
                                    val count = statusGroups[status]?.size ?: 0
                                    val fraction = count.toFloat() / maxCount
                                    val barColor = statusBarColors[status] ?: Color.Gray
                                    val label = statusLabels[status] ?: status
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(label, modifier = Modifier.width(76.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        Spacer(Modifier.width(8.dp))
                                        Box(modifier = Modifier.weight(1f).height(28.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))) {
                                            if (count > 0) {
                                                Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(barColor, RoundedCornerShape(8.dp)))
                                            }
                                            Text("$count", modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp), fontSize = 11.sp, color = if (count > 0 && fraction > 0.6f) Color.White else Color.Gray, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- PIZZAS MÁS VENDIDAS ---
                    item {
                        val topPizzas = state.sales
                            .groupBy { it.pizzaName }
                            .map { (name, orders) -> name to orders.size }
                            .sortedByDescending { it.second }
                            .take(5)

                        val medalColors = listOf(Color(0xFFFFD700), Color(0xFFB0BEC5), Color(0xFFCD7F32), pizzaOrange.copy(alpha = 0.7f), pizzaOrange.copy(alpha = 0.45f))
                        val medalLabels = listOf("🥇", "🥈", "🥉", "4°", "5°")

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🍕  PIZZAS MÁS VENDIDAS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = pizzaOrange)
                                Spacer(Modifier.height(14.dp))
                                if (topPizzas.isEmpty()) {
                                    Text("Sin datos aún", color = Color.LightGray, fontSize = 13.sp)
                                } else {
                                    val maxSales = topPizzas.first().second.coerceAtLeast(1)
                                    topPizzas.forEachIndexed { index, (name, count) ->
                                        val fraction = count.toFloat() / maxSales
                                        val barColor = medalColors.getOrElse(index) { pizzaOrange }
                                        val medal = medalLabels.getOrElse(index) { "${index + 1}°" }
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(medal, modifier = Modifier.width(28.dp), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                            Spacer(Modifier.width(4.dp))
                                            Text(name, modifier = Modifier.width(90.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                            Spacer(Modifier.width(8.dp))
                                            Box(modifier = Modifier.weight(1f).height(28.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))) {
                                                if (count > 0) {
                                                    Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(barColor, RoundedCornerShape(8.dp)))
                                                }
                                                Text("$count ventas", modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp), fontSize = 11.sp, color = if (fraction > 0.55f) Color.White else Color.Gray, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = pizzaOrange)
                        }
                    }
                } else if (filteredSales.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingCart, null, Modifier.size(72.dp), Color.LightGray)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (state.sales.isEmpty()) "Sin ventas registradas" else "Sin resultados para los filtros aplicados",
                                color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    items(filteredSales) { sale ->
                        val statusColor = when (sale.status) {
                            "COMPLETED" -> successColor
                            "IN_PROGRESS" -> Color(0xFF1565C0)
                            else -> Color(0xFFF9A825)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                                    Text("Orden #${sale.id}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text(
                                        when (sale.status) {
                                            "PENDING" -> "Pendiente"
                                            "IN_PROGRESS" -> "Preparando"
                                            "COMPLETED" -> "Completado"
                                            "DELIVERED" -> "Entregado"
                                            else -> sale.status
                                        },
                                        color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("🍕  ${sale.pizzaName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Cliente: ${sale.clientName}", color = Color.Gray, fontSize = 13.sp)
                                Text("Mesa: ${sale.tableNumber}", color = Color.Gray, fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Column {
                                        Text("PRECIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("$${String.format(Locale.US, "%.2f", sale.price)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = pizzaOrange)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PAGÓ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("$${String.format(Locale.US, "%.2f", sale.totalPaid)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("CAMBIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = successColor)
                                        Text("$${String.format(Locale.US, "%.2f", sale.changeReturned)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = successColor)
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
