package com.chame.myapplication.feactures.Admin.domain.usescases

import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

class DeletePizzaUseCase(private val repository: PizzaAdminRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deletePizza(id)
}