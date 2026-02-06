package com.chame.myapplication.feacture.auth.di

import com.chame.myapplication.core.di.AppContainer
import com.chame.myapplication.feacture.auth.presentation.viewModel.AuthViewModelFactory

class AuthModule(private val appContainer: AppContainer) {
    fun provideAuthViewModelFactory(): AuthViewModelFactory = AuthViewModelFactory()
}