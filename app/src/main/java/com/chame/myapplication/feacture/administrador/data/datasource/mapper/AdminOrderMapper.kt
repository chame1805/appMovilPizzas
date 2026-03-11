package com.chame.myapplication.feacture.administrador.data.datasource.mapper

import com.chame.myapplication.feacture.administrador.data.datasource.model.SaleRecordDto
import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord

fun SaleRecordDto.toDomain(): SaleRecord = SaleRecord(
    id = id,
    pizzaName = pizzaName,
    price = price ?: 0.0,
    clientName = clientName,
    totalPaid = totalPaid ?: 0.0,
    changeReturned = changeReturned ?: 0.0,
    tableNumber = tableNumber,
    status = status,
    createdAt = createdAt
)
