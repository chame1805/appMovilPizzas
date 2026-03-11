package com.chame.myapplication.feacture.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.auth.domian.usescases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(
        email: String,
        password: String,
        onMesero: () -> Unit,
        onCocinero: () -> Unit,
        onAdmin: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase(email, password)

            result.onSuccess { response ->
                _uiState.update { state -> state.copy(isLoading = false) }
                when (response.rol) {
                    "COCINERO" -> onCocinero()
                    "ADMIN" -> onAdmin()
                    else -> onMesero()
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(isLoading = false, errorMessage = "Credenciales incorrectas")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
