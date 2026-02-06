package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chame.myapplication.features.pizzeriadistrito.presentation.components.PizzaCard
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.PizzaViewModel
import com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel.PizzaViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaMenuScreen(
    viewModelFactory: PizzaViewModelFactory,
    onPizzaClick: (String, Double) -> Unit,
    onHistoryClick: () -> Unit
){
    val viewModel : PizzaViewModel = viewModel(factory = viewModelFactory)
    val menuState by viewModel.uiState.collectAsStateWithLifecycle()

    // Tu paleta de colores oficial
    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(pizzaYellow.copy(alpha = 0.15f), Color.White)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "DISTRITO PIZZA",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Historial"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(contentPadding)
        ) {
            when {
                menuState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = pizzaOrange
                    )
                }
                menuState.error != null -> {
                    Text(
                        text = menuState.error ?: "Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "NUESTRAS ESPECIALIDADES",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = pizzaOrange,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(menuState.pizzas) { pizzaItem ->
                            PizzaCard(
                                name = pizzaItem.name,
                                price = pizzaItem.price,
                                imageUrl = pizzaItem.imagenUrl,
                                modifier = Modifier.clickable {
                                    onPizzaClick(pizzaItem.name, pizzaItem.price)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}