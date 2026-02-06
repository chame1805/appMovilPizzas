package com.chame.myapplication.feactures.Admin.data.repositories

import com.chame.myapplication.feactures.Admin.data.datasource.AdminApi
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

class PizzaAdminRepositoryImpl(private val api: AdminApi) : PizzaAdminRepository {
    override suspend fun getPizzas(): Result<List<Pizza>> = runCatching { api.getPizzas() }
    override suspend fun createPizza(pizza: Pizza): Result<Pizza> = runCatching { api.createPizza(pizza) }
    override suspend fun updatePizza(id: Int, pizza: Pizza): Result<Pizza> = runCatching { api.updatePizza(id, pizza) }
    override suspend fun deletePizza(id: Int): Result<Unit> = runCatching { api.deletePizza(id) }
}