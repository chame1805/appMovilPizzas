package com.chame.myapplication.feacture.register.data.repositories

import com.chame.myapplication.feacture.register.data.datasource.RegisterApi
import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse
import com.chame.myapplication.feacture.register.domain.repositories.RegisterRepository
import retrofit2.HttpException
import java.io.IOException

class RegisterRepositoryImpl(private val api: RegisterApi) : RegisterRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<RegisteResponse> {
        return try {
            val response = api.register(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )
            )
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: IOException) {
            Result.failure(Exception("No hay conexión con el servidor"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message ?: "desconocido"}"))
        }
    }
}
