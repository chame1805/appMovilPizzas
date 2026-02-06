package com.chame.myapplication.feactures.Admin.domain.usescases


import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

class UpdatePizzaUseCase(private val repository: PizzaAdminRepository) {
    suspend operator fun invoke(id: Int, pizza: Pizza): Result<Pizza> = repository.updatePizza(id, pizza)
}