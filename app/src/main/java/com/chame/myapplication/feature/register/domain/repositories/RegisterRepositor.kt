package com.chame.myapplication.feacture.register.domain.repositories

import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse

interface RegisterRepository {
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<RegisteResponse>
}
