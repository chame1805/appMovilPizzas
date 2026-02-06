package com.chame.myapplication.feacture.auth.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.auth.domian.usescases.LoginUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun onLoginSuccess(navigateToMenu: () -> Unit) {
        navigateToMenu()
    }

    fun loginAdmin(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = loginUseCase(email, pass)

            result.onSuccess {
                _isLoading.value = false
                onSuccess() // Navegación al éxito
            }.onFailure {
                _isLoading.value = false
                _errorMessage.value = "Credenciales incorrectas"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}