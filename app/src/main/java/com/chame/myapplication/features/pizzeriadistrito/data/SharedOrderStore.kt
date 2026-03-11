package com.chame.myapplication.features.pizzeriadistrito.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedOrderStore @Inject constructor() {

    private val payments = mutableMapOf<Int, Pair<Double, Double>>()

    fun savePayment(orderId: Int, totalPaid: Double, changeReturned: Double) {
        payments[orderId] = Pair(totalPaid, changeReturned)
    }

    fun getPayment(orderId: Int): Pair<Double, Double>? = payments[orderId]
}
