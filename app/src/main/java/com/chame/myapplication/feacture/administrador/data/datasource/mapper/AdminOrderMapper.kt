package com.chame.myapplication.feacture.administrador.data.datasource.mapper

import com.chame.myapplication.feacture.administrador.data.datasource.model.SaleRecordDto
import com.chame.myapplication.feacture.administrador.domain.entities.SaleRecord

fun SaleRecordDto.toDomain(): SaleRecord = SaleRecord(
    id = id,
    pizzaName = pizzaName,
    price = price,
    clientName = clientName,
    totalPaid = totalPaid,
    changeReturned = changeReturned,
    tableNumber = tableNumber,
    status = status,
    createdAt = createdAt
)
