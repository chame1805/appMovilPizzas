package com.chame.myapplication.feacture.auth.domian.repositories

import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResponse>
}