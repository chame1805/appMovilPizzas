package com.chame.myapplication.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.chame.myapplication.ui.theme.PizzaTheme

/**
 * Generic Pizza Card component
 * Provides consistent styling across the app
 */
@Composable
fun PizzaCard(
    modifier: Modifier = Modifier,
    elevation: Dp = PizzaTheme.Elevation.card,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        content()
    }
}
