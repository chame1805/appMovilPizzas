package com.chame.myapplication.feacture.auth.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.auth.domian.usescases.LoginUseCase
import com.chame.myapplication.feacture.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onOpenAdminDialog() {
        uiState = uiState.copy(showAdminDialog = true, errorMessage = null)
    }

    fun onDismissAdminDialog() {
        uiState = uiState.copy(showAdminDialog = false, errorMessage = null)
    }

    fun loginAdmin(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = loginUseCase(email, pass)

            result.onSuccess {
                uiState = uiState.copy(isLoading = false, showAdminDialog = false)
                onSuccess()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, errorMessage = "Credenciales incorrectas")
            }
        }
    }

    fun onLoginSuccess(navigateToMenu: () -> Unit) {
        navigateToMenu()
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}