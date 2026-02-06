package com.chame.myapplication.feacture.auth.data.repositories

import com.chame.myapplication.feacture.auth.data.datasource.AuthApi
import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            api.login(mapOf("email" to email, "password" to password))
        }
    }
}