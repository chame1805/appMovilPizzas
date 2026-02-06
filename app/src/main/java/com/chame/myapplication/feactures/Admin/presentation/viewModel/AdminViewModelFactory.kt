package com.chame.myapplication.feactures.Admin.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chame.myapplication.feactures.Admin.domain.usescases.*

class AdminViewModelFactory(
    private val getPizzasUseCase: GetPizzasUseCase,
    private val createPizzaUseCase: CreatePizzaUseCase,
    private val updatePizzaUseCase: UpdatePizzaUseCase,
    private val deletePizzaUseCase: DeletePizzaUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(
                getPizzasUseCase,
                createPizzaUseCase,
                updatePizzaUseCase,
                deletePizzaUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}