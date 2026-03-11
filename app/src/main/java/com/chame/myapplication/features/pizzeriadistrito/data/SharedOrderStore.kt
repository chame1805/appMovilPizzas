package com.chame.myapplication.features.pizzeriadistrito.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedOrderStore @Inject constructor() {

    data class OrderPayment(val price: Double, val totalPaid: Double, val changeReturned: Double)

    private val payments = mutableMapOf<Int, OrderPayment>()

    fun savePayment(orderId: Int, price: Double, totalPaid: Double, changeReturned: Double) {
        payments[orderId] = OrderPayment(price, totalPaid, changeReturned)
    }

    fun getPayment(orderId: Int): OrderPayment? = payments[orderId]
}
