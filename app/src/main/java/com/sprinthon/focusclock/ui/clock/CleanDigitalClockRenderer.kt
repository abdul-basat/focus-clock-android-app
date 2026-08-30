package com.sprinthon.focusclock.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CleanDigitalClockRenderer(
    timeData: ClockTimeData,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color(0xFFA6A6AC),
    clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    showDate: Boolean = true,
    showDayOfWeek: Boolean = true,
    scale: Float = 1.0f,
    isLandscape: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier.testTag("clean_digital_clock"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        // Dynamic adaptive font sizing based on available dimensions
        val baseFontSize = if (isLandscape) {
            val h = availableHeight.value * 0.40f * scale
            val w = availableWidth.value * 0.28f * scale
            minOf(h, w).coerceIn(40f, 170f)
        } else {
            val h = availableHeight.value * 0.27f * scale
            val w = availableWidth.value * 0.40f * scale
            minOf(h, w).coerceIn(36f, 140f)
        }

        val dateFontSize = (baseFontSize * 0.16f).coerceIn(13f, 18f)
        // Tight, cohesive line height so hours and minutes feel connected as one unit
        val digitLineHeight = (baseFontSize * 0.88f).sp

        val clockDigitStyle = TextStyle(
            fontSize = baseFontSize.sp,
            lineHeight = digitLineHeight,
            fontWeight = clockFont.defaultWeight,
            fontFamily = clockFont.fontFamily,
            color = primaryColor,
            textAlign = TextAlign.Center,
            letterSpacing = clockFont.letterSpacing,
            fontFeatureSettings = "tnum",
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            // Hours
            Text(
                text = timeData.hourString,
                style = clockDigitStyle,
                modifier = Modifier.testTag("clock_hours")
            )

            // Minutes (tightly stacked directly below hours)
            Text(
                text = timeData.minuteString,
                style = clockDigitStyle,
                modifier = Modifier.testTag("clock_minutes")
            )

            if (showDate || showDayOfWeek) {
                Spacer(modifier = Modifier.height((baseFontSize * 0.12f).dp.coerceIn(8.dp, 16.dp)))

                val dateLabel = when {
                    showDate && showDayOfWeek -> timeData.dateString
                    showDayOfWeek -> timeData.dayOfWeek
                    showDate -> timeData.dateString.substringAfter(" · ")
                    else -> ""
                }

                if (dateLabel.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dateLabel,
                            fontSize = dateFontSize.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = secondaryColor,
                            letterSpacing = 1.0.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("clock_date")
                        )
                        if (!timeData.is24Hour) {
                            Text(
                                text = "  ${timeData.amPm}",
                                fontSize = (dateFontSize * 0.85f).sp,
                                fontWeight = FontWeight.Light,
                                color = secondaryColor.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}
