package com.chame.myapplication.feacture.register.data.repositories

import com.chame.myapplication.feacture.register.data.datasource.RegisterApi
import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse
import com.chame.myapplication.feacture.register.domain.repositories.RegisterRepository

class RegisterRepositoryImpl(private val api: RegisterApi) : RegisterRepository {
    override suspend fun register(
        nombre: String,
        email: String,
        password: String
    ): Result<RegisteResponse> {
        return runCatching {
            api.register(mapOf("name" to nombre, "email" to email, "password" to password))
        }
    }
}
