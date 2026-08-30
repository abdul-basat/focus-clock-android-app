package com.sprinthon.focusclock.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartFocusScreen(
    preferences: FocusPreferences,
    configuredDurationMinutes: Int,
    isCustomDuration: Boolean,
    configuredTimerMode: TimerDisplayMode,
    modifier: Modifier = Modifier,
    onSelectPresetDuration: (PresetDuration) -> Unit,
    onSelectTimerMode: (TimerDisplayMode) -> Unit,
    onOpenClockStyleSelector: () -> Unit,
    onOpenBackgroundSettings: () -> Unit,
    onOpenAudioSettings: () -> Unit = {},
    onStartFocus: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()
    val timeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)
    val currentTrack = com.sprinthon.focusclock.playback.FocusAudioCatalog.getTrackById(preferences.selectedTrackId)

    // Ensure crisp light status bar icons over AMOLED black
    DisposableEffect(view) {
        val activity = context.findActivity()
        var originalLightStatusBars: Boolean? = null
        if (activity != null) {
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, view)
            originalLightStatusBars = insetsController.isAppearanceLightStatusBars
            insetsController.isAppearanceLightStatusBars = false
            @Suppress("DEPRECATION")
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }

        onDispose {
            activity?.let {
                val insetsController = WindowCompat.getInsetsController(it.window, view)
                originalLightStatusBars?.let { original ->
                    insetsController.isAppearanceLightStatusBars = original
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Focus Session",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("start_focus_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmoledBlack
                )
            )
        },
        bottomBar = {
            Surface(
                color = AmoledBlack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = onStartFocus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FocusAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("start_session_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "START FOCUS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = AmoledBlack,
        modifier = modifier
            .fillMaxSize()
            .testTag("start_focus_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 1: FOCUS DURATION
            SectionHeader(
                title = "FOCUS DURATION",
                icon = Icons.Outlined.HourglassEmpty
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PresetDuration.values().forEach { preset ->
                    val isSelected = when (preset) {
                        PresetDuration.CUSTOM -> isCustomDuration
                        PresetDuration.UNLIMITED -> configuredDurationMinutes <= 0 && !isCustomDuration
                        else -> configuredDurationMinutes == preset.minutes && !isCustomDuration
                    }

                    val label = if (preset == PresetDuration.CUSTOM && isCustomDuration) {
                        "$configuredDurationMinutes min (Custom)"
                    } else {
                        preset.label
                    }

                    DurationChip(
                        label = label,
                        isSelected = isSelected,
                        onClick = { onSelectPresetDuration(preset) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 2: TIMER STYLE
            SectionHeader(
                title = "TIMER DISPLAY MODE",
                icon = Icons.Outlined.Timer
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isUnlimited = configuredDurationMinutes <= 0 && !isCustomDuration

                TimerModeOption(
                    title = "Countdown",
                    description = "Countdown timer below clock",
                    isSelected = configuredTimerMode == TimerDisplayMode.COUNTDOWN && !isUnlimited,
                    enabled = !isUnlimited,
                    onClick = { onSelectTimerMode(TimerDisplayMode.COUNTDOWN) },
                    modifier = Modifier.weight(1f)
                )

                TimerModeOption(
                    title = "Elapsed",
                    description = "Clock only · No timer digits",
                    isSelected = configuredTimerMode == TimerDisplayMode.ELAPSED || isUnlimited,
                    enabled = true,
                    onClick = { onSelectTimerMode(TimerDisplayMode.ELAPSED) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 3: HERO CLOCK & CANVAS
            SectionHeader(
                title = "HERO CLOCK & CANVAS",
                icon = Icons.Outlined.Palette
            )

            Spacer(modifier = Modifier.height(10.dp))

            val bgSummaryText = when (preferences.backgroundType) {
                com.sprinthon.focusclock.domain.model.BackgroundType.SOLID_COLOR -> {
                    val colorName = com.sprinthon.focusclock.domain.model.CuratedColors.findByHex(preferences.solidBackgroundColor)?.name ?: "Solid Color"
                    "$colorName · ${(preferences.backgroundOverlayStrength * 100).toInt()}% Dim"
                }
                com.sprinthon.focusclock.domain.model.BackgroundType.SINGLE_IMAGE -> {
                    "Photo Wallpaper · ${(preferences.backgroundOverlayStrength * 100).toInt()}% Dim"
                }
                com.sprinthon.focusclock.domain.model.BackgroundType.SLIDESHOW -> {
                    "${preferences.slideshowImageUris.size} Photos · ${preferences.slideshowInterval.label}"
                }
            }

            ClockCanvasPreviewRow(
                preferences = preferences,
                timeData = timeData,
                bgSummary = bgSummaryText,
                onClick = onOpenBackgroundSettings // navigates to unified customizer
            )

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 4: SOUNDSCAPE
            SectionHeader(
                title = "AMBIENT SOUNDSCAPE",
                icon = Icons.Outlined.MusicNote
            )

            Spacer(modifier = Modifier.height(10.dp))

            ConfigurationSummaryRow(
                title = currentTrack.title,
                subtitle = "${currentTrack.artist} · ${if (preferences.autoPlayMusicOnFocus) "Auto-Play" else "Manual"} · ${if (preferences.musicLoop) "Loop On" else "Loop Off"}",
                tag = "sound_summary_row",
                onClick = onOpenAudioSettings
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FocusAmber,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun DurationChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (isSelected) FocusAmber else DarkElevatedSurface
    val textColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
    val borderModifier = if (isSelected) {
        Modifier.border(1.dp, FocusAmber, shape)
    } else {
        Modifier.border(0.5.dp, DarkOutline, shape)
    }

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bgColor)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("duration_chip_${label.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun TimerModeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val borderColor = if (isSelected) FocusAmber else DarkOutline
    val bgColor = if (isSelected) DarkElevatedSurface else DarkCardSurface

    Column(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 0.5.dp, borderColor, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(14.dp)
            .testTag("timer_mode_${title.lowercase()}"),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (enabled) (if (isSelected) FocusAmber else MaterialTheme.colorScheme.onBackground) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            fontSize = 11.sp,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun ClockCanvasPreviewRow(
    preferences: FocusPreferences,
    timeData: com.sprinthon.focusclock.ui.clock.ClockTimeData,
    bgSummary: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DarkCardSurface)
            .border(0.5.dp, DarkOutline, shape)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("clock_canvas_preview_row"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Mini preview box with synchronized background and clock
        Box(
            modifier = Modifier
                .size(width = 95.dp, height = 75.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(0.5.dp, DarkOutline, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            com.sprinthon.focusclock.ui.components.FocusBackground(
                preferences = preferences,
                isInteractivePreview = true,
                modifier = Modifier.fillMaxSize()
            )

            ClockRenderer(
                style = preferences.clockStyle,
                timeData = timeData,
                clockFont = preferences.clockFont,
                scale = 0.48f,
                showDate = false,
                showDayOfWeek = false
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${preferences.clockStyle.displayName} · ${preferences.clockFont.displayName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = bgSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = "Customize",
                style = MaterialTheme.typography.labelMedium,
                color = FocusAmber,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = FocusAmber,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ConfigurationSummaryRow(
    title: String,
    subtitle: String,
    tag: String,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(14.dp)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DarkCardSurface)
            .border(0.5.dp, DarkOutline, shape)
            .then(clickModifier)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = "Active",
                style = MaterialTheme.typography.labelSmall,
                color = FocusAmber,
                fontWeight = FontWeight.Medium
            )
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = FocusAmber,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Helper to safely extract Activity from Context hierarchy.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
