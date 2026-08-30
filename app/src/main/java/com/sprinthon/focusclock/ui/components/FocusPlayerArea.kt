package com.sprinthon.focusclock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.sprinthon.focusclock.playback.PlaybackStatus
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

// Backward-compatible alias for seamless integration
typealias PlayerState = PlayerUiState

/**
 * Reusable player area component for the Active Focus screen.
 * Connects directly to the Media3 FocusPlayerManager via PlayerUiState.
 * Refined with high-contrast, touch-friendly, disciplined controls against AMOLED black.
 */
@Composable
fun FocusPlayerArea(
    playerState: PlayerUiState,
    controlsVisible: Boolean,
    onPlayPauseToggle: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleLoop: () -> Unit,
    modifier: Modifier = Modifier,
    showWaveform: Boolean = true,
    waveformHeight: Dp = 22.dp,
    waveformWidth: Dp = 150.dp,
    batterySaverActive: Boolean = false
) {
    val waveformPlaybackState = when (playerState.status) {
        PlaybackStatus.PLAYING -> WaveformPlaybackState.PLAYING
        PlaybackStatus.BUFFERING -> WaveformPlaybackState.BUFFERING
        PlaybackStatus.READY -> WaveformPlaybackState.PAUSED
        PlaybackStatus.ERROR -> WaveformPlaybackState.ERROR
        else -> if (!playerState.isStopped) WaveformPlaybackState.PAUSED else WaveformPlaybackState.STOPPED
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.testTag("focus_player_area")
    ) {
        // Track Title (Subtle, calm, readable 14-15sp)
        Text(
            text = playerState.trackTitle.ifEmpty { "No sound selected" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            letterSpacing = 0.4.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .testTag("player_track_title")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Waveform
        if (showWaveform) {
            FocusWaveform(
                playbackState = waveformPlaybackState,
                height = waveformHeight,
                width = waveformWidth,
                activeColor = FocusAmber.copy(alpha = 0.90f),
                inactiveColor = Color.White.copy(alpha = 0.30f),
                batterySaverActive = batterySaverActive
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playback Controls (Revealed on interaction / auto-hides)
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("playback_controls_row")
            ) {
                // 1. Previous Track
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("player_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous track",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 2. Play / Pause Primary Button (50dp size, elevated & bordered for contrast on black)
                val isPlaying = playerState.isPlaying
                val playButtonBg = if (isPlaying) FocusAmber else Color(0xFF222226)
                val playButtonBorder = if (isPlaying) FocusAmber else Color(0xFF383840)
                val playButtonTint = if (isPlaying) Color.Black else Color.White

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(playButtonBg)
                        .border(1.dp, playButtonBorder, CircleShape)
                        .clickable(
                            onClick = onPlayPauseToggle
                        )
                        .testTag("player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = playButtonTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 3. Stop Button
                IconButton(
                    onClick = onStop,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("player_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 4. Next Track
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next track",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 5. Loop Toggle (Off / Repeat All / Repeat One)
                val loopIcon = when (playerState.repeatMode) {
                    Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                }
                val isLoopActive = playerState.repeatMode != Player.REPEAT_MODE_OFF
                val loopTint = if (isLoopActive) FocusAmber else Color.White.copy(alpha = 0.45f)
                val loopDescription = when (playerState.repeatMode) {
                    Player.REPEAT_MODE_ONE -> "Repeat track"
                    Player.REPEAT_MODE_ALL -> "Repeat playlist"
                    else -> "Repeat off"
                }

                IconButton(
                    onClick = onToggleLoop,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("player_loop_button")
                ) {
                    Icon(
                        imageVector = loopIcon,
                        contentDescription = loopDescription,
                        tint = loopTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
