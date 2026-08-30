package com.sprinthon.focusclock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.ui.theme.FocusAmber
import com.sprinthon.focusclock.ui.theme.FocusPaused

/**
 * Secondary timer widget rendered subordinate to the hero clock on the Active Focus screen.
 * Distinct, clean, and legible without overpowering the primary clock.
 */
@Composable
fun FocusTimerWidget(
    session: SessionSnapshot,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    spacing: Dp = 2.dp,
    textColor: Color = Color.White.copy(alpha = 0.92f),
    accentColor: Color = FocusAmber
) {
    val isPaused = session.state == SessionState.PAUSED

    val timerModeLabel = when {
        isPaused -> "PAUSED"
        session.isUnlimited -> "ELAPSED"
        else -> session.displayModeLabel.uppercase()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.testTag("focus_timer_widget")
    ) {
        // Mode indicator label (small, restrained uppercase)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isPaused) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(FocusPaused)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }

            Text(
                text = timerModeLabel,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isPaused) FocusPaused else Color.White.copy(alpha = 0.55f)
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Timer Digits (Tabular, clean, modern)
        Text(
            text = session.formattedDisplayTime,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                color = if (isPaused) FocusPaused else textColor,
                letterSpacing = 1.2.sp,
                fontFeatureSettings = "tnum",
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            modifier = Modifier.testTag("focus_timer_digits")
        )
    }
}
