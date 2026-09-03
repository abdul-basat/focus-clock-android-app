package com.sprinthon.focusclock.playback

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.WallpaperBackgroundType
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.ui.clock.ClockTimeData
import java.io.InputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object WallpaperBitmapRenderer {

    fun applyWallpaper(context: Context, bitmap: Bitmap, destinationFlags: Int): Boolean {
        return try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, destinationFlags)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun renderWallpaperBitmap(
        context: Context,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        width: Int,
        height: Int
    ): Bitmap {
        val safeW = if (width <= 0) 1080 else width
        val safeH = if (height <= 0) 1920 else height
        val bitmap = Bitmap.createBitmap(safeW, safeH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawWallpaperToCanvas(context, canvas, config, timeData, safeW, safeH)
        return bitmap
    }

    fun drawWallpaperToCanvas(
        context: Context,
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        width: Int,
        height: Int
    ) {
        val w = width.toFloat()
        val h = height.toFloat()
        val minDim = min(w, h)

        // 1. Render Background
        if (config.backgroundType == WallpaperBackgroundType.GALLERY_IMAGE && !config.backgroundImageUri.isNullOrEmpty()) {
            var bgBitmap: Bitmap? = null
            try {
                val uri = Uri.parse(config.backgroundImageUri)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val decoded = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    if (decoded != null) {
                        bgBitmap = scaleCenterCrop(decoded, width, height)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap, 0f, 0f, null)
            } else {
                canvas.drawColor(config.backgroundColorHex.toInt())
            }
        } else {
            canvas.drawColor(config.backgroundColorHex.toInt())
        }

        // 2. Scrim Overlay
        if (config.scrimOpacity > 0f) {
            val scrimPaint = Paint().apply {
                color = AndroidColor.BLACK
                alpha = (config.scrimOpacity * 255f).toInt().coerceIn(0, 255)
            }
            canvas.drawRect(0f, 0f, w, h, scrimPaint)
        }

        // 3. Draggable Clock Position Center Calculation
        val centerX = (w / 2f) + (config.position.xPercent * (w / 2f))
        val centerY = (h / 2f) + (config.position.yPercent * (h / 2f))

        canvas.save()
        canvas.translate(centerX, centerY)

        val scale = config.position.scale.coerceIn(0.4f, 2.5f)
        val clockColor = config.clockColorHex.toInt()

        when (config.clockStyle) {
            ClockStyle.ANALOG -> {
                drawAnalogClock(canvas, config, timeData, scale, clockColor, minDim)
            }
            ClockStyle.FLIP_CLOCK -> {
                drawFlipClock(canvas, config, timeData, scale, clockColor, minDim)
            }
            ClockStyle.MINIMAL_DIGITAL -> {
                drawMinimalClock(canvas, config, timeData, scale, clockColor, minDim)
            }
            ClockStyle.CLEAN_DIGITAL -> {
                drawCleanDigitalClock(canvas, config, timeData, scale, clockColor, minDim)
            }
        }

        canvas.restore()
    }

    private fun drawCleanDigitalClock(
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        scale: Float,
        clockColor: Int,
        minDim: Float
    ) {
        val baseFontSize = minDim * 0.28f * scale
        val digitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            textSize = baseFontSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        }

        val hourY = -baseFontSize * 0.08f
        val minuteY = baseFontSize * 0.74f

        // Draw Hours
        canvas.drawText(timeData.hourString, 0f, hourY, digitPaint)

        // Draw Minutes
        canvas.drawText(timeData.minuteString, 0f, minuteY, digitPaint)

        // Draw Secondary details (Date, Motto, Streak)
        var nextY = minuteY + baseFontSize * 0.28f
        drawSecondaryInfo(canvas, config, timeData, nextY, scale, minDim, clockColor)
    }

    private fun drawMinimalClock(
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        scale: Float,
        clockColor: Int,
        minDim: Float
    ) {
        val baseFontSize = minDim * 0.26f * scale
        val digitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            textSize = baseFontSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        }

        val hourY = -baseFontSize * 0.06f
        val minuteY = baseFontSize * 0.74f

        // Draw Hours
        canvas.drawText(timeData.hourString, 0f, hourY, digitPaint)

        // Draw Minutes
        canvas.drawText(timeData.minuteString, 0f, minuteY, digitPaint)

        // Draw Secondary details
        val nextY = minuteY + baseFontSize * 0.28f
        drawSecondaryInfo(canvas, config, timeData, nextY, scale, minDim, clockColor)
    }

    private fun drawFlipClock(
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        scale: Float,
        clockColor: Int,
        minDim: Float
    ) {
        val cardWidth = minDim * 0.52f * scale
        val cardHeight = cardWidth * 0.68f
        val fontSize = cardHeight * 0.72f
        val cornerRadius = 24f * scale
        val gap = 12f * scale

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(70, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 2f * scale
        }
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(180, 0, 0, 0)
            style = Paint.Style.STROKE
            strokeWidth = 3f * scale
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            textSize = fontSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        }

        // 1. Hour Card
        val hourRect = RectF(
            -cardWidth / 2f,
            -cardHeight - (gap / 2f),
            cardWidth / 2f,
            -(gap / 2f)
        )
        cardPaint.shader = LinearGradient(
            0f, hourRect.top, 0f, hourRect.bottom,
            AndroidColor.argb(230, 32, 32, 36),
            AndroidColor.argb(230, 16, 16, 18),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(hourRect, cornerRadius, cornerRadius, cardPaint)
        canvas.drawRoundRect(hourRect, cornerRadius, cornerRadius, borderPaint)
        canvas.drawLine(hourRect.left, hourRect.centerY(), hourRect.right, hourRect.centerY(), dividerPaint)

        val textBounds = Rect()
        textPaint.getTextBounds(timeData.hourString, 0, timeData.hourString.length, textBounds)
        canvas.drawText(
            timeData.hourString,
            0f,
            hourRect.centerY() + (textBounds.height() / 2f),
            textPaint
        )

        // 2. Minute Card
        val minRect = RectF(
            -cardWidth / 2f,
            (gap / 2f),
            cardWidth / 2f,
            cardHeight + (gap / 2f)
        )
        cardPaint.shader = LinearGradient(
            0f, minRect.top, 0f, minRect.bottom,
            AndroidColor.argb(230, 32, 32, 36),
            AndroidColor.argb(230, 16, 16, 18),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(minRect, cornerRadius, cornerRadius, cardPaint)
        canvas.drawRoundRect(minRect, cornerRadius, cornerRadius, borderPaint)
        canvas.drawLine(minRect.left, minRect.centerY(), minRect.right, minRect.centerY(), dividerPaint)

        textPaint.getTextBounds(timeData.minuteString, 0, timeData.minuteString.length, textBounds)
        canvas.drawText(
            timeData.minuteString,
            0f,
            minRect.centerY() + (textBounds.height() / 2f),
            textPaint
        )

        // Draw Secondary details
        val nextY = minRect.bottom + minDim * 0.05f * scale
        drawSecondaryInfo(canvas, config, timeData, nextY, scale, minDim, clockColor)
    }

    private fun drawAnalogClock(
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        scale: Float,
        clockColor: Int,
        minDim: Float
    ) {
        val radius = minDim * 0.32f * scale
        val dialPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            style = Paint.Style.STROKE
            strokeWidth = 3.5f * scale
            alpha = 180
        }
        val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            strokeCap = Paint.Cap.ROUND
        }

        // 1. Dial outer circle
        canvas.drawCircle(0f, 0f, radius, dialPaint)

        // 2. Numerals / Ticks based on orientation
        when (config.analogNumeralOrientation) {
            AnalogNumeralOrientation.MINIMAL_TICKS -> {
                drawMinimalTicks(canvas, radius, scale, clockColor)
            }
            AnalogNumeralOrientation.MINIMAL_PIPS -> {
                drawMinimalPips(canvas, radius, scale, clockColor)
            }
            AnalogNumeralOrientation.HORIZONTAL_UPRIGHT -> {
                drawAnalogNumerals(
                    canvas = canvas,
                    radius = radius,
                    scale = scale,
                    clockColor = clockColor,
                    isRadialRotated = false,
                    analogNumeralSize = config.analogNumeralSize,
                    numeralScale = config.analogNumeralScale
                )
            }
            AnalogNumeralOrientation.RADIAL_ROTATED -> {
                drawAnalogNumerals(
                    canvas = canvas,
                    radius = radius,
                    scale = scale,
                    clockColor = clockColor,
                    isRadialRotated = true,
                    analogNumeralSize = config.analogNumeralSize,
                    numeralScale = config.analogNumeralScale
                )
            }
        }

        // 3. Hands Calculations
        val hoursFraction = (timeData.hourInt % 12) + (timeData.minuteInt / 60.0)
        val minutesFraction = timeData.minuteInt + (timeData.secondInt / 60.0)
        val secondsFraction = timeData.secondInt.toDouble()

        val hourAngle = (hoursFraction * 30.0 - 90.0) * (PI / 180.0)
        val minuteAngle = (minutesFraction * 6.0 - 90.0) * (PI / 180.0)
        val secondAngle = (secondsFraction * 6.0 - 90.0) * (PI / 180.0)

        // Hour Hand
        val hourLen = radius * 0.50f
        val hourX = (hourLen * cos(hourAngle)).toFloat()
        val hourY = (hourLen * sin(hourAngle)).toFloat()
        handPaint.strokeWidth = 8.5f * scale
        handPaint.color = clockColor
        canvas.drawLine(0f, 0f, hourX, hourY, handPaint)

        // Minute Hand
        val minLen = radius * 0.72f
        val minX = (minLen * cos(minuteAngle)).toFloat()
        val minY = (minLen * sin(minuteAngle)).toFloat()
        handPaint.strokeWidth = 5.0f * scale
        handPaint.color = clockColor
        canvas.drawLine(0f, 0f, minX, minY, handPaint)

        // Second Hand (Accent color)
        if (config.showSeconds) {
            val secLen = radius * 0.86f
            val secX = (secLen * cos(secondAngle)).toFloat()
            val secY = (secLen * sin(secondAngle)).toFloat()
            val tailLen = radius * 0.18f
            val tailX = (-tailLen * cos(secondAngle)).toFloat()
            val tailY = (-tailLen * sin(secondAngle)).toFloat()

            handPaint.strokeWidth = 2.8f * scale
            handPaint.color = AndroidColor.parseColor("#FFB703")
            canvas.drawLine(tailX, tailY, secX, secY, handPaint)
        }

        // Center Pivot
        val pivotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (config.showSeconds) AndroidColor.parseColor("#FFB703") else clockColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(0f, 0f, 7.5f * scale, pivotPaint)
        val innerPivotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.BLACK
            style = Paint.Style.FILL
        }
        canvas.drawCircle(0f, 0f, 3.2f * scale, innerPivotPaint)

        // Draw Secondary details below the dial
        val nextY = radius + minDim * 0.05f * scale
        drawSecondaryInfo(canvas, config, timeData, nextY, scale, minDim, clockColor)
    }

    private fun drawMinimalTicks(canvas: Canvas, radius: Float, scale: Float, clockColor: Int) {
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            strokeCap = Paint.Cap.ROUND
        }
        for (i in 0 until 12) {
            val angle = (i * 30.0 - 90.0) * (PI / 180.0)
            val isCardinal = i % 3 == 0
            val tickLen = if (isCardinal) radius * 0.14f else radius * 0.08f
            tickPaint.strokeWidth = if (isCardinal) 4.5f * scale else 2.2f * scale
            tickPaint.alpha = if (isCardinal) 220 else 90

            val startX = ((radius - tickLen) * cos(angle)).toFloat()
            val startY = ((radius - tickLen) * sin(angle)).toFloat()
            val endX = (radius * cos(angle)).toFloat()
            val endY = (radius * sin(angle)).toFloat()

            canvas.drawLine(startX, startY, endX, endY, tickPaint)
        }
    }

    private fun drawMinimalPips(canvas: Canvas, radius: Float, scale: Float, clockColor: Int) {
        val pipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            style = Paint.Style.FILL
        }
        val pipDistance = radius * 0.88f
        for (i in 0 until 12) {
            val angle = (i * 30.0 - 90.0) * (PI / 180.0)
            val isCardinal = i % 3 == 0
            val pipRadius = if (isCardinal) 6.0f * scale else 3.5f * scale
            pipPaint.alpha = if (isCardinal) 230 else 100

            val x = (pipDistance * cos(angle)).toFloat()
            val y = (pipDistance * sin(angle)).toFloat()

            canvas.drawCircle(x, y, pipRadius, pipPaint)
        }
    }

    private fun drawAnalogNumerals(
        canvas: Canvas,
        radius: Float,
        scale: Float,
        clockColor: Int,
        isRadialRotated: Boolean,
        analogNumeralSize: com.sprinthon.focusclock.domain.model.AnalogNumeralSize = com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
        numeralScale: Float = 1.35f
    ) {
        val effectiveScale = numeralScale.coerceIn(0.80f, 2.0f)
        val numeralDistance = radius * (0.80f - (effectiveScale - 1.0f) * 0.05f).coerceIn(0.68f, 0.82f)

        val baseFontSize = (radius * 0.20f * effectiveScale).coerceIn(18f * scale, 96f * scale)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = clockColor
            textSize = baseFontSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val cardinalPaint = if (analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = clockColor
                textSize = (radius * 0.26f * effectiveScale).coerceIn(22f * scale, 108f * scale)
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
        } else textPaint

        val nonCardinalPaint = if (analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = clockColor
                alpha = (0.45f * 255).toInt()
                textSize = (radius * 0.14f * effectiveScale).coerceIn(14f * scale, 60f * scale)
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }
        } else textPaint

        val bounds = Rect()
        for (hour in 1..12) {
            val isCardinal = (hour % 3 == 0)
            val currentPaint = when {
                analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL && isCardinal -> cardinalPaint
                analogNumeralSize == com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL && !isCardinal -> nonCardinalPaint
                else -> textPaint
            }

            val hourText = hour.toString()
            currentPaint.getTextBounds(hourText, 0, hourText.length, bounds)
            val textHeightOffset = bounds.height() / 2f

            val angleDeg = (hour * 30.0 - 90.0)
            val angleRad = angleDeg * (PI / 180.0)

            val posX = (numeralDistance * cos(angleRad)).toFloat()
            val posY = (numeralDistance * sin(angleRad)).toFloat()

            if (isRadialRotated) {
                canvas.save()
                canvas.translate(posX, posY)
                canvas.rotate((angleDeg + 90.0).toFloat())
                canvas.drawText(hourText, 0f, textHeightOffset, currentPaint)
                canvas.restore()
            } else {
                canvas.drawText(hourText, posX, posY + textHeightOffset, currentPaint)
            }
        }
    }

    private fun drawSecondaryInfo(
        canvas: Canvas,
        config: WallpaperConfig,
        timeData: ClockTimeData,
        startY: Float,
        scale: Float,
        minDim: Float,
        clockColor: Int
    ) {
        var currentY = startY

        // 1. Date
        if (config.showDate) {
            val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = clockColor
                alpha = 220
                textSize = minDim * 0.040f * scale
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }
            val dateLabel = "${timeData.dayOfWeek}, ${timeData.dateString}" +
                if (!config.timeFormat24Hour) " · ${timeData.amPm}" else ""
            canvas.drawText(dateLabel, 0f, currentY, datePaint)
            currentY += minDim * 0.055f * scale
        }

        // 2. Custom Motto
        if (config.showMotto && config.customMotto.isNotBlank()) {
            val mottoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = clockColor
                alpha = 190
                textSize = minDim * 0.034f * scale
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            }
            canvas.drawText(config.customMotto, 0f, currentY, mottoPaint)
            currentY += minDim * 0.055f * scale
        }

        // 3. Focus Streak Badge
        if (config.showFocusStreak) {
            val streakBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.argb(190, 20, 20, 24)
                style = Paint.Style.FILL
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.parseColor("#FFB703")
                style = Paint.Style.STROKE
                strokeWidth = 2.5f * scale
            }
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.parseColor("#FFB703")
                textSize = minDim * 0.032f * scale
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            val streakText = "🔥 5 Day Focus Streak"
            val textWidth = textPaint.measureText(streakText)
            val paddingH = 28f * scale
            val paddingV = 14f * scale
            val badgeHeight = textPaint.textSize + (paddingV * 2)
            val rect = RectF(
                -textWidth / 2f - paddingH,
                currentY,
                textWidth / 2f + paddingH,
                currentY + badgeHeight
            )
            canvas.drawRoundRect(rect, 24f * scale, 24f * scale, streakBgPaint)
            canvas.drawRoundRect(rect, 24f * scale, 24f * scale, borderPaint)
            canvas.drawText(
                streakText,
                0f,
                currentY + paddingV + (textPaint.textSize * 0.85f),
                textPaint
            )
        }
    }

    private fun scaleCenterCrop(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val sourceWidth = source.width
        val sourceHeight = source.height

        val xScale = targetWidth.toFloat() / sourceWidth
        val yScale = targetHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left = (targetWidth - scaledWidth) / 2f
        val top = (targetHeight - scaledHeight) / 2f

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val dest = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
    }
}
