package com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.mapper

import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.model.WaiterOrderDto
import com.chame.myapplication.features.pizzeriadistrito.domain.entities.WaiterOrder

fun WaiterOrderDto.toDomain(): WaiterOrder = WaiterOrder(
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
