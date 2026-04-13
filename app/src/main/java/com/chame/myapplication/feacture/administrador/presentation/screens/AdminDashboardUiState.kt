package com.chame.myapplication.feacture.administrador.presentation.screens

import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val sales: List<SaleRecord> = emptyList(),
    val error: String? = null
)
