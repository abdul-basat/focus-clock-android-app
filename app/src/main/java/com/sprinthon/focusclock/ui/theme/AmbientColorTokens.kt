package com.sprinthon.focusclock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Phase 4 Milestone 4.3: Ambient Design System Color Tokens.
 * Centralizes all ambient screen color definitions into a @Stable semantic token system
 * with dark/light mode resolution provided via CompositionLocal.
 */
@Stable
data class AmbientColorTokens(
    val ambientAccent: Color,
    val ambientAccentDim: Color,
    val ambientAccentOnDark: Color,
    val ambientSurface: Color,
    val ambientSurfaceVariant: Color,
    val ambientActiveGlow: Color,
    val ambientActiveBg: Color,
    val ambientOnSurface: Color,
    val ambientOnSurfaceMuted: Color,
    val ambientOutline: Color,
    val ambientError: Color,
    val ambientErrorContainer: Color
)

val DarkAmbientColorTokens = AmbientColorTokens(
    ambientAccent = Color(0xFFF59E0B),
    ambientAccentDim = Color(0xFFF59E0B).copy(alpha = 0.15f),
    ambientAccentOnDark = Color(0xFFFCD34D),
    ambientSurface = Color(0xFF131318),
    ambientSurfaceVariant = Color(0xFF1A1A22),
    ambientActiveGlow = Color(0xFFF59E0B).copy(alpha = 0.20f),
    ambientActiveBg = Color(0xFF1A1500).copy(alpha = 0.40f),
    ambientOnSurface = Color(0xFFFFFFFF),
    ambientOnSurfaceMuted = Color(0xFF9E9EA8),
    ambientOutline = Color(0xFF242430),
    ambientError = Color(0xFFE11D48),
    ambientErrorContainer = Color(0xFFE11D48).copy(alpha = 0.15f)
)

val LightAmbientColorTokens = AmbientColorTokens(
    ambientAccent = Color(0xFFD97706),
    ambientAccentDim = Color(0xFFD97706).copy(alpha = 0.15f),
    ambientAccentOnDark = Color(0xFFB45309),
    ambientSurface = Color(0xFFFFFFFF),
    ambientSurfaceVariant = Color(0xFFF4F4F5),
    ambientActiveGlow = Color(0xFFD97706).copy(alpha = 0.25f),
    ambientActiveBg = Color(0xFFFFFBEB),
    ambientOnSurface = Color(0xFF18181B),
    ambientOnSurfaceMuted = Color(0xFF71717A),
    ambientOutline = Color(0xFFE4E4E7),
    ambientError = Color(0xFFDC2626),
    ambientErrorContainer = Color(0xFFFEE2E2)
)

val LocalAmbientColors = staticCompositionLocalOf { DarkAmbientColorTokens }

/**
 * AmbientTheme accessor for easy access in composables:
 * `AmbientTheme.colors.ambientAccent`, `AmbientTheme.colors.ambientSurface`, etc.
 */
object AmbientTheme {
    val colors: AmbientColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAmbientColors.current
}
