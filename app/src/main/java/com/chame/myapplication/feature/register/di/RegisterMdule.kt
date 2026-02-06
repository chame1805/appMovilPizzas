package com.chame.myapplication.feacture.register.di

import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.feacture.register.data.repositories.RegisterRepositoryImpl
import com.chame.myapplication.feacture.register.domain.usescases.RegisterUseCase
import com.chame.myapplication.feacture.register.presentation.viewModel.RegisterViewModelFactory

class RegisterModule(private val appContainer: AppContainer) {

    private val repository by lazy { RegisterRepositoryImpl(appContainer.registerApi) }
    private val useCase by lazy { RegisterUseCase(repository) }

    fun provideRegisterViewModelFactory(): RegisterViewModelFactory {
        return RegisterViewModelFactory(useCase)
    }
}
