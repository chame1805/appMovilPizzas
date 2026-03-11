package com.chame.myapplication.features.pizzeriadistrito.domain.usecases

import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder
import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import javax.inject.Inject

class GetWaiterOrdersUseCase @Inject constructor(
    private val repository: WaiterOrderRepository
) {
    suspend operator fun invoke(): Result<List<WaiterOrder>> {
        return repository.getMyOrders()
    }
}
