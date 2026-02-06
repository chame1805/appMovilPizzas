package com.chame.myapplication.feactures.Admin.domain.usescases


import com.chame.myapplication.feactures.Admin.domain.entities.Pizza
import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

class GetPizzasUseCase(private val repository: PizzaAdminRepository) {
    suspend operator fun invoke(): Result<List<Pizza>> = repository.getPizzas()
}