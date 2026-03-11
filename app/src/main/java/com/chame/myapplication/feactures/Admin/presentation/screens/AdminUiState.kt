package com.chame.myapplication.feactures.Admin.presentation.screens

import com.chame.myapplication.feactures.Admin.domain.entities.Pizza

data class AdminUiState(
    val isLoading: Boolean = false,
    val pizzas: List<Pizza> = emptyList(),
    val error: String? = null,
    val showDialog: Boolean = false,
    val selectedPizza: Pizza? = null,
    val editNombre: String = "",
    val editPrecio: String = ""
)