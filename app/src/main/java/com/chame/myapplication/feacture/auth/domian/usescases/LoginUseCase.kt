package com.chame.myapplication.feacture.auth.domian.usescases

import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository

import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        return repository.login(email, password)
    }
}