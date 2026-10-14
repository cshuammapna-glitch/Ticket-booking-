package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkNavyPrimary,
    onPrimary = DarkNavyOnPrimary,
    primaryContainer = DarkNavyContainer,
    onPrimaryContainer = DarkNavyOnContainer,
    secondary = DarkAmberSecondary,
    onSecondary = DarkAmberOnSecondary,
    secondaryContainer = DarkAmberContainer,
    onSecondaryContainer = DarkAmberOnContainer,
    tertiary = DarkGreenTertiary,
    onTertiary = DarkGreenOnTertiary,
    tertiaryContainer = DarkGreenContainer,
    onTertiaryContainer = DarkGreenOnContainer,
    background = DarkSlateBackground,
    onBackground = DarkSlateOnBackground,
    surface = DarkSlateSurface,
    onSurface = DarkSlateOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutlineColor
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = NavyContainer,
    onPrimaryContainer = NavyOnContainer,
    secondary = AmberSecondary,
    onSecondary = AmberOnSecondary,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = AmberOnContainer,
    tertiary = GreenTertiary,
    onTertiary = GreenOnTertiary,
    tertiaryContainer = GreenContainer,
    onTertiaryContainer = GreenOnContainer,
    background = SlateBackground,
    onBackground = SlateOnBackground,
    surface = PureWhiteSurface,
    onSurface = SlateOnSurface,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = OnSurfaceVariantColor,
    outline = OutlineColor
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors for cohesive Indian Railway styling
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
