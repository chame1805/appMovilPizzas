package com.chame.myapplication.core.services

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderServiceHelper @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "OrderServiceHelper"
    }
    
    fun startOrderTracking(orderId: String, status: String = "Procesando tu orden") {
        try {
            val intent = Intent(context, OrderTrackingService::class.java).apply {
                putExtra(OrderTrackingService.ORDER_ID_EXTRA, orderId)
                putExtra(OrderTrackingService.ORDER_STATUS_EXTRA, status)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
            
            Log.d(TAG, "Order tracking started for order: $orderId")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting order tracking", e)
        }
    }
    
    fun stopOrderTracking() {
        try {
            val intent = Intent(context, OrderTrackingService::class.java)
            context.stopService(intent)
            Log.d(TAG, "Order tracking stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping order tracking", e)
        }
    }
}
