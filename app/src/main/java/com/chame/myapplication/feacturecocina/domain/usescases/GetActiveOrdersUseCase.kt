package com.chame.myapplication.feacturecocina.domain.usescases

import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder
import com.chame.myapplication.feacturecocina.domain.repositories.CocineroRepository
import javax.inject.Inject

class GetActiveOrdersUseCase @Inject constructor(
    private val repository: CocineroRepository
) {
    suspend operator fun invoke(): Result<List<KitchenOrder>> {
        return repository.getActiveOrders()
    }
}
