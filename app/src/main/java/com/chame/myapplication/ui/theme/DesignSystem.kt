package com.chame.myapplication.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design System constants for Pizzería Theme
 * Centralizes spacing, corners, elevations, and font sizes
 */
object PizzaTheme {

    /**
     * Spacing scale used throughout the app
     */
    object Spacing {
        val xs = 4.dp      // Extra small - gaps between elements
        val sm = 8.dp      // Small - internal padding for small components
        val md = 16.dp     // Medium - standard padding
        val lg = 24.dp     // Large - section spacing
        val xl = 32.dp     // Extra large - container spacing
        val xxl = 48.dp    // 2XL - major section spacing
    }

    /**
     * Corner radius for different component types
     */
    object Corners {
        val small = 8.dp        // Small components, buttons
        val medium = 12.dp      // Medium components
        val large = 20.dp       // Cards, dialogs
        val extraLarge = 30.dp  // Large buttons, distinctive elements
    }

    /**
     * Elevation (shadow) values for Material Design
     */
    object Elevation {
        val card = 4.dp
        val dialog = 24.dp
        val fab = 6.dp
        val bottomSheet = 16.dp
        val modal = 32.dp
    }

    /**
     * Font sizes for text elements
     */
    object FontSizes {
        val caption = 12.sp    // Small labels, hints
        val body = 14.sp       // Body text, descriptions
        val bodyLarge = 16.sp  // Large body text
        val subtitle = 16.sp   // Subtitles
        val title = 20.sp      // Small titles
        val heading = 28.sp    // Headings
    }
}
