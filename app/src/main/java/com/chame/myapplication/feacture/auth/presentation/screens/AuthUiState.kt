package com.chame.myapplication.feacture.auth.presentation.screens

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAdminDialog: Boolean = false
)