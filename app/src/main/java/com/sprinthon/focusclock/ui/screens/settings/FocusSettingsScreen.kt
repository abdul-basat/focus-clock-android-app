package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusProfile
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FocusSettingsScreen(
    preferences: FocusPreferences,
    session: SessionSnapshot,
    modifier: Modifier = Modifier,
    allProfiles: List<FocusProfile> = FocusProfile.DEFAULT_PROFILES,
    onOpenProfileSelector: () -> Unit = {},
    onSelectDefaultDuration: (Int) -> Unit,
    onOpenCustomDurationDialog: () -> Unit,
    onSelectTimerMode: (TimerDisplayMode) -> Unit,
    onToggleVibrateOnCompletion: (Boolean) -> Unit,
    onToggleNotifyOnCompletion: (Boolean) -> Unit = {},
    onToggleSoundOnCompletion: (Boolean) -> Unit = {},
    onToggleConfirmBeforeExit: (Boolean) -> Unit,
    onClearHistory: () -> Unit = {},
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isSessionActive = session.state == SessionState.RUNNING || session.state == SessionState.PAUSED
    val activeProfile = allProfiles.find { it.id == preferences.activeProfileId }
    val activeProfileName = activeProfile?.name ?: "Custom"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0C0E),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Focus Session",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("focus_settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0C0C0E)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
            if (isSessionActive) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2E1C08),
                    border = BorderStroke(1.dp, FocusAmber.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = FocusAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "A focus session is currently in progress. Updates will become the default for your next session.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = Color(0xFFFFD59E)
                            )
                        )
                    }
                }
            }

            // Focus Profiles / Presets Shortcut
            SettingsSectionHeader(title = "Focus Profiles & Presets")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenProfileSelector)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("manage_profiles_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FocusAmber.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = FocusAmber,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Preset: $activeProfileName",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Switch or create custom focus configurations",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFF8E8E96)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Presets",
                        tint = Color(0xFF8E8E96),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            SettingsSectionHeader(title = "Default Focus Duration")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF141418),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose the initial duration for new focus sessions",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF8E8E96),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PresetDuration.values().forEach { preset ->
                            val isSelected = when (preset) {
                                PresetDuration.CUSTOM -> preferences.defaultDurationMinutes !in listOf(15, 25, 45, 60, 0)
                                PresetDuration.UNLIMITED -> preferences.defaultDurationMinutes == 0
                                else -> preferences.defaultDurationMinutes == preset.minutes
                            }

                            val labelText = if (preset == PresetDuration.CUSTOM && isSelected) {
                                "${preferences.defaultDurationMinutes} min (Custom)"
                            } else {
                                preset.label
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) FocusAmber else Color(0xFF222228),
                                border = if (isSelected) null else BorderStroke(0.5.dp, Color(0xFF33333C)),
                                modifier = Modifier
                                    .clickable(
                                        role = Role.RadioButton,
                                        onClick = {
                                            if (preset == PresetDuration.CUSTOM) {
                                                onOpenCustomDurationDialog()
                                            } else {
                                                onSelectDefaultDuration(preset.minutes)
                                            }
                                        }
                                    )
                                    .testTag("preset_chip_${preset.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = labelText,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        ),
                                        color = if (isSelected) Color.Black else Color(0xFFE2E2E6)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SettingsSectionHeader(title = "Timer Display Mode")
            SettingsCard {
                TimerModeRow(
                    title = "Countdown Timer",
                    subtitle = "Counts down from your target focus time below the clock",
                    icon = Icons.Default.Timer,
                    selected = preferences.timerDisplayMode == TimerDisplayMode.COUNTDOWN,
                    testTag = "timer_mode_countdown",
                    onClick = { onSelectTimerMode(TimerDisplayMode.COUNTDOWN) }
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                TimerModeRow(
                    title = "Elapsed (Clock Only)",
                    subtitle = "Shows clock only during focus without countdown or count-up timer digits",
                    icon = Icons.Default.HourglassBottom,
                    selected = preferences.timerDisplayMode == TimerDisplayMode.ELAPSED,
                    testTag = "timer_mode_elapsed",
                    onClick = { onSelectTimerMode(TimerDisplayMode.ELAPSED) }
                )
            }

            SettingsSectionHeader(title = "Session Completion Feedback")
            SettingsCard {
                SettingsToggleRow(
                    title = "Completion Chime",
                    subtitle = "Play a serene harmonic chime sound when your session finishes",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    checked = preferences.soundOnCompletion,
                    testTag = "toggle_sound_completion",
                    onCheckedChange = onToggleSoundOnCompletion
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Completion Notification",
                    subtitle = "Post a banner notification when focus goal is reached",
                    icon = Icons.Default.NotificationsActive,
                    checked = preferences.notifyOnCompletion,
                    testTag = "toggle_notify_completion",
                    onCheckedChange = onToggleNotifyOnCompletion
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Vibrate on Completion",
                    subtitle = "Gentle haptic pulses when your focus timer finishes",
                    icon = Icons.Default.Vibration,
                    checked = preferences.vibrateOnCompletion,
                    testTag = "toggle_vibrate_completion",
                    onCheckedChange = onToggleVibrateOnCompletion
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Confirm Before Exit",
                    subtitle = "Show a confirmation dialog before cancelling an active focus session",
                    icon = Icons.Default.WarningAmber,
                    checked = preferences.confirmBeforeExit,
                    testTag = "toggle_confirm_exit",
                    onCheckedChange = onToggleConfirmBeforeExit
                )
            }

            SettingsSectionHeader(title = "Focus History")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClearHistory)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("clear_history_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3B1818),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clear Focus History",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFFF6B6B)
                        )
                        Text(
                            text = "Reset all logged session records and metrics",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFF8E8E96)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}

@Composable
private fun TimerModeRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) FocusAmber.copy(alpha = 0.15f) else Color(0xFF222228),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) FocusAmber else Color(0xFF9E9EA4),
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                color = if (selected) Color.White else Color(0xFFD0D0D5)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color(0xFF8E8E96)
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = FocusAmber,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
