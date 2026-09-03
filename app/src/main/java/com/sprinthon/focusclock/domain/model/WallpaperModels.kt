package com.sprinthon.focusclock.domain.model

import com.sprinthon.focusclock.ui.clock.ClockFont

/**
 * Alignment presets for clock positioning on Home & Lock screen wallpapers.
 */
enum class ClockAlignment(val displayName: String, val xPercent: Float, val yPercent: Float) {
    TOP_START("Top Left", -0.7f, -0.65f),
    TOP_CENTER("Top Center", 0.0f, -0.65f),
    TOP_END("Top Right", 0.7f, -0.65f),
    CENTER_START("Center Left", -0.7f, 0.0f),
    CENTER("Center", 0.0f, 0.0f),
    CENTER_END("Center Right", 0.7f, 0.0f),
    BOTTOM_START("Bottom Left", -0.7f, 0.55f),
    BOTTOM_CENTER("Bottom Center", 0.0f, 0.55f),
    BOTTOM_END("Bottom Right", 0.7f, 0.55f),
    CUSTOM("Custom Position", 0.0f, 0.0f)
}

/**
 * Precise coordinates and scale multiplier for clock placement on wallpaper canvas.
 * @param xPercent Horizontal offset from center [-1.0f..1.0f] where 0.0f is exact center.
 * @param yPercent Vertical offset from center [-1.0f..1.0f] where 0.0f is exact center.
 * @param scale Clock scale multiplier [0.5f..2.0f].
 * @param alignment Current selected preset or custom.
 */
data class WallpaperClockPosition(
    val xPercent: Float = 0.0f,
    val yPercent: Float = -0.35f, // Slightly above center by default (classic lock/home screen placement)
    val scale: Float = 1.0f,
    val alignment: ClockAlignment = ClockAlignment.TOP_CENTER
) {
    companion object {
        val DEFAULT = WallpaperClockPosition(
            xPercent = 0.0f,
            yPercent = -0.35f,
            scale = 1.0f,
            alignment = ClockAlignment.TOP_CENTER
        )
    }
}

/**
 * Numeral orientation on the analog clock dial.
 */
enum class AnalogNumeralOrientation(val displayName: String, val description: String) {
    HORIZONTAL_UPRIGHT(
        displayName = "Horizontal (Upright)",
        description = "Numbers 1–12 remain upright on the vertical axis for effortless reading"
    ),
    RADIAL_ROTATED(
        displayName = "Radial (Curved)",
        description = "Numbers dynamically rotate along the curvature of the dial perimeter"
    ),
    MINIMAL_TICKS(
        displayName = "Minimal Ticks",
        description = "Clean hour tick marks without full numerals"
    ),
    MINIMAL_PIPS(
        displayName = "Minimal Dots",
        description = "Subtle dot hour markers for a modern horological look"
    )
}

/**
 * Wallpaper background style type.
 */
enum class WallpaperBackgroundType(val displayName: String) {
    SOLID_COLOR("Solid Color"),
    GALLERY_IMAGE("Custom Photo")
}

/**
 * Comprehensive configuration model for Home & Lock screen clock wallpaper.
 */
data class WallpaperConfig(
    val clockStyle: ClockStyle = ClockStyle.ANALOG,
    val clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    val clockColorHex: Long = 0xFFFFFFFF,
    val position: WallpaperClockPosition = WallpaperClockPosition.DEFAULT,
    val analogNumeralOrientation: AnalogNumeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
    val analogNumeralSize: AnalogNumeralSize = AnalogNumeralSize.LARGE,
    val analogNumeralScale: Float = 1.35f,
    val backgroundType: WallpaperBackgroundType = WallpaperBackgroundType.SOLID_COLOR,
    val backgroundColorHex: Long = 0xFF000000, // AMOLED black default
    val backgroundImageUri: String? = null,
    val scrimOpacity: Float = 0.25f, // 0.0f to 0.8f dark scrim for contrast
    val blurRadius: Int = 0, // 0 to 25 dp blur for photo backgrounds
    val showDate: Boolean = true,
    val showSeconds: Boolean = true,
    val showMotto: Boolean = false,
    val customMotto: String = "Stay in the Flow",
    val showFocusStreak: Boolean = false,
    val timeFormat24Hour: Boolean = true
)
