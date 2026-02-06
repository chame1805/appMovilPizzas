package com.chame.myapplication.feactures.Admin.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.usescases.*
import kotlinx.coroutines.launch

class AdminViewModel(
    private val getPizzasUseCase: GetPizzasUseCase,
    private val createPizzaUseCase: CreatePizzaUseCase,
    private val updatePizzaUseCase: UpdatePizzaUseCase,
    private val deletePizzaUseCase: DeletePizzaUseCase
) : ViewModel() {

    var pizzas by mutableStateOf<List<Pizza>>(emptyList())
    var isLoading by mutableStateOf(false)

    init { loadPizzas() }

    fun loadPizzas() {
        viewModelScope.launch {
            isLoading = true
            getPizzasUseCase().onSuccess { pizzas = it }
            isLoading = false
        }
    }

    fun addPizza(nombre: String, precio: Double) {
        viewModelScope.launch {
            createPizzaUseCase(Pizza(nombre = nombre, precio = precio)).onSuccess { loadPizzas() }
        }
    }

    fun editPizza(id: Int, nombre: String, precio: Double) {
        viewModelScope.launch {
            updatePizzaUseCase(id, Pizza(nombre = nombre, precio = precio)).onSuccess { loadPizzas() }
        }
    }

    fun removePizza(id: Int) {
        viewModelScope.launch {
            deletePizzaUseCase(id).onSuccess { loadPizzas() }
        }
    }
}