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
    val pizzaOrange = Color(0xFFE65100)
    var showDialog by remember { mutableStateOf(false) }
    var selectedPizza by remember { mutableStateOf<Pizza?>(null) }

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

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
                onClick = {
                    selectedPizza = null
                    nombre = ""; precio = ""
                    showDialog = true
                },
                containerColor = pizzaOrange,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Nuevo") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.pizzas) { pizza ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(pizza.nombre, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("$${pizza.precio}", color = pizzaOrange, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            IconButton(onClick = {
                                selectedPizza = pizza
                                nombre = pizza.nombre
                                precio = pizza.precio.toString()
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, "Editar", tint = Color.Gray) }

                            IconButton(onClick = { pizza.id?.let { viewModel.removePizza(it) } }) {
                                Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (selectedPizza == null) "NUEVA PIZZA" else "EDITAR PIZZA", fontWeight = FontWeight.Black) },
                text = {
                    Column {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val p = precio.toDoubleOrNull() ?: 0.0
                            if (selectedPizza == null) viewModel.addPizza(nombre, p)
                            else selectedPizza!!.id?.let { viewModel.editPizza(it, nombre, p) }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
                    ) { Text("GUARDAR") }
                }
            )
        }
    }
}