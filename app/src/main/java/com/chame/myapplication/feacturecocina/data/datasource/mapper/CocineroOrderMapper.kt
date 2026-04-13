package com.chame.myapplication.feacturecocina.data.datasource.mapper

import com.chame.myapplication.feacturecocina.data.datasource.model.KitchenOrderDto
import com.chame.myapplication.feacturecocina.domain.entities.KitchenOrder

fun KitchenOrderDto.toDomain(): KitchenOrder = KitchenOrder(
    id = id,
    pizzaName = pizzaName,
    tableNumber = tableNumber,
    clientName = clientName,
    status = normalizeStatus(status),
    createdAt = createdAt
)


private fun normalizeStatus(status: String): String {
    val normalized = status.trim().uppercase().replace(' ' , '_')
    return when (normalized) {
        "LISTA", "READY" -> "COMPLETED"
        "PREPARANDO" -> "IN_PROGRESS"
        else -> normalized
    }
}
