package com.chame.myapplication.feactures.Admin.domain.usescases

import com.chame.myapplication.feactures.Admin.domain.repositories.PizzaAdminRepository

import javax.inject.Inject

class DeletePizzaUseCase @Inject constructor(private val repository: PizzaAdminRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deletePizza(id)
}