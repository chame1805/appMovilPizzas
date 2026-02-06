package com.chame.myapplication.feacture.register.domain.usescases

import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse
import com.chame.myapplication.feacture.register.domain.repositories.RegisterRepository

class RegisterUseCase(private val repository: RegisterRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<RegisteResponse> {
        return repository.register(name, email, password)
    }
}
