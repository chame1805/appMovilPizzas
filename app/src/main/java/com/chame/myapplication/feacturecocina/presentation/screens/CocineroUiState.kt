package com.chame.myapplication.feacturecocina.presentation.screens

import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder

data class CocineroUiState(
    val isLoading: Boolean = false,
    val orders: List<KitchenOrder> = emptyList(),
    val error: String? = null
)
