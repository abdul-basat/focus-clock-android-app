package com.sprinthon.focusclock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.sprinthon.focusclock.domain.model.AudioSourceType
import com.sprinthon.focusclock.domain.model.ExternalMediaSessionState
import com.sprinthon.focusclock.playback.PlaybackStatus
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.theme.CanvasContrastPalette
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

// Backward-compatible alias for seamless integration
typealias PlayerState = PlayerUiState

/**
 * Reusable player area component for the Active Focus screen.
 * Connects directly to the Media3 FocusPlayerManager via PlayerUiState or
 * system MediaSession (Spotify, YouTube, SoundCloud) via ExternalMediaSessionState.
 * Refined with high-contrast, touch-friendly, disciplined controls against AMOLED black or light canvas.
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
    contrastPalette: CanvasContrastPalette? = null,
    audioSourceType: AudioSourceType = AudioSourceType.AMBIENT_SOUNDS,
    externalMediaState: ExternalMediaSessionState? = null,
    onLaunchMusicApp: ((String) -> Unit)? = null,
    onOpenPermissionSettings: (() -> Unit)? = null,
    onTransferToFocus: (() -> Unit)? = null,
    showWaveform: Boolean = true,
    waveformHeight: Dp = 22.dp,
    waveformWidth: Dp = 150.dp,
    batterySaverActive: Boolean = false
) {
    if (audioSourceType == AudioSourceType.EXTERNAL_MUSIC && externalMediaState != null) {
        ExternalMusicPlayerArea(
            externalMediaState = externalMediaState,
            controlsVisible = controlsVisible,
            contrastPalette = contrastPalette,
            showWaveform = showWaveform,
            waveformHeight = waveformHeight,
            waveformWidth = waveformWidth,
            batterySaverActive = batterySaverActive,
            onPlayPauseToggle = onPlayPauseToggle,
            onNext = onNext,
            onPrevious = onPrevious,
            onLaunchMusicApp = onLaunchMusicApp,
            onOpenPermissionSettings = onOpenPermissionSettings,
            onTransferToFocus = onTransferToFocus,
            modifier = modifier
        )
    } else {
        AmbientAudioPlayerArea(
            playerState = playerState,
            controlsVisible = controlsVisible,
            contrastPalette = contrastPalette,
            showWaveform = showWaveform,
            waveformHeight = waveformHeight,
            waveformWidth = waveformWidth,
            batterySaverActive = batterySaverActive,
            onPlayPauseToggle = onPlayPauseToggle,
            onStop = onStop,
            onNext = onNext,
            onPrevious = onPrevious,
            onToggleLoop = onToggleLoop,
            modifier = modifier
        )
    }
}

/**
 * Ambient Audio / Soundscapes Player View.
 */
