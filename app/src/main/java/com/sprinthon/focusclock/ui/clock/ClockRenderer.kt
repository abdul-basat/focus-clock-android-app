package com.sprinthon.focusclock.ui.clock

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.ui.theme.FocusAmber

@Composable
fun ClockRenderer(
    style: ClockStyle,
    timeData: ClockTimeData,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.onBackground,
    secondaryColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    accentColor: Color = FocusAmber,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    showDate: Boolean = true,
    showDayOfWeek: Boolean = true,
    scale: Float = 1.0f,
    isLandscape: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = style,
            animationSpec = tween(durationMillis = 350),
            label = "ClockStyleTransition"
        ) { targetStyle ->
            when (targetStyle) {
                ClockStyle.CLEAN_DIGITAL -> CleanDigitalClockRenderer(
                    timeData = timeData,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    clockFont = clockFont,
                    showDate = showDate,
                    showDayOfWeek = showDayOfWeek,
                    scale = scale,
                    isLandscape = isLandscape
                )
                ClockStyle.FLIP_CLOCK -> FlipClockRenderer(
                    timeData = timeData,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    clockFont = clockFont,
                    showDate = showDate,
                    showDayOfWeek = showDayOfWeek,
                    scale = scale,
                    isLandscape = isLandscape
                )
                ClockStyle.MINIMAL_DIGITAL -> MinimalClockRenderer(
                    timeData = timeData,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    clockFont = clockFont,
                    showDate = showDate,
                    showDayOfWeek = showDayOfWeek,
                    scale = scale,
                    isLandscape = isLandscape
                )
                ClockStyle.ANALOG -> AnalogClockRenderer(
                    timeData = timeData,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    accentColor = accentColor,
                    showDate = showDate,
                    showDayOfWeek = showDayOfWeek,
                    scale = scale,
                    isLandscape = isLandscape
                )
            }
        }
    }
}
