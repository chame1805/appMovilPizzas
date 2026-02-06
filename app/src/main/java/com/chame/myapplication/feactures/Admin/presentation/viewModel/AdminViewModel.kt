package com.chame.myapplication.feactures.Admin.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.usescases.*
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminUiState
import kotlinx.coroutines.launch

class AdminViewModel(
    private val getPizzasUseCase: GetPizzasUseCase,
    private val createPizzaUseCase: CreatePizzaUseCase,
    private val updatePizzaUseCase: UpdatePizzaUseCase,
    private val deletePizzaUseCase: DeletePizzaUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AdminUiState())
        private set

    init { loadPizzas() }

    fun loadPizzas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            getPizzasUseCase().onSuccess { lista ->
                uiState = uiState.copy(pizzas = lista, isLoading = false)
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error al cargar pizzas")
            }
        }
    }

    fun addPizza(nombre: String, precio: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            createPizzaUseCase(Pizza(nombre = nombre, precio = precio)).onSuccess {
                loadPizzas() // Recargamos la lista tras el éxito
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "No se pudo agregar la pizza")
            }
        }
    }

    fun editPizza(id: Int, nombre: String, precio: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            updatePizzaUseCase(id, Pizza(nombre = nombre, precio = precio)).onSuccess {
                loadPizzas()
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Error al editar")
            }
        }
    }

    fun removePizza(id: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            deletePizzaUseCase(id).onSuccess {
                loadPizzas()
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Error al eliminar")
            }
        }
    }
}