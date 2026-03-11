package com.chame.myapplication.feacture.auth.data.repositories

import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacture.auth.data.datasource.AuthApi
import com.chame.myapplication.feacture.auth.data.datasource.mapper.toDomain
import com.chame.myapplication.feacture.auth.data.datasource.model.LoginRequestDto
import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            val response = api.login(LoginRequestDto(email = email, password = password)).toDomain()
            sessionManager.saveSession(
                token = response.accessToken,
                userId = response.userId,
                name = response.nombre,
                role = response.rol,
                email = response.email
            )
            response
        }
    }
}
