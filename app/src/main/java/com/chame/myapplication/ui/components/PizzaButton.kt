package com.chame.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chame.myapplication.ui.theme.PizzaTheme

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    DANGER
}

/**
 * Themed Pizza Button component
 * Supports different variants and loading states
 */
@Composable
fun PizzaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.PRIMARY
) {
    val containerColor = when (variant) {
        ButtonVariant.PRIMARY -> MaterialTheme.colorScheme.primary
        ButtonVariant.SECONDARY -> MaterialTheme.colorScheme.secondary
        ButtonVariant.DANGER -> MaterialTheme.colorScheme.error
    }

    val contentColor = when (variant) {
        ButtonVariant.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        ButtonVariant.SECONDARY -> MaterialTheme.colorScheme.onSecondary
        ButtonVariant.DANGER -> MaterialTheme.colorScheme.onError
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = PizzaTheme.Spacing.sm),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = PizzaTheme.Spacing.sm)
                    .align(Alignment.CenterVertically),
                color = contentColor,
                strokeWidth = 2.dp
            )
        }
        Text(
            text = if (isLoading) "Cargando..." else text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