@Composable
private fun AmbientAudioPlayerArea(
    playerState: PlayerUiState,
    controlsVisible: Boolean,
    contrastPalette: CanvasContrastPalette?,
    showWaveform: Boolean,
    waveformHeight: Dp,
    waveformWidth: Dp,
    batterySaverActive: Boolean,
    onPlayPauseToggle: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleLoop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val effectiveAccent = contrastPalette?.accentColor ?: FocusAmber
    val effectivePrimaryText = contrastPalette?.primaryText ?: Color.White
    val effectiveSecondaryText = contrastPalette?.secondaryText ?: Color(0xFFA6A6AC)
    val effectiveButtonSurface = contrastPalette?.buttonSurface ?: Color(0xFF222226)
    val effectiveButtonBorder = contrastPalette?.buttonBorder ?: Color(0xFF383840)
    val effectiveButtonText = contrastPalette?.buttonText ?: Color.White

    val waveformPlaybackState = when (playerState.status) {
        PlaybackStatus.PLAYING -> WaveformPlaybackState.PLAYING
        PlaybackStatus.BUFFERING, PlaybackStatus.RESOLVING -> WaveformPlaybackState.BUFFERING
        PlaybackStatus.READY -> WaveformPlaybackState.PAUSED
        PlaybackStatus.ERROR -> WaveformPlaybackState.ERROR
        else -> if (!playerState.isStopped) WaveformPlaybackState.PAUSED else WaveformPlaybackState.STOPPED
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.testTag("focus_player_area")
    ) {
        if (playerState.activeCollectionName != null) {
            Text(
                text = "Collection: ${playerState.activeCollectionName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = effectiveAccent.copy(alpha = 0.95f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // Track Title (Subtle, calm, readable 14-15sp)
        Text(
            text = playerState.trackTitle.ifEmpty { "No sound selected" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = effectivePrimaryText.copy(alpha = 0.90f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            letterSpacing = 0.4.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .testTag("player_track_title")
        )

        // Sub-status indicator (Resolving / Error / State description)
        when (playerState.status) {
            PlaybackStatus.RESOLVING -> {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.5.dp,
                        color = effectiveAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Resolving audio…",
                        fontSize = 12.sp,
                        color = effectiveAccent.copy(alpha = 0.90f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            PlaybackStatus.ERROR -> {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x22EF5350),
                    border = BorderStroke(1.dp, Color(0x66EF5350)),
                    onClick = {
                        android.util.Log.d("FocusClockApp", "[DIAGNOSTIC] YouTube retry clicked: trackId=${playerState.currentTrack.id}, title='${playerState.currentTrack.title}', isYouTube=${playerState.currentTrack.isYouTube}, status=${playerState.status}")
                        onPlayPauseToggle()
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unable to play • Tap to retry",
                            fontSize = 11.sp,
                            color = Color(0xFFEF5350),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Waveform
        if (showWaveform) {
            FocusWaveform(
                playbackState = waveformPlaybackState,
                height = waveformHeight,
                width = waveformWidth,
                activeColor = effectiveAccent.copy(alpha = 0.95f),
                inactiveColor = effectivePrimaryText.copy(alpha = 0.25f),
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
                        tint = effectiveSecondaryText.copy(alpha = 0.85f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 2. Play / Pause Primary Button (50dp size, elevated & bordered for contrast)
                val isPlaying = playerState.isPlaying
                val playButtonBg = if (isPlaying) effectiveAccent else effectiveButtonSurface
                val playButtonBorder = if (isPlaying) effectiveAccent else effectiveButtonBorder
                val playButtonTint = if (isPlaying) Color.Black else effectiveButtonText

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
                        tint = effectiveSecondaryText.copy(alpha = 0.85f),
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
                        tint = effectiveSecondaryText.copy(alpha = 0.85f),
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
                val loopTint = if (isLoopActive) effectiveAccent else effectiveSecondaryText.copy(alpha = 0.50f)
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

/**
 * External Music (Spotify, YouTube, SoundCloud) Player View with Live Metadata & Remote Controls.
 */
@Composable
private fun ExternalMusicPlayerArea(
    externalMediaState: ExternalMediaSessionState,
    controlsVisible: Boolean,
    contrastPalette: CanvasContrastPalette?,
    showWaveform: Boolean,
    waveformHeight: Dp,
    waveformWidth: Dp,
    batterySaverActive: Boolean,
    onPlayPauseToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onLaunchMusicApp: ((String) -> Unit)?,
    onOpenPermissionSettings: (() -> Unit)?,
    onTransferToFocus: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val effectiveAccent = contrastPalette?.accentColor ?: FocusAmber
    val effectivePrimaryText = contrastPalette?.primaryText ?: Color.White
    val effectiveSecondaryText = contrastPalette?.secondaryText ?: Color(0xFFA6A6AC)
    val effectiveCardBg = contrastPalette?.cardBackground ?: Color(0xFF222228)
    val effectiveButtonSurface = contrastPalette?.buttonSurface ?: Color(0xFF222226)
    val effectiveButtonBorder = contrastPalette?.buttonBorder ?: Color(0xFF383840)
    val effectiveButtonText = contrastPalette?.buttonText ?: Color.White

    val waveformPlaybackState = if (externalMediaState.isPlaying) {
        WaveformPlaybackState.PLAYING
    } else if (externalMediaState.hasActiveSession) {
        WaveformPlaybackState.PAUSED
    } else {
        WaveformPlaybackState.STOPPED
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.testTag("focus_external_player_area")
    ) {
        if (!externalMediaState.hasNotificationPermission && onOpenPermissionSettings != null) {
            // Permission banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(effectiveCardBg)
                    .border(1.dp, effectiveAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { onOpenPermissionSettings() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = effectiveAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Grant Media Access to Control Music",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = effectiveAccent
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (externalMediaState.hasActiveSession) {
            // Track Info Row with Album Art Thumbnail & Source App Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("external_media_info_row")
            ) {
                if (externalMediaState.albumArt != null) {
                    Image(
                        bitmap = externalMediaState.albumArt.asImageBitmap(),
                        contentDescription = "Album Artwork",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(0.5.dp, effectivePrimaryText.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(effectiveCardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = effectiveAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = externalMediaState.displayTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = effectivePrimaryText.copy(alpha = 0.95f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = externalMediaState.displaySubtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = effectiveSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            // No active session state with quick app launchers
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "No Music Playing",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = effectivePrimaryText.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Start playing in Spotify, YouTube, or SoundCloud",
                    fontSize = 11.sp,
                    color = effectiveSecondaryText.copy(alpha = 0.70f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Waveform
        if (showWaveform) {
            FocusWaveform(
                playbackState = waveformPlaybackState,
                height = waveformHeight,
                width = waveformWidth,
                activeColor = effectiveAccent.copy(alpha = 0.95f),
                inactiveColor = effectivePrimaryText.copy(alpha = 0.25f),
                batterySaverActive = batterySaverActive
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Remote Controls
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
                    .testTag("external_media_controls_row")
            ) {
                // 1. Skip Previous
                IconButton(
                    onClick = onPrevious,
                    enabled = externalMediaState.canSkipToPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("external_player_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = if (externalMediaState.canSkipToPrevious) effectiveSecondaryText.copy(alpha = 0.85f) else effectiveSecondaryText.copy(alpha = 0.30f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 2. Play / Pause Button
                val isPlaying = externalMediaState.isPlaying
                val playButtonBg = if (isPlaying) effectiveAccent else effectiveButtonSurface
                val playButtonBorder = if (isPlaying) effectiveAccent else effectiveButtonBorder
                val playButtonTint = if (isPlaying) Color.Black else effectiveButtonText

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(playButtonBg)
                        .border(1.dp, playButtonBorder, CircleShape)
                        .clickable(
                            onClick = onPlayPauseToggle
                        )
                        .testTag("external_player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = playButtonTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3. Skip Next
                IconButton(
                    onClick = onNext,
                    enabled = externalMediaState.canSkipToNext,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("external_player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = if (externalMediaState.canSkipToNext) effectiveSecondaryText.copy(alpha = 0.85f) else effectiveSecondaryText.copy(alpha = 0.30f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 4. Optional Quick Launch / Open Player App Button
                if (externalMediaState.packageName.isNotBlank() && onLaunchMusicApp != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onLaunchMusicApp(externalMediaState.packageName) },
                        modifier = Modifier
                            .size(48.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("external_player_open_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open ${externalMediaState.appName.ifBlank { "Music App" }}",
                            tint = effectiveAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (onTransferToFocus != null && externalMediaState.displayTitle.isNotBlank() && externalMediaState.displayTitle != "No media playing") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(effectiveCardBg)
                        .border(1.dp, effectiveAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onTransferToFocus() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("transfer_track_to_focus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = effectiveAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Play in Focus Screen",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = effectiveAccent
                    )
                }
            }
        }
    }
}

