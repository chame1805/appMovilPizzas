package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

data class OrderUiState(
    val clientName: String = "",
    val address: String = "",
    val amountPaidText: String = "",
    val isPaymentSufficient: Boolean = false,
    val change: Double = 0.0
)