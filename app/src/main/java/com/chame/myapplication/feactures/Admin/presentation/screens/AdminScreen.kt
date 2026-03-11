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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.feactures.Admin.presentation.viewModel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBackClick: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pizzaOrange = Color(0xFFE65100)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ADMINISTRACIÓN", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Gestión del menú", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = pizzaOrange,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Nuevo") }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- LISTA DE PIZZAS ---
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
                                IconButton(onClick = { viewModel.openEditDialog(pizza) }) {
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

            // --- INDICADOR DE CARGA ---
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = pizzaOrange
                )
            }

            // --- MENSAJE DE ERROR ---
            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- DIÁLOGO PARA CREAR/EDITAR ---
        if (state.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeDialog() },
                title = {
                    Text(
                        if (state.selectedPizza == null) "NUEVA PIZZA" else "EDITAR PIZZA",
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = state.editNombre,
                            onValueChange = { viewModel.setEditNombre(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.editPrecio,
                            onValueChange = { viewModel.setEditPrecio(it) },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val p = state.editPrecio.toDoubleOrNull() ?: 0.0
                            val pizza = state.selectedPizza
                            if (pizza == null) {
                                viewModel.addPizza(state.editNombre, p)
                            } else {
                                pizza.id?.let { viewModel.editPizza(it, state.editNombre, p) }
                            }
                            viewModel.closeDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange),
                        enabled = state.editNombre.isNotBlank() && state.editPrecio.isNotBlank()
                    ) { Text("GUARDAR") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeDialog() }) {
                        Text("CANCELAR", color = Color.Gray)
                    }
                }
            )
        }
    }
}