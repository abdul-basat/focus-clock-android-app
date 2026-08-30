package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    preferences: FocusPreferences,
    onToggleKeepScreenAwake: (Boolean) -> Unit,
    onToggleAutoHideControls: (Boolean) -> Unit,
    onToggleBatterySaver: (Boolean) -> Unit = {},
    onToggleImmersiveFullscreen: (Boolean) -> Unit = {},
    onToggleVibrateOnCompletion: (Boolean) -> Unit,
    onToggleConfirmBeforeExit: (Boolean) -> Unit,
    onResetAllSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0C0E),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "General Preferences",
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
                        modifier = Modifier.testTag("general_settings_back_button")
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
            SettingsSectionHeader(title = "Display & Screen")
            SettingsCard {
                SettingsToggleRow(
                    title = "Keep Screen Awake",
                    subtitle = "Prevents device display from sleeping during an active focus session",
                    icon = Icons.Default.ScreenLockPortrait,
                    checked = preferences.keepScreenAwake,
                    testTag = "toggle_keep_awake",
                    onCheckedChange = onToggleKeepScreenAwake
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Auto-Hide Controls",
                    subtitle = "Fades out player and pause controls after 4.5s of inactivity",
                    icon = Icons.Default.VisibilityOff,
                    checked = preferences.autoHideControls,
                    testTag = "toggle_auto_hide_controls",
                    onCheckedChange = onToggleAutoHideControls
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Battery Saver Mode",
                    subtitle = "Dims screen during idle focus and pauses animations to extend battery life",
                    icon = Icons.Default.BatterySaver,
                    checked = preferences.batterySaverEnabled,
                    testTag = "toggle_battery_saver",
                    onCheckedChange = onToggleBatterySaver
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Immersive Fullscreen",
                    subtitle = "When ON, hides top/bottom bars across all screens for distraction-free view",
                    icon = Icons.Default.Fullscreen,
                    checked = preferences.immersiveFullscreenEnabled,
                    testTag = "toggle_immersive_fullscreen",
                    onCheckedChange = onToggleImmersiveFullscreen
                )
            }

            SettingsSectionHeader(title = "Session Feedback")
            SettingsCard {
                SettingsToggleRow(
                    title = "Vibrate on Completion",
                    subtitle = "Trigger device vibration motor when the focus timer completes",
                    icon = Icons.Default.Vibration,
                    checked = preferences.vibrateOnCompletion,
                    testTag = "toggle_vibrate_completion_gen",
                    onCheckedChange = onToggleVibrateOnCompletion
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Confirm Before Exit",
                    subtitle = "Require confirmation before cancelling an active session",
                    icon = Icons.Default.WarningAmber,
                    checked = preferences.confirmBeforeExit,
                    testTag = "toggle_confirm_exit_gen",
                    onCheckedChange = onToggleConfirmBeforeExit
                )
            }

            SettingsSectionHeader(title = "Information & Reset")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button, onClick = onNavigateToAbout)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("about_app_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF222228),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "About Focus Clock",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Principles, version 1.6.0, and privacy",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFF8E8E96)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF6B6B73),
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            role = Role.Button,
                            onClick = { showResetDialog = true }
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("reset_settings_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF331414),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset All Settings",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFFF6B6B)
                        )
                        Text(
                            text = "Restore all configurations to their original factory defaults",
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

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset All Settings?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "This will restore all clock styles, default timers, sound preferences, and background configurations to default AMOLED presets.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCD0)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetAllSettings()
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text(
                        text = "Reset Defaults",
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFFAAAAAF)
                    )
                }
            },
            containerColor = Color(0xFF1E1E24),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
