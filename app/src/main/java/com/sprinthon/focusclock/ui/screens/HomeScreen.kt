package com.sprinthon.focusclock.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.FocusHistoryRecord
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusProfile
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    preferences: FocusPreferences,
    configuredDurationMinutes: Int,
    configuredTimerMode: TimerDisplayMode,
    allProfiles: List<FocusProfile>,
    historyRecords: List<FocusHistoryRecord>,
    onStartFocus: () -> Unit,
    onOpenStartConfig: () -> Unit,
    onOpenClockStyleSelector: () -> Unit,
    onOpenProfileSelector: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val timeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)
    var showHistorySection by remember { mutableStateOf(false) }

    val activeProfile = allProfiles.find { it.id == preferences.activeProfileId }
    val profileName = activeProfile?.name ?: "Custom"

    val durationLabel = if (configuredDurationMinutes <= 0) "Unlimited" else "$configuredDurationMinutes min"
    val modeLabel = if (configuredDurationMinutes <= 0) "Elapsed" else configuredTimerMode.name.lowercase().replaceFirstChar { it.uppercase() }

    // Calculate today's focus minutes
    val nowMillis = System.currentTimeMillis()
    val todayStartMillis = remember(nowMillis) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    val todayRecords = historyRecords.filter { it.timestamp >= todayStartMillis && it.completed }
    val todayMinutesFocused = todayRecords.sumOf { (it.actualSecondsElapsed / 60).toInt() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("home_screen")
    ) {
        // Top Ambient Controls: Clock Style, Active Profile & Settings Hub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clock style indicator button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkElevatedSurface.copy(alpha = 0.7f))
                    .clickable(onClick = onOpenClockStyleSelector)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("open_clock_style_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = "Clock Style",
                    tint = FocusAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = preferences.clockStyle.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Active Preset Shortcut
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkElevatedSurface.copy(alpha = 0.7f))
                    .clickable(onClick = onOpenProfileSelector)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("home_profile_selector_button")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Preset",
                    tint = FocusAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
            }

            // Central Settings Hub Shortcut
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkElevatedSurface.copy(alpha = 0.7f))
                    .testTag("open_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isLandscape) {
            // Landscape layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left: Hero Clock
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ClockRenderer(
                        style = preferences.clockStyle,
                        timeData = timeData,
                        clockFont = preferences.clockFont,
                        scale = 1.0f,
                        showDate = preferences.showDate,
                        showDayOfWeek = preferences.showDayOfWeek,
                        isLandscape = true
                    )
                }

                // Right: Session Summary & Start Button
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SessionSummaryBadge(
                        durationLabel = durationLabel,
                        modeLabel = modeLabel,
                        clockStyleName = preferences.clockStyle.displayName,
                        onClick = onOpenStartConfig
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StartFocusPrimaryButton(
                        onStart = onStartFocus,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
            }
        } else {
            // Portrait layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Hero Clock in upper center
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ClockRenderer(
                        style = preferences.clockStyle,
                        timeData = timeData,
                        clockFont = preferences.clockFont,
                        scale = 1.0f,
                        showDate = preferences.showDate,
                        showDayOfWeek = preferences.showDayOfWeek,
                        isLandscape = false
                    )
                }

                // Bottom Session Summary, History Toggle & Start Action
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Daily focus metrics & recent history banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkElevatedSurface.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showHistorySection = !showHistorySection }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("today_focus_history_banner")
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = FocusAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (todayMinutesFocused > 0) "Today: $todayMinutesFocused min (${todayRecords.size} session${if (todayRecords.size > 1) "s" else ""})" else "Today: No sessions yet",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        ),
                                        color = if (todayMinutesFocused > 0) Color.White else Color(0xFF8E8E96)
                                    )
                                }

                                Icon(
                                    imageVector = if (showHistorySection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF8E8E96),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = showHistorySection) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    if (historyRecords.isEmpty()) {
                                        Text(
                                            text = "Complete your first focus session to see it logged here.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF7A7A84),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                                        historyRecords.take(4).forEach { record ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = if (record.completed) Color(0xFF81C784) else FocusAmber,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = record.profileName,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = Color(0xFFE2E2E6)
                                                    )
                                                }
                                                Text(
                                                    text = "${record.actualSecondsElapsed / 60} min · ${timeFormatter.format(Date(record.timestamp))}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = Color(0xFF8E8E96)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SessionSummaryBadge(
                        durationLabel = durationLabel,
                        modeLabel = modeLabel,
                        clockStyleName = preferences.clockStyle.displayName,
                        onClick = onOpenStartConfig
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StartFocusPrimaryButton(
                        onStart = onStartFocus,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SessionSummaryBadge(
    durationLabel: String,
    modeLabel: String,
    clockStyleName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkElevatedSurface.copy(alpha = 0.5f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("session_summary_badge")
    ) {
        Text(
            text = "$durationLabel  ·  $modeLabel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun StartFocusPrimaryButton(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onStart,
        colors = ButtonDefaults.buttonColors(
            containerColor = FocusAmber,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .height(56.dp)
            .testTag("start_focus_button")
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

