package com.chame.myapplication.feacture.register.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.register.domain.usescases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            registerUseCase(name, email, pass)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = it.message ?: "No se pudo registrar")
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
