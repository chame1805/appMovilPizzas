package com.chame.myapplication.feacture.auth.data.repositories

import com.chame.myapplication.feacture.auth.data.datasource.AuthApi
import com.chame.myapplication.feacture.auth.data.datasource.mapper.toDomain
import com.chame.myapplication.feacture.auth.data.datasource.model.LoginRequestDto
import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            api.login(LoginRequestDto(email = email, password = password)).toDomain()
        }
    }
}
