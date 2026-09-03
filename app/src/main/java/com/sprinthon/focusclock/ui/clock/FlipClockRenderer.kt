package com.sprinthon.focusclock.ui.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlipClockRenderer(
    timeData: ClockTimeData,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color(0xFF9E9EA4),
    cardBackground: Color? = null,
    cardBorder: Color? = null,
    cardDivider: Color? = null,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    showDate: Boolean = true,
    showDayOfWeek: Boolean = true,
    scale: Float = 1.0f,
    isLandscape: Boolean = false
) {
    val isLight = primaryColor.luminance() < 0.5f
    val effectiveCardBackground = cardBackground ?: if (isLight) Color(0xFFE4E4EC) else Color(0xFF1E1E22)
    val effectiveCardBorder = cardBorder ?: if (isLight) Color(0xFFCBCBD4) else Color(0xFF2C2C32)
    val effectiveCardDivider = cardDivider ?: if (isLight) Color(0xFFB8B8C2) else Color(0xFF000000).copy(alpha = 0.8f)

    BoxWithConstraints(
        modifier = modifier.testTag("flip_clock"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        val cardWidth = if (isLandscape) {
            val w = availableWidth.value * 0.50f * scale
            val h = availableHeight.value * 0.55f * scale
            minOf(w, h * 1.5f).coerceIn(80f, 320f).dp
        } else {
            val w = availableWidth.value * 0.68f * scale
            val h = availableHeight.value * 0.35f * scale
            minOf(w, h * 1.8f).coerceIn(70f, 300f).dp
        }

        val cardHeight = (cardWidth.value * 0.72f).dp
        val fontSize = (cardHeight.value * 0.70f).sp
        val dateFontSize = (fontSize.value * 0.16f).coerceIn(12f, 22f).sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            // Hour Card
            FlipDigitCard(
                digits = timeData.hourString,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                textColor = primaryColor,
                cardBackgroundColor = effectiveCardBackground,
                cardBorderColor = effectiveCardBorder,
                cardDividerColor = effectiveCardDivider,
                isLightMode = isLight,
                clockFont = clockFont
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Minute Card
            FlipDigitCard(
                digits = timeData.minuteString,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                textColor = primaryColor,
                cardBackgroundColor = effectiveCardBackground,
                cardBorderColor = effectiveCardBorder,
                cardDividerColor = effectiveCardDivider,
                isLightMode = isLight,
                clockFont = clockFont
            )

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
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dateLabel,
                            fontSize = dateFontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = secondaryColor,
                            letterSpacing = 1.2.sp,
                            textAlign = TextAlign.Center
                        )
                        if (!timeData.is24Hour) {
                            Text(
                                text = "  ${timeData.amPm}",
                                fontSize = (dateFontSize.value * 0.85f).sp,
                                fontWeight = FontWeight.Light,
                                color = secondaryColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipDigitCard(
    digits: String,
    cardWidth: androidx.compose.ui.unit.Dp,
    cardHeight: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    textColor: Color,
    cardBackgroundColor: Color,
    cardBorderColor: Color,
    cardDividerColor: Color,
    isLightMode: Boolean,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE
) {
    val cornerShape = RoundedCornerShape(12.dp)

    val backgroundModifier = if (isLightMode) {
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    cardBackgroundColor,
                    cardBackgroundColor.copy(alpha = 0.95f),
                    cardBackgroundColor.copy(alpha = 0.90f)
                )
            )
        )
    } else {
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1E1E22),
                    Color(0xFF141416),
                    Color(0xFF0D0D0E)
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .size(width = cardWidth, height = cardHeight)
            .clip(cornerShape)
            .then(backgroundModifier)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = cornerShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle central horizontal slit / divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(cardDividerColor)
                .align(Alignment.Center)
        )

        // Digits
        Text(
            text = digits,
            fontSize = fontSize,
            fontWeight = clockFont.defaultWeight,
            fontFamily = clockFont.fontFamily,
            color = textColor,
            textAlign = TextAlign.Center,
            letterSpacing = clockFont.letterSpacing
        )
    }
}
