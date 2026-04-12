package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import com.chame.myapplication.features.pizzeriadistrito.domain.entities.Order

object OrderMapper {
    
    fun toEntity(order: Order): OrderEntity {
        return OrderEntity(
            pizzaName = order.pizzaName,
            price = order.price,
            clientName = order.clientName,
            totalPaid = order.totalPaid,
            changeReturned = order.changeReturned,
            date = order.date
        )
    }
    
    fun toDomain(entity: OrderEntity): Order {
        return Order(
            pizzaName = entity.pizzaName,
            price = entity.price,
            clientName = entity.clientName,
            totalPaid = entity.totalPaid,
            changeReturned = entity.changeReturned,
            date = entity.date
        )
    }
    
    fun entitiesToDomain(entities: List<OrderEntity>): List<Order> {
        return entities.map { toDomain(it) }
    }
    
    fun domainToEntities(orders: List<Order>): List<OrderEntity> {
        return orders.map { toEntity(it) }
    }
}
