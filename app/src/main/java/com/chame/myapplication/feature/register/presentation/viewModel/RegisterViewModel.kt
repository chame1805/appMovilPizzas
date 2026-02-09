package com.chame.myapplication.feacture.register.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chame.myapplication.feacture.register.domain.usescases.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // --- NUEVOS ESTADOS ELEVADOS ---
    private val _nameInput = mutableStateOf("")
    val nameInput: State<String> = _nameInput

    private val _emailInput = mutableStateOf("")
    val emailInput: State<String> = _emailInput

    private val _passwordInput = mutableStateOf("")
    val passwordInput: State<String> = _passwordInput
    // ------------------------------

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Funciones para actualizar el estado desde la UI
    fun onNameChange(newValue: String) { _nameInput.value = newValue }
    fun onEmailChange(newValue: String) { _emailInput.value = newValue }
    fun onPasswordChange(newValue: String) { _passwordInput.value = newValue }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Usamos los valores que ya tenemos en el ViewModel
            registerUseCase(_nameInput.value, _emailInput.value, _passwordInput.value)
                .onSuccess {
                    clearInputs()
                    onSuccess()
                }
                .onFailure { _errorMessage.value = "No se pudo registrar" }

            _isLoading.value = false
        }
    }

    private fun clearInputs() {
        _nameInput.value = ""
        _emailInput.value = ""
        _passwordInput.value = ""
    }

    fun clearError() { _errorMessage.value = null }
}