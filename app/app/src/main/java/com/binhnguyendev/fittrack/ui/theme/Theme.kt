package com.binhnguyendev.fittrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/**
 * Dark-only theme. Material3 components are barely used (the UI is hand-built
 * from the prototype), but a sane dark colour scheme is provided so any default
 * component surfaces / ripples read correctly on the #0F0F11 background.
 */
private val FitTrackColors = darkColorScheme(
    primary = FT.orange,
    onPrimary = FT.text,
    secondary = FT.blue,
    onSecondary = FT.text,
    background = FT.bg,
    onBackground = FT.text,
    surface = FT.surface,
    onSurface = FT.text,
    surfaceVariant = FT.raised,
    onSurfaceVariant = FT.text2,
    outline = FT.border,
    error = FT.destructive,
    onError = FT.text,
)

@Composable
fun FitTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FitTrackColors,
        // Force the body font everywhere so stray defaults still render Tiempos.
        typography = Typography().run {
            val f = AppFonts.text
            copy(
                displayLarge = displayLarge.withFamily(f),
                displayMedium = displayMedium.withFamily(f),
                displaySmall = displaySmall.withFamily(f),
                headlineLarge = headlineLarge.withFamily(f),
                headlineMedium = headlineMedium.withFamily(f),
                headlineSmall = headlineSmall.withFamily(f),
                titleLarge = titleLarge.withFamily(f),
                titleMedium = titleMedium.withFamily(f),
                titleSmall = titleSmall.withFamily(f),
                bodyLarge = bodyLarge.withFamily(f),
                bodyMedium = bodyMedium.withFamily(f),
                bodySmall = bodySmall.withFamily(f),
                labelLarge = labelLarge.withFamily(f),
                labelMedium = labelMedium.withFamily(f),
                labelSmall = labelSmall.withFamily(f),
            )
        },
        content = content,
    )
}

private fun TextStyle.withFamily(family: androidx.compose.ui.text.font.FontFamily) =
    copy(fontFamily = family)
