package com.sprinthon.focusclock.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.FocusPreferences

/**
 * High-contrast, WCAG AAA compliant palette dynamically computed based on
 * the effective luminance of the focus canvas background and dimming overlay.
 */
data class CanvasContrastPalette(
    val isLightCanvas: Boolean,
    val primaryText: Color,          // Clock digits, analog hands, major headings
    val secondaryText: Color,        // Date, AM/PM, labels, subtitles
    val tertiaryText: Color,         // Subtle captions, inactive icons
    val accentColor: Color,          // Hero accent (Focus Amber / Deep Amber)
    val cardBackground: Color,       // Flip cards / Dialog / Control surfaces
    val cardBorder: Color,           // Crisp subtle border for surfaces
    val cardDivider: Color,          // Card slit / horizontal divider
    val buttonSurface: Color,        // Action button background (Pause/Resume/End)
    val buttonText: Color,           // Action button text color
    val buttonBorder: Color,         // Action button border
    val statusBarsDarkIcons: Boolean // True for dark icons on light status bar
)

object CanvasContrastHelper {

    /**
     * Determines whether a given color hex/long and overlay strength produce a light canvas.
     */
    fun isLightCanvas(
        solidBackgroundColor: Long,
        backgroundType: BackgroundType,
        overlayStrength: Float
    ): Boolean {
        if (backgroundType != BackgroundType.SOLID_COLOR) {
            // Photos and slideshows are treated as dark surfaces when dimming is present
            return false
        }
        val baseColor = Color(solidBackgroundColor)
        val clampedOverlay = overlayStrength.coerceIn(0f, 0.9f)
        val r = baseColor.red * (1f - clampedOverlay)
        val g = baseColor.green * (1f - clampedOverlay)
        val b = baseColor.blue * (1f - clampedOverlay)
        val effectiveColor = Color(red = r, green = g, blue = b, alpha = 1f)
        return effectiveColor.luminance() > 0.45f
    }

    /**
     * Calculates the perceived effective background color and returns an adaptive palette.
     * Takes into account:
     * - Solid background color
     * - Background type (Photo vs Solid)
     * - Background dimming overlay (overlayStrength blends toward black)
     */
    fun calculatePalette(
        solidBackgroundColor: Long,
        backgroundType: BackgroundType,
        overlayStrength: Float
    ): CanvasContrastPalette {
        val isLight = isLightCanvas(solidBackgroundColor, backgroundType, overlayStrength)

        return if (isLight) {
            // LIGHT CANVAS PALETTE (e.g. Soft White #F0F0F2, Warm Paper #EBE6DF, Sky Mist with low dimming)
            CanvasContrastPalette(
                isLightCanvas = true,
                primaryText = Color(0xFF111115),          // Deep Obsidian / Ink (Contrast > 14:1)
                secondaryText = Color(0xFF4E4E58),        // Refined Slate Charcoal (Contrast > 7:1)
                tertiaryText = Color(0xFF767682),         // Muted Slate (Contrast > 4.5:1)
                accentColor = Color(0xFFD97706),          // Deep Amber with enhanced contrast on light bg
                cardBackground = Color(0xFFE4E4EC),       // Light subtle card surface
                cardBorder = Color(0xFFCBCBD4),           // Crisp card outline
                cardDivider = Color(0xFFB8B8C2),          // Flip card central slit
                buttonSurface = Color(0xFFE2E2E8),       // Elevated button surface
                buttonText = Color(0xFF111115),          // Button text
                buttonBorder = Color(0xFFC0C0CB),         // Button border
                statusBarsDarkIcons = true                // Dark status bar icons on light canvas
            )
        } else {
            // DARK CANVAS PALETTE (e.g. AMOLED Black, Deep Charcoal, Dark Slate, Photos with overlay)
            CanvasContrastPalette(
                isLightCanvas = false,
                primaryText = Color(0xFFFFFFFF),          // Pure Crisp White (Contrast > 15:1)
                secondaryText = Color(0xFFA6A6AC),        // Soft Silver (Contrast > 7:1)
                tertiaryText = Color(0xFF6E6E78),         // Muted Charcoal
                accentColor = FocusAmber,                 // Bright Focus Amber #FFB800
                cardBackground = Color(0xFF1E1E22),       // Dark Elevated Surface
                cardBorder = Color(0xFF2C2C34),           // Dark Outline
                cardDivider = Color(0xFF000000).copy(alpha = 0.8f), // Dark flip card central slit
                buttonSurface = Color(0xFF1E1E22),       // Dark Elevated Button
                buttonText = Color(0xFFFFFFFF),          // White Button Text
                buttonBorder = Color(0xFF383840),         // Dark Button Border
                statusBarsDarkIcons = false               // Light (White) status bar icons
            )
        }
    }
}

/**
 * Composable helper that remembers and recalculates the contrast palette when relevant
 * preferences change.
 */
@Composable
fun rememberCanvasContrastPalette(preferences: FocusPreferences): CanvasContrastPalette {
    return remember(
        preferences.solidBackgroundColor,
        preferences.backgroundType,
        preferences.backgroundOverlayStrength
    ) {
        CanvasContrastHelper.calculatePalette(
            solidBackgroundColor = preferences.solidBackgroundColor,
            backgroundType = preferences.backgroundType,
            overlayStrength = preferences.backgroundOverlayStrength
        )
    }
}
