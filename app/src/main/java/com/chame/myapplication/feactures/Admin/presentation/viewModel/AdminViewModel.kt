package com.chame.myapplication.feactures.Admin.presentation.viewModel

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getPizzasUseCase: GetPizzasUseCase,
    private val createPizzaUseCase: CreatePizzaUseCase,
    private val updatePizzaUseCase: UpdatePizzaUseCase,
    private val deletePizzaUseCase: DeletePizzaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadPizzas()
    }

    fun loadPizzas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPizzasUseCase().onSuccess { lista ->
                _uiState.update { it.copy(pizzas = lista, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar pizzas") }
            }
        }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(showDialog = true, selectedPizza = null, editNombre = "", editPrecio = "") }
    }

    fun openEditDialog(pizza: Pizza) {
        _uiState.update { it.copy(showDialog = true, selectedPizza = pizza, editNombre = pizza.nombre, editPrecio = pizza.precio.toString()) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    fun setEditNombre(value: String) {
        _uiState.update { it.copy(editNombre = value) }
    }

    fun setEditPrecio(value: String) {
        _uiState.update { it.copy(editPrecio = value) }
    }

    fun addPizza(nombre: String, precio: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createPizzaUseCase(Pizza(nombre = nombre, precio = precio)).onSuccess {
                loadPizzas()
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo agregar la pizza") }
            }
        }
    }

    fun editPizza(id: Int, nombre: String, precio: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updatePizzaUseCase(id, Pizza(nombre = nombre, precio = precio)).onSuccess {
                loadPizzas()
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Error al editar") }
            }
        }
    }

    fun removePizza(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deletePizzaUseCase(id).onSuccess {
                loadPizzas()
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") }
            }
        }
    }
}
