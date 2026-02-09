package com.chame.myapplication.feactures.Admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.presentation.viewModel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state = viewModel.uiState
    val pizzaOrange = Color(0xFFE65100)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ADMINISTRACIÓN MENÚ", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onOpenDialog() },
                containerColor = pizzaOrange,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Nuevo") }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.pizzas) { pizza ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(pizza.nombre, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("$${pizza.precio}", color = pizzaOrange, fontWeight = FontWeight.Bold)
                            }
                            Row {
                                IconButton(onClick = { viewModel.onOpenDialog(pizza) }) {
                                    Icon(Icons.Default.Edit, "Editar", tint = Color.Gray)
                                }
                                IconButton(onClick = { pizza.id?.let { viewModel.removePizza(it) } }) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = pizzaOrange)
            }
        }

        if (state.isDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDialog() },
                title = {
                    Text(
                        if (state.selectedPizza == null) "NUEVA PIZZA" else "EDITAR PIZZA",
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = state.nombreInput,
                            onValueChange = { viewModel.onNombreChange(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.precioInput,
                            onValueChange = { viewModel.onPrecioChange(it) },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.savePizza() },
                        colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange),
                        enabled = state.nombreInput.isNotBlank() && state.precioInput.isNotBlank()
                    ) { Text("GUARDAR") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onDismissDialog() }) {
                        Text("CANCELAR", color = Color.Gray)
                    }
                }
            )
        }
    }
}