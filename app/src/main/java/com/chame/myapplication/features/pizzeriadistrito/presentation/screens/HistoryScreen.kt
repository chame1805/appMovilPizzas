package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.local.OrderStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBackClick: () -> Unit) {
    val orders = OrderStorage.orders.reversed()
    val pizzaOrange = Color(0xFFE65100)
    val successColor = Color(0xFF2E7D32)

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HISTORIAL DE CAJA", fontWeight = FontWeight.Black) },
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
        if (orders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.ShoppingCart, null, Modifier.size(64.dp), Color.LightGray)
                Text("No hay ventas hoy", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text(order.pizzaName, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text(order.date, color = Color.Gray, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null, Modifier.size(14.dp), Color.Gray)
                                Spacer(Modifier.width(4.dp))
                                Text(order.clientName, color = Color.Gray, fontSize = 14.sp)
                            }

                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column {
                                    Text("PAGÓ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text("$${String.format("%.2f", order.totalPaid)}", fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CAMBIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = successColor)
                                    Text("$${String.format("%.2f", order.changeReturned)}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = successColor)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}