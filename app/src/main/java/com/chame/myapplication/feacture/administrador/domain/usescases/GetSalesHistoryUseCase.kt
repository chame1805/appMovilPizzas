package com.chame.myapplication.feacture.administrador.domain.usescases

import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord
import com.chame.myapplication.feacture.administrador.domain.repositories.AdminOrdersRepository
import javax.inject.Inject

class GetSalesHistoryUseCase @Inject constructor(
    private val repository: AdminOrdersRepository
) {
    suspend operator fun invoke(): Result<List<SaleRecord>> {
        return repository.getSalesHistory()
    }
}
