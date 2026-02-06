package com.chame.myapplication.feactures.Admin.domain.usescases


import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

class CreatePizzaUseCase(private val repository: PizzaAdminRepository) {
    suspend operator fun invoke(pizza: Pizza): Result<Pizza> = repository.createPizza(pizza)
}