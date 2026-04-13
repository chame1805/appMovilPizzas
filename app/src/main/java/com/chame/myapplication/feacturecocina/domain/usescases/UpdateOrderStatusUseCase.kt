package com.chame.myapplication.feacturecocina.domain.usescases

import com.chame.myapplication.feacturecocina.domain.repositories.CocineroRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val repository: CocineroRepository
) {
    suspend operator fun invoke(orderId: Int, newStatus: String): Result<Unit> {
        return repository.updateOrderStatus(orderId, newStatus)
    }
}
