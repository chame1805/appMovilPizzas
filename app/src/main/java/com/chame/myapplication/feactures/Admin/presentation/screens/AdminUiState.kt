package com.chame.myapplication.feactures.Admin.presentation.screens

import com.chame.myapplication.feactures.Admin.domain.entities.Pizza

data class AdminUiState(
    val isLoading: Boolean = false,
    val pizzas: List<Pizza> = emptyList(),
    val error: String? = null,

    val isDialogVisible: Boolean = false,
    val selectedPizza: Pizza? = null, // Si es null, estamos creando; si no, editando
    val nombreInput: String = "",
    val precioInput: String = ""
)