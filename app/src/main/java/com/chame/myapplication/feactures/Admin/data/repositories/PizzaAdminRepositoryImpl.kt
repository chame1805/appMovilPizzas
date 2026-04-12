package com.chame.myapplication.feactures.Admin.data.repositories

import com.chame.myapplication.feactures.Admin.data.datasource.AdminApi
import com.chame.myapplication.feactures.Admin.data.datasource.mapper.toDomain
import com.chame.myapplication.feactures.Admin.data.datasource.mapper.toDto
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository
import javax.inject.Inject

class PizzaAdminRepositoryImpl @Inject constructor(private val api: AdminApi) : PizzaAdminRepository {
    override suspend fun getPizzas(): Result<List<Pizza>> = runCatching {
        api.getPizzas().map { it.toDomain() }
    }

    override suspend fun createPizza(pizza: Pizza): Result<Pizza> = runCatching {
        api.createPizza(pizza.toDto()).toDomain()
    }

    override suspend fun updatePizza(id: Int, pizza: Pizza): Result<Pizza> = runCatching {
        api.updatePizza(id, pizza.toDto()).toDomain()
    }

    override suspend fun deletePizza(id: Int): Result<Unit> = runCatching {
        api.deletePizza(id)
    }
}
