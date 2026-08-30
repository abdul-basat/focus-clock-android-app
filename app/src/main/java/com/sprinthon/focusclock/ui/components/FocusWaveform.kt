package com.sprinthon.focusclock.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sprinthon.focusclock.ui.theme.FocusAmber

enum class WaveformPlaybackState {
    PLAYING,
    PAUSED,
    STOPPED,
    BUFFERING,
    ERROR
}

/**
 * Lightweight, serene waveform component for active focus audio visualization.
 * Designed to be visually subtle, peaceful, and extremely CPU-efficient.
 */
@Composable
fun FocusWaveform(
    playbackState: WaveformPlaybackState,
    modifier: Modifier = Modifier,
    activeColor: Color = FocusAmber.copy(alpha = 0.85f),
    inactiveColor: Color = Color.White.copy(alpha = 0.25f),
    barCount: Int = 24,
    height: Dp = 24.dp,
    width: Dp = 160.dp,
    batterySaverActive: Boolean = false
) {
    val isPlaying = playbackState == WaveformPlaybackState.PLAYING
    val isBuffering = playbackState == WaveformPlaybackState.BUFFERING

    // Infinite gentle oscillation transition (calm, non-distracting) - paused in battery saver
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformOscillation")
    
    val phase1 by if (!batterySaverActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "phase1"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.6f) }
    }

    val phase2 by if (!batterySaverActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "phase2"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.6f) }
    }

    val bufferingPhase by if (!batterySaverActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bufferingPhase"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.4f) }
    }


    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .testTag("focus_waveform"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val totalWidth = size.width
            val totalHeight = size.height
            val barWidth = (totalWidth / (barCount * 1.6f)).coerceIn(2.dp.toPx(), 6.dp.toPx())
            val spacing = (totalWidth - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

            // Calm baseline heights using a harmonic wave shape
            for (i in 0 until barCount) {
                val normalizedIndex = i.toFloat() / (barCount - 1)
                // Center-weighted arch (sine wave shape)
                val baseArch = kotlin.math.sin(normalizedIndex * Math.PI).toFloat().coerceIn(0.15f, 1.0f)
                
                val currentFactor = when {
                    isPlaying -> {
                        val variation = if (i % 2 == 0) phase1 else phase2
                        (baseArch * 0.5f + variation * 0.5f).coerceIn(0.18f, 1.0f)
                    }
                    isBuffering -> {
                        (baseArch * 0.3f + bufferingPhase * 0.3f).coerceIn(0.15f, 0.6f)
                    }
                    playbackState == WaveformPlaybackState.PAUSED -> {
                        (baseArch * 0.45f).coerceIn(0.15f, 0.7f)
                    }
                    else -> {
                        0.15f // Minimal idle line
                    }
                }

                val barH = (totalHeight * currentFactor).coerceAtLeast(3.dp.toPx())
                val startX = i * (barWidth + spacing)
                val startY = (totalHeight - barH) / 2f

                val color = when {
                    isPlaying -> activeColor
                    isBuffering -> activeColor.copy(alpha = 0.5f)
                    else -> inactiveColor
                }

                drawRoundRect(
                    color = color,
                    topLeft = Offset(startX, startY),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}
