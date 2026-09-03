package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.CollectionPlaybackMode
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusTrack
import com.sprinthon.focusclock.domain.model.TrackCollection
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.theme.AmbientTheme
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusActiveGlow
import com.sprinthon.focusclock.ui.theme.FocusAmber

/**
 * Phase 2 Milestone 2.3 & Phase 4 Milestone 4.3: Expanded Playback Controls Modal Bottom Sheet.
 * Houses heavy playback controls (master soundscape volume slider, collection solo toggle,
 * playback mode pills, and transport controls) in a secondary sheet triggered from the mini-player.
 * Uses AmbientTheme.colors semantic design system tokens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientPlaybackDetailSheet(
    activeTrack: FocusTrack,
    activeCollection: TrackCollection?,
    playerState: PlayerUiState,
    preferences: FocusPreferences,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onClearCollection: () -> Unit,
    onSelectPlaybackMode: (CollectionPlaybackMode) -> Unit,
    modifier: Modifier = Modifier,
    onSkipPrevious: (() -> Unit)? = null,
    onSkipNext: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = AmbientTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.ambientSurface,
        contentColor = colors.ambientOnSurface,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = colors.ambientAccent.copy(alpha = 0.35f)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar with collapse button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = colors.ambientOnSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse playback controls",
                        tint = colors.ambientOnSurfaceMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. HERO ARTWORK / EQUALIZER VISUALIZATION
            // ==========================================
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (playerState.isPlaying) {
                            Brush.linearGradient(listOf(colors.ambientAccent, colors.ambientActiveGlow))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF22222C), Color(0xFF181820)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (playerState.isPlaying) {
                    AnimatedWaveformBars(isAnimating = true, barColor = Color.Black)
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = colors.ambientAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 2. TRACK & COLLECTION DETAILS
            // ==========================================
            Text(
                text = activeTrack.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = colors.ambientOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (activeTrack.isYouTube) "YouTube Audio Link" else activeTrack.artist,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                color = colors.ambientAccent,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Collection badge if playing a playlist
            if (activeCollection != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(activeCollection.accentColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(activeCollection.accentColorHex).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "PLAYLIST: ${activeCollection.name.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(activeCollection.accentColorHex),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = onClearCollection,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Play Solo Track",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = colors.ambientOnSurfaceMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = colors.ambientOutline, thickness = 0.75.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. PLAYBACK MODE SELECTOR PILLS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mode:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.ambientOnSurfaceMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val currentMode = preferences.collectionPlaybackMode
                    CollectionPlaybackMode.values().forEach { mode ->
                        val isModeSelected = currentMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isModeSelected) colors.ambientAccentDim else colors.ambientSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isModeSelected) colors.ambientAccent else colors.ambientOutline
                            ),
                            modifier = Modifier.clickable { onSelectPlaybackMode(mode) }
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.5.sp
                                ),
                                color = if (isModeSelected) colors.ambientAccent else colors.ambientOnSurfaceMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 4. TRANSPORT CONTROLS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (onSkipPrevious != null) {
                    IconButton(
                        onClick = onSkipPrevious,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = colors.ambientOnSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Big Master Play/Pause Audition Button
                Surface(
                    shape = CircleShape,
                    color = colors.ambientAccent,
                    modifier = Modifier
                        .size(58.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = onTogglePlayPause
                        )
                        .testTag("master_preview_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                if (onSkipNext != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = colors.ambientOnSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 5. MASTER VOLUME SLIDER SECTION
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Master Soundscape Volume",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.ambientOnSurface
                )
                Text(
                    text = "${(preferences.musicVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.ambientAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (preferences.musicVolume > 0f) onVolumeChange(0f) else onVolumeChange(0.7f)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (preferences.musicVolume == 0f) {
                            Icons.AutoMirrored.Filled.VolumeMute
                        } else {
                            Icons.AutoMirrored.Filled.VolumeDown
                        },
                        contentDescription = "Mute",
                        tint = if (preferences.musicVolume == 0f) colors.ambientAccent else colors.ambientOnSurfaceMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = preferences.musicVolume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.ambientAccent,
                        activeTrackColor = colors.ambientAccent,
                        inactiveTrackColor = colors.ambientOutline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("audio_volume_slider")
                )

                IconButton(
                    onClick = { onVolumeChange(1f) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Max Volume",
                        tint = colors.ambientOnSurfaceMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
