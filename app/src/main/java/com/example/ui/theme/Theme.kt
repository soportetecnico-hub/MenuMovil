package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NovaDarkColorScheme = darkColorScheme(
  primary = NovaRedBright,
  onPrimary = NovaTextPrimary,
  primaryContainer = NovaBadgeBg,
  onPrimaryContainer = NovaBadgeText,
  secondary = NovaFlameOrange,
  onSecondary = NovaDarkBg,
  background = NovaDarkBg,
  onBackground = NovaTextPrimary,
  surface = NovaCardBg,
  onSurface = NovaTextPrimary,
  surfaceVariant = NovaCardBgElevated,
  onSurfaceVariant = NovaTextSecondary,
  outline = NovaCardBorder,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme to match the reference app style
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = NovaDarkColorScheme,
    typography = Typography,
    content = content
  )
}

