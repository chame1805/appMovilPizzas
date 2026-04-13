package com.chame.myapplication.feactures.Admin.domain.repositories


import  com.chame.myapplication.feactures.Admin.domain.entities.Pizza

interface PizzaAdminRepository {
    suspend fun getPizzas(): Result<List<Pizza>>
    suspend fun createPizza(pizza: Pizza): Result<Pizza>
    suspend fun updatePizza(id: Int, pizza: Pizza): Result<Pizza>
    suspend fun deletePizza(id: Int): Result<Unit>
}