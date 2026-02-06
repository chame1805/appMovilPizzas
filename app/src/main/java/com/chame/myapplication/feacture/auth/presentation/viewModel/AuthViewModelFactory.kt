package com.chame.myapplication.feacture.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chame.myapplication.feacture.auth.domian.usescases.LoginUseCase

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase // <--- Ahora recibe el caso de uso
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Se lo pasamos al AuthViewModel al momento de crearlo
            return AuthViewModel(loginUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}