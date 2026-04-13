package com.chame.myapplication.core.websocket

import com.chame.myapplication.core.session.SessionManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

data class KitchenEvent(
    val event: String,
    val id: Int,
    val pizzaName: String = "",
    val tableNumber: Int = 0,
    val clientName: String = "",
    val status: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Singleton
class KitchenWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val sessionManager: SessionManager
) {
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    private val _events = MutableSharedFlow<KitchenEvent>(extraBufferCapacity = 20)
    val events: SharedFlow<KitchenEvent> = _events.asSharedFlow()

    fun connect() {
        disconnect()
        val token = sessionManager.token
        val request = Request.Builder()
            .url("ws://44.212.148.188:8000/orders/ws/kitchen?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = gson.fromJson(text, JsonObject::class.java)
                val event = json.get("event")?.asString ?: return
                val id = json.get("id")?.asInt ?: return

                val kitchenEvent = KitchenEvent(
                    event = event,
                    id = id,
                    pizzaName = json.get("pizza_name")?.asString ?: "",
                    tableNumber = json.get("table_number")?.asInt ?: 0,
                    clientName = json.get("client_name")?.asString ?: "",
                    status = json.get("status")?.asString ?: "",
                    createdAt = json.get("created_at")?.asString ?: "",
                    updatedAt = json.get("updated_at")?.asString ?: ""
                )
                _events.tryEmit(kitchenEvent)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Connection failed - will be retried on next connect() call
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing")
        webSocket = null
    }
}
