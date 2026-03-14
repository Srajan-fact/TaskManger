package com.example.taskmanger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// ── Neon Dark Color Scheme (SOP v1.0) ──────────────────────────
val NeonDarkColorScheme = darkColorScheme(
    background   = Background,
    surface      = SurfaceColor,
    primary      = Accent,
    onPrimary    = TextOnAccent,
    onBackground = TextPrimary,
    onSurface    = TextPrimary,
    outline      = BorderColor,
    error        = ColorNegative,
    secondary    = TextSecondary,
    onSecondary  = TextSecondary,
)

@Composable
fun TaskMangerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeonDarkColorScheme,
        typography  = NeonTypography,
        content     = content,
    )
}