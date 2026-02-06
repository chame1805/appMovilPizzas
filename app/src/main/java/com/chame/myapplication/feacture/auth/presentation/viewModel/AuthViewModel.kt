package com.chame.myapplication.feacture.auth.presentation.viewModel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    fun onLoginSuccess(navigateToMenu: () -> Unit) = navigateToMenu()
    fun onAdminAccess(navigateToAdmin: () -> Unit) = navigateToAdmin()
}