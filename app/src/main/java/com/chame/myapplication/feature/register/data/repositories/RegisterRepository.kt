package com.chame.myapplication.feacture.register.data.repositories

import com.chame.myapplication.feacture.register.data.datasource.RegisterApi
import com.chame.myapplication.feacture.register.data.datasource.mapper.toDomain
import com.chame.myapplication.feacture.register.data.datasource.model.RegisterRequestDto
import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse
import com.chame.myapplication.feacture.register.domain.repositories.RegisterRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(private val api: RegisterApi) : RegisterRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<RegisteResponse> {
        return try {
            val response = api.register(
                RegisterRequestDto(name = name, email = email, password = password)
            ).toDomain()
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
