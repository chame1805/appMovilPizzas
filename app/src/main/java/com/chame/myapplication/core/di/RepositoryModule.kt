package com.chame.myapplication.core.di

import com.chame.myapplication.feacture.auth.data.repositories.AuthRepositoryImpl
import com.chame.myapplication.feacture.auth.domian.repositories.AuthRepository
import com.chame.myapplication.feacture.register.data.repositories.RegisterRepositoryImpl
import com.chame.myapplication.feacture.register.domain.repositories.RegisterRepository
import com.chame.myapplication.feactures.Admin.data.repositories.PizzaAdminRepositoryImpl
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository
import com.chame.myapplication.features.pizzeriadistrito.data.repositories.PizzaRepositoryImpl
import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.PizzaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPizzaRepository(impl: PizzaRepositoryImpl): PizzaRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(impl: RegisterRepositoryImpl): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(impl: PizzaAdminRepositoryImpl): PizzaAdminRepository
}
