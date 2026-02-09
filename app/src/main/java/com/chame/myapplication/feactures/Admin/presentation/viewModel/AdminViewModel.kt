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

    fun onOpenDialog(pizza: Pizza? = null) {
        uiState = uiState.copy(
            isDialogVisible = true,
            selectedPizza = pizza,
            nombreInput = pizza?.nombre ?: "",
            precioInput = pizza?.precio?.toString() ?: ""
        )
    }

    fun onDismissDialog() {
        uiState = uiState.copy(isDialogVisible = false)
    }

    fun onNombreChange(nuevoNombre: String) {
        uiState = uiState.copy(nombreInput = nuevoNombre)
    }

    fun onPrecioChange(nuevoPrecio: String) {
        uiState = uiState.copy(precioInput = nuevoPrecio)
    }


    fun loadPizzas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            getPizzasUseCase().onSuccess { lista ->
                uiState = uiState.copy(pizzas = lista, isLoading = false)
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error al cargar")
            }
        }
    }

    fun savePizza() {
        val nombre = uiState.nombreInput
        val precio = uiState.precioInput.toDoubleOrNull() ?: 0.0
        val pizzaActual = uiState.selectedPizza

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, isDialogVisible = false)

            val result = if (pizzaActual == null) {
                createPizzaUseCase(Pizza(nombre = nombre, precio = precio))
            } else {
                pizzaActual.id?.let { updatePizzaUseCase(it, Pizza(nombre = nombre, precio = precio)) }
                    ?: Result.failure(Exception("ID no encontrado"))
            }

            result.onSuccess {
                loadPizzas()
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Error al guardar pizza")
            }
        }
    }

    fun removePizza(id: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            deletePizzaUseCase(id).onSuccess {
                loadPizzas()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo eliminar")
            }
        }
    }
}