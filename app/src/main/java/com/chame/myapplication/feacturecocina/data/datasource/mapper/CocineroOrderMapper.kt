package com.chame.myapplication.feacturecocina.data.datasource.mapper

import com.chame.myapplication.feacturecocina.data.datasource.model.KitchenOrderDto
import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder

fun KitchenOrderDto.toDomain(): KitchenOrder = KitchenOrder(
    id = id,
    pizzaName = pizzaName,
    tableNumber = tableNumber,
    clientName = clientName,
    status = status,
    createdAt = createdAt
)
