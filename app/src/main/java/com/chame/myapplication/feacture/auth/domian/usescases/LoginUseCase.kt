package com.chame.myapplication.feacture.auth.domian.usescases

import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        return repository.login(email, password)
    }
}