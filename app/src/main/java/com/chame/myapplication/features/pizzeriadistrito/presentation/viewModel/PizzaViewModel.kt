package com.chame.myapplication.features.pizzeriadistrito.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.features.pizzeriadistrito.domain.usecases.GetPizzasUseCase
import com.chame.myapplication.features.pizzeriadistrito.presentation.screens.PizzaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PizzaViewModel @Inject constructor(
    private val getPizzasUseCase: GetPizzasUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PizzaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    private fun loadMenu() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = getPizzasUseCase()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(isLoading = false, pizzas = list)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }
}
