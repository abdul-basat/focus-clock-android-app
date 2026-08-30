package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.CuratedColors
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsNavigationRow
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHubScreen(
    preferences: FocusPreferences,
    session: SessionSnapshot,
    onNavigateToFocusSettings: () -> Unit,
    onNavigateToClockSettings: () -> Unit,
    onNavigateToBackgroundSettings: () -> Unit,
    onNavigateToAudioSettings: () -> Unit,
    onNavigateToGeneralSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Generate scannable summary badges
    val focusSummary = if (preferences.defaultDurationMinutes <= 0) {
        "Unlimited · Elapsed"
    } else {
        "${preferences.defaultDurationMinutes} min · ${if (preferences.timerDisplayMode == TimerDisplayMode.COUNTDOWN) "Countdown" else "Elapsed"}"
    }

    val clockSummary = "${preferences.clockStyle.displayName} · ${if (preferences.timeFormat24Hour) "24h" else "12h"}"

    val backgroundSummary = when (preferences.backgroundType) {
        BackgroundType.SOLID_COLOR -> {
            val colorName = CuratedColors.findByHex(preferences.solidBackgroundColor)?.name ?: "Custom Color"
            "$colorName (${(preferences.backgroundOverlayStrength * 100).toInt()}%)"
        }
        BackgroundType.SINGLE_IMAGE -> "Single Photo (${(preferences.backgroundOverlayStrength * 100).toInt()}%)"
        BackgroundType.SLIDESHOW -> "Slideshow (${preferences.slideshowImageUris.size} photos)"
    }

    val currentTrack = FocusAudioCatalog.getTrackById(preferences.selectedTrackId)
    val audioSummary = "${currentTrack.title} · ${if (preferences.autoPlayMusicOnFocus) "Auto" else "Manual"}"

    val generalSummary = if (preferences.immersiveFullscreenEnabled) "Fullscreen On" else if (preferences.keepScreenAwake) "Keep Awake On" else "Standard Timeout"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0C0E),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Configure once · Put the phone down",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF8E8E96)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
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
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState)
            ) {
            // Active session banner if running
            if (session.state == SessionState.RUNNING || session.state == SessionState.PAUSED) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FocusAmber.copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FocusAmber)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Session in progress (${session.state.name.lowercase()}). Settings will apply to future focus sessions.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = FocusAmber
                            )
                        )
                    }
                }
            }

            SettingsSectionHeader(title = "Visual & Audio Customization")
            SettingsCard {
                SettingsNavigationRow(
                    title = "Clock & Canvas Studio",
                    subtitle = "Dial styles, typography fonts, backgrounds, and dimming",
                    icon = Icons.Default.Palette,
                    iconTint = FocusAmber,
                    badgeValue = "$clockSummary · $backgroundSummary",
                    testTag = "settings_clock_row",
                    onClick = onNavigateToClockSettings
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsNavigationRow(
                    title = "Soundscape & Music Player",
                    subtitle = "Ambient catalog, custom tracks, autoplay, and looping",
                    icon = Icons.Default.GraphicEq,
                    iconTint = Color(0xFFFFB74D),
                    badgeValue = audioSummary,
                    testTag = "settings_audio_row",
                    onClick = onNavigateToAudioSettings
                )
            }

            SettingsSectionHeader(title = "Focus & System")
            SettingsCard {
                SettingsNavigationRow(
                    title = "Focus Session Defaults",
                    subtitle = "Default duration, timer mode, and presets",
                    icon = Icons.Default.HourglassEmpty,
                    iconTint = FocusAmber,
                    badgeValue = focusSummary,
                    testTag = "settings_focus_row",
                    onClick = onNavigateToFocusSettings
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsNavigationRow(
                    title = "Device & App Preferences",
                    subtitle = "Awake lock, auto-hide, haptics, and reset",
                    icon = Icons.Default.Tune,
                    iconTint = Color(0xFFBA68C8),
                    badgeValue = generalSummary,
                    testTag = "settings_general_row",
                    onClick = onNavigateToGeneralSettings
                )
            }

            SettingsSectionHeader(title = "About & Information")
            SettingsCard {
                SettingsNavigationRow(
                    title = "About Focus Clock",
                    subtitle = "Version 1.6.0 · Offline & 100% Private",
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF90A4AE),
                    testTag = "settings_about_row",
                    onClick = onNavigateToAbout
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}
