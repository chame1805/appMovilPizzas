package com.chame.myapplication.core.di

import android.content.Context
import com.chame.myapplication.core.network.PizzeriaApi
import com.chame.myapplication.features.pizzeriadistrito.data.repositories.PizzaRepositoryImpl
import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.PizzaRepository

// --- IMPORTACIONES DE ADMIN ---
import com.chame.myapplication.feactures.Admin.data.datasource.AdminApi
import com.chame.myapplication.feactures.Admin.data.repositories.PizzaAdminRepositoryImpl
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository
import com.chame.myapplication.feactures.Admin.domain.usescases.*

// --- IMPORTACIONES DE AUTH (LOGIN) ---
import com.chame.myapplication.feacture.auth.data.datasource.AuthApi
import com.chame.myapplication.feacture.auth.data.repositories.AuthRepositoryImpl
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository
import com.chame.myapplication.feacture.auth.domian.usescases.LoginUseCase

// --- IMPORTACIONES DE REGISTER ---
import com.chame.myapplication.feacture.register.data.datasource.RegisterApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // ✅ ESTE ES TU RETROFIT REAL (YA NO Any, YA NO TODO())
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://44.212.148.188:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- 1. SECCIÓN DE VENTAS ---
    val pizzeriaApi: PizzeriaApi by lazy {
        retrofit.create(PizzeriaApi::class.java)
    }

    val pizzaRepository: PizzaRepository by lazy {
        PizzaRepositoryImpl(pizzeriaApi)
    }

    // --- 2. SECCIÓN DE AUTENTICACIÓN (LOGIN) ---
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    // --- 2.1 SECCIÓN DE REGISTER (NUEVO) ---
    val registerApi: RegisterApi by lazy {
        retrofit.create(RegisterApi::class.java)
    }

    // --- 3. SECCIÓN ADMIN ---
    val adminApi: AdminApi by lazy {
        retrofit.create(AdminApi::class.java)
    }

    private val pizzaAdminRepository: PizzaAdminRepository by lazy {
        PizzaAdminRepositoryImpl(adminApi)
    }

    // Casos de uso
    val getPizzasUseCase get() = GetPizzasUseCase(pizzaAdminRepository)
    val createPizzaUseCase get() = CreatePizzaUseCase(pizzaAdminRepository)
    val updatePizzaUseCase get() = UpdatePizzaUseCase(pizzaAdminRepository)
    val deletePizzaUseCase get() = DeletePizzaUseCase(pizzaAdminRepository)
}
