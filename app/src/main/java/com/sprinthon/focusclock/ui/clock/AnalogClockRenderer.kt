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
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.ui.theme.FocusAmber

@Composable
fun AnalogClockRenderer(
    timeData: ClockTimeData,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color(0xFF9E9EA4),
    accentColor: Color = FocusAmber,
    numeralOrientation: AnalogNumeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
    showSeconds: Boolean = true,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    showDate: Boolean = true,
    showDayOfWeek: Boolean = true,
    scale: Float = 1.0f,
    analogNumeralSize: com.sprinthon.focusclock.domain.model.AnalogNumeralSize = com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
    analogNumeralScale: Float = 1.35f,
    isLandscape: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier.testTag("analog_clock"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        val dialSize = if (isLandscape) {
            val h = availableHeight.value * 0.82f * scale
            val w = availableWidth.value * 0.55f * scale
            minOf(h, w).coerceIn(130f, 380f).dp
        } else {
            val h = availableHeight.value * 0.58f * scale
            val w = availableWidth.value * 0.82f * scale
            minOf(h, w).coerceIn(130f, 420f).dp
        }

        val dateFontSize = (dialSize.value * 0.052f).coerceIn(12f, 19f).sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Canvas(modifier = Modifier.size(dialSize)) {
                AnalogClockDialEngine.drawDial(
                    drawScope = this,
                    timeData = timeData,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    accentColor = accentColor,
                    numeralOrientation = numeralOrientation,
                    showSeconds = showSeconds,
                    clockFont = clockFont,
                    analogNumeralSize = analogNumeralSize,
                    analogNumeralScale = analogNumeralScale
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
