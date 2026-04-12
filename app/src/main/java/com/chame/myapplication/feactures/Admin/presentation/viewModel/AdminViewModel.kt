package com.chame.myapplication.feactures.Admin.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.usescases.CreatePizzaUseCase
import com.chame.myapplication.feactures.Admin.domain.usescases.DeletePizzaUseCase
import com.chame.myapplication.feactures.Admin.domain.usescases.GetPizzasUseCase
import com.chame.myapplication.feactures.Admin.domain.usescases.UpdatePizzaUseCase
import com.chame.myapplication.feactures.Admin.presentation.screens.AdminUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getPizzasUseCase: GetPizzasUseCase,
    private val createPizzaUseCase: CreatePizzaUseCase,
    private val updatePizzaUseCase: UpdatePizzaUseCase,
    private val deletePizzaUseCase: DeletePizzaUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AdminUiState())
        private set

    init {
        loadPizzas()
    }

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
                loadPizzas()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo agregar la pizza")
            }
        }
    }

    fun editPizza(id: Int, nombre: String, precio: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            updatePizzaUseCase(id, Pizza(nombre = nombre, precio = precio)).onSuccess {
                loadPizzas()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "Error al editar")
            }
        }
    }

    fun removePizza(id: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            deletePizzaUseCase(id).onSuccess {
                loadPizzas()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "Error al eliminar")
            }
        }
    }
}
