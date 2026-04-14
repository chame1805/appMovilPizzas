package com.chame.myapplication.feacturecocina.presentation.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.feacturecocina.presentation.viewModel.CocineroViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocineroScreen(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: CocineroViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            currentTime = System.currentTimeMillis()
        }
    }

    val pizzaOrange = Color(0xFFE65100)
    val pendingYellow = Color(0xFFF9A825)
    val inProgressBlue = Color(0xFF1565C0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("COCINA", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "${state.orders.size} órdenes activas",
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
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
                    }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading && state.orders.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = pizzaOrange
                    )
                }

                state.orders.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sin órdenes pendientes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text("Las nuevas órdenes aparecerán aquí", color = Color.LightGray, fontSize = 14.sp)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders, key = { it.id }) { order ->
                            val statusColor = when (order.status) {
                                "PENDING" -> pendingYellow
                                "IN_PROGRESS" -> inProgressBlue
                                "COMPLETED" -> pizzaOrange
                                else -> Color.Gray
                            }
                            val nextStatus = viewModel.getNextStatus(order.status)
                            val elapsed = elapsedTime(order.createdAt, currentTime)

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = statusColor,
                                                shape = CircleShape,
                                                modifier = Modifier.size(12.dp)
                                            ) {}
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Orden #${order.id}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (elapsed.isNotBlank()) {
                                                Icon(
                                                    Icons.Default.Timer,
                                                    contentDescription = null,
                                                    tint = when {
                                                        isUrgent(order.createdAt, currentTime) -> Color.Red
                                                        else -> Color.Gray
                                                    },
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(Modifier.width(3.dp))
                                                Text(
                                                    elapsed,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when {
                                                        isUrgent(order.createdAt, currentTime) -> Color.Red
                                                        else -> Color.Gray
                                                    }
                                                )
                                                Spacer(Modifier.width(8.dp))
                                            }
                                            Surface(
                                                color = statusColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    viewModel.getStatusLabel(order.status),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    color = statusColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Text("🍕  ${order.pizzaName}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Mesa: ${order.tableNumber}", color = Color.Gray, fontSize = 14.sp)
                                    Text("Cliente: ${order.clientName}", color = Color.Gray, fontSize = 14.sp)

                                    if (nextStatus != null) {
                                        Spacer(Modifier.height(12.dp))
                                        val buttonColor = when (nextStatus) {
                                            "IN_PROGRESS" -> inProgressBlue
                                            "COMPLETED" -> pizzaOrange
                                            else -> pizzaOrange
                                        }
                                        val buttonText = when (nextStatus) {
                                            "IN_PROGRESS" -> "INICIAR PREPARACIÓN"
                                            "COMPLETED" -> "MARCAR COMO LISTA"
                                            else -> "SIGUIENTE"
                                        }
                                        Button(
                                            onClick = { viewModel.updateStatus(order.id, nextStatus) },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                                        ) {
                                            Text(buttonText, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                        }
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

private fun elapsedTime(createdAt: String, currentTime: Long): String {
    if (createdAt.isBlank()) return ""
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(createdAt) ?: return ""
        val mins = (currentTime - date.time) / 60_000
        when {
            mins < 1 -> "< 1 min"
            mins < 60 -> "${mins} min"
            else -> "${mins / 60}h ${mins % 60}min"
        }
    }.getOrDefault("")
}

private fun isUrgent(createdAt: String, currentTime: Long): Boolean {
    if (createdAt.isBlank()) return false
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(createdAt) ?: return false
        (currentTime - date.time) / 60_000 >= 15
    }.getOrDefault(false)
}
