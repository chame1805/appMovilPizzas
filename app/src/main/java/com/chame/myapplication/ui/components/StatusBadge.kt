package com.chame.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chame.myapplication.ui.theme.PizzaTheme

enum class OrderStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completado"),
    DELIVERED("Entregado")
}

/**
 * Status Badge component for order statuses
 * Shows color and status text
 */
@Composable
fun StatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFF9A825)  // Warning yellow
        OrderStatus.IN_PROGRESS -> Color(0xFFE3F2FD) to Color(0xFF1565C0)  // Info blue
        OrderStatus.COMPLETED -> Color(0xFFF1F8E9) to Color(0xFF4CAF50)  // Success green
        OrderStatus.DELIVERED -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)  // Purple
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(PizzaTheme.Corners.small)
            )
            .padding(
                horizontal = PizzaTheme.Spacing.md,
                vertical = PizzaTheme.Spacing.xs
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
