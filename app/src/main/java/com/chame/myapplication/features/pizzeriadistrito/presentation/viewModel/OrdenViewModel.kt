package com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel


import androidx.lifecycle.ViewModel
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    fun onClientNameChange(newName: String) {
        _uiState.update { it.copy(clientName = newName) }
    }

    fun onAddressChange(newAddress: String) {
        _uiState.update { it.copy(address = newAddress) }
    }

    fun onAmountPaidChange(newAmount: String, pizzaPrice: Double) {
        // Solo permitimos números y un punto decimal
        if (newAmount.all { it.isDigit() || it == '.' }) {
            val amount = newAmount.toDoubleOrNull() ?: 0.0
            _uiState.update { it.copy(
                amountPaidText = newAmount,
                change = amount - pizzaPrice,
                isPaymentSufficient = amount >= pizzaPrice
            ) }
        }
    }
}