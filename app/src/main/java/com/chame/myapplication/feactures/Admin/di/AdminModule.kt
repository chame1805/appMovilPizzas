package com.chame.myapplication.feactures.Admin.di

import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.feactures.Admin.presentation.viewModel.AdminViewModelFactory

class AdminModule(private val appContainer: AppContainer) {
    fun provideAdminViewModelFactory(): AdminViewModelFactory {
        return AdminViewModelFactory(
            getPizzasUseCase = appContainer.getPizzasUseCase,
            createPizzaUseCase = appContainer.createPizzaUseCase,
            updatePizzaUseCase = appContainer.updatePizzaUseCase,
            deletePizzaUseCase = appContainer.deletePizzaUseCase
        )
    }
}