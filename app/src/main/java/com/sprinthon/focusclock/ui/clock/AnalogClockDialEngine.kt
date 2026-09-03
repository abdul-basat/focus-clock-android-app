package com.sprinthon.focusclock.ui.clock

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shared mathematical and drawing engine for rendering Analog clocks
 * across both Jetpack Compose DrawScope and Android Wallpaper Canvas.
 */
object AnalogClockDialEngine {

    /**
     * Draws the complete analog clock dial including outer ring, numerals/ticks,
     * hour/minute/second hands, and center pivot.
     */
    fun drawDial(
        drawScope: DrawScope,
        timeData: ClockTimeData,
        primaryColor: Color,
        secondaryColor: Color,
        accentColor: Color,
        numeralOrientation: AnalogNumeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
        showSeconds: Boolean = true,
        clockFont: ClockFont = ClockFont.BEBAS_NEUE,
        analogNumeralSize: com.sprinthon.focusclock.domain.model.AnalogNumeralSize = com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
        analogNumeralScale: Float = 1.35f
    ) {
        val center = Offset(drawScope.size.width / 2f, drawScope.size.height / 2f)
        val radius = drawScope.size.minDimension / 2f - 6f
        if (radius <= 0f) return

        // 1. Outer Dial Ring
        drawScope.drawCircle(
            color = primaryColor.copy(alpha = 0.18f),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )

        // 2. Draw Numerals or Ticks based on orientation setting
        when (numeralOrientation) {
            AnalogNumeralOrientation.MINIMAL_TICKS -> {
                drawMinimalTicks(drawScope, center, radius, primaryColor)
            }
            AnalogNumeralOrientation.MINIMAL_PIPS -> {
                drawMinimalPips(drawScope, center, radius, primaryColor)
            }
            AnalogNumeralOrientation.HORIZONTAL_UPRIGHT -> {
                drawNumerals(
                    drawScope = drawScope,
                    center = center,
                    radius = radius,
                    primaryColor = primaryColor,
                    isRadialRotated = false,
                    analogNumeralSize = analogNumeralSize,
                    analogNumeralScale = analogNumeralScale
                )
            }
            AnalogNumeralOrientation.RADIAL_ROTATED -> {
                drawNumerals(
                    drawScope = drawScope,
                    center = center,
                    radius = radius,
                    primaryColor = primaryColor,
                    isRadialRotated = true,
                    analogNumeralSize = analogNumeralSize,
                    analogNumeralScale = analogNumeralScale
                )
            }
        }

        // 3. Smooth Hands Calculations
        val hoursFraction = (timeData.hourInt % 12) + (timeData.minuteInt / 60f)
        val minutesFraction = timeData.minuteInt + (timeData.secondInt / 60f)
        val secondsFraction = timeData.secondInt.toFloat()

        val hourAngle = (hoursFraction * 30.0 - 90.0) * (PI / 180.0)
        val minuteAngle = (minutesFraction * 6.0 - 90.0) * (PI / 180.0)
        val secondAngle = (secondsFraction * 6.0 - 90.0) * (PI / 180.0)

        // Hour Hand
        val hourLength = radius * 0.50f
        val hourEnd = Offset(
            (center.x + hourLength * cos(hourAngle)).toFloat(),
            (center.y + hourLength * sin(hourAngle)).toFloat()
        )
        drawScope.drawLine(
            color = primaryColor,
            start = center,
            end = hourEnd,
            strokeWidth = 4.5f,
            cap = StrokeCap.Round
        )

        // Minute Hand
        val minuteLength = radius * 0.72f
        val minuteEnd = Offset(
            (center.x + minuteLength * cos(minuteAngle)).toFloat(),
            (center.y + minuteLength * sin(minuteAngle)).toFloat()
        )
        drawScope.drawLine(
            color = primaryColor.copy(alpha = 0.90f),
            start = center,
            end = minuteEnd,
            strokeWidth = 2.8f,
            cap = StrokeCap.Round
        )

        // Second Hand (Accent color)
        if (showSeconds) {
            val secondLength = radius * 0.85f
            val secondEnd = Offset(
                (center.x + secondLength * cos(secondAngle)).toFloat(),
                (center.y + secondLength * sin(secondAngle)).toFloat()
            )
            val secondTail = Offset(
                (center.x - radius * 0.18f * cos(secondAngle)).toFloat(),
                (center.y - radius * 0.18f * sin(secondAngle)).toFloat()
            )
            drawScope.drawLine(
                color = accentColor,
                start = secondTail,
                end = secondEnd,
                strokeWidth = 1.6f,
                cap = StrokeCap.Round
            )
        }

        // Center Pivot Dots
        drawScope.drawCircle(
            color = if (showSeconds) accentColor else primaryColor,
            radius = 4.0f,
            center = center
        )
        drawScope.drawCircle(
            color = Color.Black,
            radius = 1.8f,
            center = center
        )
    }

