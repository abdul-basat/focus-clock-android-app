package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.ui.theme.AmbientTheme

/**
 * Phase 6 Milestone 6.2: Ambient Quick Settings Sheet.
 * 
 * Moves bottom preference cards into a dedicated quick settings modal.
 * Contains:
 * - Auto-Play on Focus Start (toggle)
 * - Loop Ambient Audio (toggle)
 * - Audio Waveform Visualizer (toggle)
 * 
 * Uses AmbientTheme.colors semantic design system tokens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientQuickSettingsSheet(
    preferences: FocusPreferences,
    onDismiss: () -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleLoop: (Boolean) -> Unit,
    onToggleShowWaveform: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = AmbientTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.ambientSurface,
        contentColor = colors.ambientOnSurface,
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
                    text = "Quick Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = colors.ambientOnSurface
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = colors.ambientAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.ambientOutline, thickness = 0.75.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. AUTO-PLAY ON FOCUS START
            // ==========================================
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.ambientSurfaceVariant,
                border = BorderStroke(1.dp, colors.ambientOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_settings_auto_play")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.ambientAccentDim,
                            modifier = Modifier.size(40.dp)
                        ) {
                            androidx.compose.foundation.layout.Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = colors.ambientAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Auto-Play on Focus Start",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                ),
                                color = colors.ambientOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Automatically trigger ambient audio when starting a focus session",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = colors.ambientOnSurfaceMuted
                            )
                        }
                    }

                    Switch(
                        checked = preferences.autoPlayMusicOnFocus,
                        onCheckedChange = onToggleAutoPlay,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.ambientAccent,
                            checkedTrackColor = colors.ambientAccentDim,
                            uncheckedThumbColor = colors.ambientOnSurfaceMuted,
                            uncheckedTrackColor = colors.ambientOutline
                        ),
                        modifier = Modifier.testTag("toggle_auto_play_quick")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 2. LOOP AMBIENT AUDIO
            // ==========================================
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.ambientSurfaceVariant,
                border = BorderStroke(1.dp, colors.ambientOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_settings_loop")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.ambientAccentDim,
                            modifier = Modifier.size(40.dp)
                        ) {
                            androidx.compose.foundation.layout.Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = colors.ambientAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Loop Ambient Audio",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                ),
                                color = colors.ambientOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Continuously loop track or collection during session",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = colors.ambientOnSurfaceMuted
                            )
                        }
                    }

                    Switch(
                        checked = preferences.musicLoop,
                        onCheckedChange = onToggleLoop,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.ambientAccent,
                            checkedTrackColor = colors.ambientAccentDim,
                            uncheckedThumbColor = colors.ambientOnSurfaceMuted,
                            uncheckedTrackColor = colors.ambientOutline
                        ),
                        modifier = Modifier.testTag("toggle_loop_quick")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 3. AUDIO WAVEFORM VISUALIZER
            // ==========================================
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.ambientSurfaceVariant,
                border = BorderStroke(1.dp, colors.ambientOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_settings_waveform")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.ambientAccentDim,
                            modifier = Modifier.size(40.dp)
                        ) {
                            androidx.compose.foundation.layout.Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = colors.ambientAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Audio Waveform Visualizer",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                ),
                                color = colors.ambientOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Display animated sound waves on the active focus canvas",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = colors.ambientOnSurfaceMuted
                            )
                        }
                    }

                    Switch(
                        checked = preferences.showWaveform,
                        onCheckedChange = onToggleShowWaveform,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.ambientAccent,
                            checkedTrackColor = colors.ambientAccentDim,
                            uncheckedThumbColor = colors.ambientOnSurfaceMuted,
                            uncheckedTrackColor = colors.ambientOutline
                        ),
                        modifier = Modifier.testTag("toggle_show_waveform_quick")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
