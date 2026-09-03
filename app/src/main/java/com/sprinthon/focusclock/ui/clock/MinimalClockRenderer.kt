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
fun MinimalClockRenderer(
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
        modifier = modifier.testTag("minimal_clock"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        val baseFontSize = if (isLandscape) {
            val h = availableHeight.value * 0.48f * scale
            val w = availableWidth.value * 0.36f * scale
            minOf(h, w).coerceIn(40f, 240f)
        } else {
            val h = availableHeight.value * 0.34f * scale
            val w = availableWidth.value * 0.52f * scale
            minOf(h, w).coerceIn(36f, 220f)
        }

        val dateFontSize = (baseFontSize * 0.15f).coerceIn(12f, 18f)
        val digitLineHeight = (baseFontSize * 0.86f).sp

        val clockDigitStyle = TextStyle(
            fontSize = baseFontSize.sp,
            lineHeight = digitLineHeight,
            fontWeight = clockFont.defaultWeight,
            fontFamily = clockFont.fontFamily,
            color = primaryColor,
            letterSpacing = clockFont.letterSpacing,
            textAlign = TextAlign.Center,
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
                style = clockDigitStyle
            )

            // Minutes
            Text(
                text = timeData.minuteString,
                style = clockDigitStyle
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
                            text = dateLabel.uppercase(),
                            fontSize = dateFontSize.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.SansSerif,
                            color = secondaryColor,
                            letterSpacing = 2.0.sp,
                            textAlign = TextAlign.Center
                        )
                        if (!timeData.is24Hour) {
                            Text(
                                text = "  ${timeData.amPm}",
                                fontSize = (dateFontSize * 0.85f).sp,
                                fontWeight = FontWeight.ExtraLight,
                                color = secondaryColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}