    private fun drawMinimalTicks(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        primaryColor: Color
    ) {
        for (i in 0 until 12) {
            val angle = (i * 30.0 - 90.0) * (PI / 180.0)
            val isCardinal = i % 3 == 0
            val tickLen = if (isCardinal) radius * 0.14f else radius * 0.08f
            val strokeW = if (isCardinal) 2.5f else 1.2f
            val tickColor = if (isCardinal) primaryColor.copy(alpha = 0.85f) else primaryColor.copy(alpha = 0.35f)

            val startX = (center.x + (radius - tickLen) * cos(angle)).toFloat()
            val startY = (center.y + (radius - tickLen) * sin(angle)).toFloat()
            val endX = (center.x + radius * cos(angle)).toFloat()
            val endY = (center.y + radius * sin(angle)).toFloat()

            drawScope.drawLine(
                color = tickColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawMinimalPips(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        primaryColor: Color
    ) {
        val pipDistance = radius * 0.88f
        for (i in 0 until 12) {
            val angle = (i * 30.0 - 90.0) * (PI / 180.0)
            val isCardinal = i % 3 == 0
            val pipRadius = if (isCardinal) 3.5f else 2.0f
            val pipColor = if (isCardinal) primaryColor.copy(alpha = 0.90f) else primaryColor.copy(alpha = 0.40f)

            val x = (center.x + pipDistance * cos(angle)).toFloat()
            val y = (center.y + pipDistance * sin(angle)).toFloat()

            drawScope.drawCircle(
                color = pipColor,
                radius = pipRadius,
                center = Offset(x, y)
            )
        }
    }

    private fun drawNumerals(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        primaryColor: Color,
        isRadialRotated: Boolean,
        analogNumeralSize: com.sprinthon.focusclock.domain.model.AnalogNumeralSize = com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
        analogNumeralScale: Float = 1.35f
    ) {
        // Draw small subtle tick marks on the rim - scaled gracefully so they don't collide with large digits
        val isJumbo = analogNumeralScale >= 1.50f || analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.JUMBO
        val tickLen = if (isJumbo) radius * 0.04f else radius * 0.06f
        for (i in 0 until 12) {
            val angle = (i * 30.0 - 90.0) * (PI / 180.0)
            val startX = (center.x + (radius - tickLen) * cos(angle)).toFloat()
            val startY = (center.y + (radius - tickLen) * sin(angle)).toFloat()
            val endX = (center.x + radius * cos(angle)).toFloat()
            val endY = (center.y + radius * sin(angle)).toFloat()

            drawScope.drawLine(
                color = primaryColor.copy(alpha = if (isJumbo) 0.18f else 0.25f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1.0f,
                cap = StrokeCap.Round
            )
        }

        // Adjust numeral radial distance dynamically so large digits do not clip the outer rim or overlap hands
        val effectiveScale = analogNumeralScale.coerceIn(0.80f, 2.0f)
        val numeralDistance = radius * (0.80f - (effectiveScale - 1.0f) * 0.05f).coerceIn(0.68f, 0.82f)

        // Dynamic base text size without artificial ceiling
        val baseNumeralSize = (radius * 0.20f * effectiveScale).coerceIn(14f, 96f)

        val textPaint = Paint().apply {
            color = primaryColor.toArgb()
            textSize = baseNumeralSize
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val cardinalPaint = if (analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL) {
            Paint().apply {
                color = primaryColor.toArgb()
                textSize = (radius * 0.26f * effectiveScale).coerceIn(16f, 108f)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
        } else textPaint

        val nonCardinalPaint = if (analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL) {
            Paint().apply {
                color = primaryColor.copy(alpha = 0.45f).toArgb()
                textSize = (radius * 0.14f * effectiveScale).coerceIn(11f, 60f)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }
        } else textPaint

        drawScope.drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            val bounds = Rect()

            for (hour in 1..12) {
                val isCardinal = (hour % 3 == 0) // 3, 6, 9, 12
                val currentPaint = when {
                    analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL && isCardinal -> cardinalPaint
                    analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL && !isCardinal -> nonCardinalPaint
                    else -> textPaint
                }

                val hourText = hour.toString()
                currentPaint.getTextBounds(hourText, 0, hourText.length, bounds)
                val textHeightOffset = bounds.height() / 2f

                // Hour 12 is at -90 degrees, Hour 1 is at -60 deg, ..., Hour 3 is at 0 deg
                val angleDeg = (hour * 30.0 - 90.0)
                val angleRad = angleDeg * (PI / 180.0)

                val posX = (center.x + numeralDistance * cos(angleRad)).toFloat()
                val posY = (center.y + numeralDistance * sin(angleRad)).toFloat()

                if (isRadialRotated) {
                    // Radial Mode: Rotate text along the tangent
                    nativeCanvas.save()
                    nativeCanvas.translate(posX, posY)
                    nativeCanvas.rotate((angleDeg + 90.0).toFloat())
                    nativeCanvas.drawText(hourText, 0f, textHeightOffset, currentPaint)
                    nativeCanvas.restore()
                } else {
                    // Horizontal / Upright Mode: Keep numbers vertical and upright on screen
                    nativeCanvas.drawText(hourText, posX, posY + textHeightOffset, currentPaint)
                }
            }
        }
    }
}
