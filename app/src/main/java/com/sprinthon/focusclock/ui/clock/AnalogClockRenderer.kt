package com.sprinthon.focusclock.ui.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.ui.theme.FocusAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockRenderer(
    timeData: ClockTimeData,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color(0xFF9E9EA4),
    accentColor: Color = FocusAmber,
    showDate: Boolean = true,
    showDayOfWeek: Boolean = true,
    scale: Float = 1.0f,
    isLandscape: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier.testTag("analog_clock"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        val dialSize = if (isLandscape) {
            val h = availableHeight.value * 0.70f * scale
            val w = availableWidth.value * 0.45f * scale
            minOf(h, w).coerceIn(120f, 260f).dp
        } else {
            val h = availableHeight.value * 0.40f * scale
            val w = availableWidth.value * 0.65f * scale
            minOf(h, w).coerceIn(120f, 280f).dp
        }

        val dateFontSize = (dialSize.value * 0.055f).coerceIn(12f, 18f).sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Canvas(modifier = Modifier.size(dialSize)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - 6.dp.toPx()

                // Outer Dial Ring
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )

                // 12 Hour Ticks
                for (i in 0 until 12) {
                    val angle = (i * 30.0 - 90.0) * (PI / 180.0)
                    val isCardinal = i % 3 == 0
                    val tickLen = if (isCardinal) radius * 0.14f else radius * 0.08f
                    val strokeW = if (isCardinal) 2.5.dp.toPx() else 1.2.dp.toPx()
                    val tickColor = if (isCardinal) primaryColor.copy(alpha = 0.85f) else primaryColor.copy(alpha = 0.35f)

                    val startX = (center.x + (radius - tickLen) * cos(angle)).toFloat()
                    val startY = (center.y + (radius - tickLen) * sin(angle)).toFloat()
                    val endX = (center.x + radius * cos(angle)).toFloat()
                    val endY = (center.y + radius * sin(angle)).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Smooth Hand Angles
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
                drawLine(
                    color = primaryColor,
                    start = center,
                    end = hourEnd,
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Minute Hand
                val minuteLength = radius * 0.75f
                val minuteEnd = Offset(
                    (center.x + minuteLength * cos(minuteAngle)).toFloat(),
                    (center.y + minuteLength * sin(minuteAngle)).toFloat()
                )
                drawLine(
                    color = primaryColor.copy(alpha = 0.90f),
                    start = center,
                    end = minuteEnd,
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Second Hand (Accent Amber)
                val secondLength = radius * 0.85f
                val secondEnd = Offset(
                    (center.x + secondLength * cos(secondAngle)).toFloat(),
                    (center.y + secondLength * sin(secondAngle)).toFloat()
                )
                val secondTail = Offset(
                    (center.x - radius * 0.18f * cos(secondAngle)).toFloat(),
                    (center.y - radius * 0.18f * sin(secondAngle)).toFloat()
                )
                drawLine(
                    color = accentColor,
                    start = secondTail,
                    end = secondEnd,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center Pivot Dot
                drawCircle(
                    color = accentColor,
                    radius = 3.5.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.Black,
                    radius = 1.5.dp.toPx(),
                    center = center
                )
            }

            if (showDate || showDayOfWeek) {
                Spacer(modifier = Modifier.height(14.dp))
                val dateLabel = when {
                    showDate && showDayOfWeek -> timeData.dateString
                    showDayOfWeek -> timeData.dayOfWeek
                    showDate -> timeData.dateString.substringAfter(" · ")
                    else -> ""
                }

                if (dateLabel.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dateLabel,
                            fontSize = dateFontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = secondaryColor,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
