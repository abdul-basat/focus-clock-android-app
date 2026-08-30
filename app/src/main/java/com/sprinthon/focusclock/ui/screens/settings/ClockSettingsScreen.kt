package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.DateFormatOption
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.ui.clock.ClockFont
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockSettingsScreen(
    preferences: FocusPreferences,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectClockFont: (ClockFont) -> Unit = {},
    onToggle24Hour: (Boolean) -> Unit,
    onToggleShowDate: (Boolean) -> Unit,
    onToggleShowDayOfWeek: (Boolean) -> Unit,
    onSelectDateFormat: (DateFormatOption) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val currentTimeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0C0E),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Clock Display",
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
                        modifier = Modifier.testTag("clock_settings_back_button")
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
            // Live Interactive Preview Hero
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF050507),
                border = BorderStroke(1.dp, Color(0xFF22222A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = FocusAmber
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ClockRenderer(
                            style = preferences.clockStyle,
                            timeData = currentTimeData,
                            primaryColor = Color.White,
                            secondaryColor = Color(0xFF8E8E96),
                            accentColor = FocusAmber,
                            clockFont = preferences.clockFont,
                            showDate = preferences.showDate,
                            showDayOfWeek = preferences.showDayOfWeek,
                            scale = 0.85f
                        )
                    }
                    Text(
                        text = "${preferences.clockStyle.displayName} · ${preferences.clockFont.displayName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFCCCCD0)
                    )
                }
            }

            SettingsSectionHeader(title = "Clock Typography")
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ClockFont.entries.forEach { font ->
                    val isFontSelected = preferences.clockFont == font
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isFontSelected) Color(0xFF1C1917) else Color(0xFF141417),
                        border = BorderStroke(
                            if (isFontSelected) 2.dp else 1.dp,
                            if (isFontSelected) FocusAmber else Color(0xFF26262E)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                role = Role.RadioButton,
                                onClick = { onSelectClockFont(font) }
                            )
                            .testTag("clock_font_card_${font.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = font.displayName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = if (isFontSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isFontSelected) FocusAmber else Color.White
                                    )
                                    if (font == ClockFont.BEBAS_NEUE || font == ClockFont.TEKO) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(FocusAmber.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FocusAmber
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = font.tagLine,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.55f)
                                )
                            }

                            // Live sample
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF08080A))
                                    .border(0.5.dp, if (isFontSelected) FocusAmber.copy(alpha = 0.4f) else Color(0xFF202026), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "10:45",
                                    fontFamily = font.fontFamily,
                                    fontWeight = font.defaultWeight,
                                    letterSpacing = font.letterSpacing,
                                    fontSize = 20.sp,
                                    color = if (isFontSelected) FocusAmber else Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            if (isFontSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(FocusAmber),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            SettingsSectionHeader(title = "Clock Style")
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ClockStyle.entries.chunked(2).forEach { rowStyles ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowStyles.forEach { style ->
                            val isSelected = preferences.clockStyle == style
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF1C1917) else Color(0xFF141417),
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) FocusAmber else Color(0xFF26262E)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        role = Role.RadioButton,
                                        onClick = { onSelectClockStyle(style) }
                                    )
                                    .testTag("clock_style_card_${style.name.lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(85.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ClockRenderer(
                                            style = style,
                                            timeData = currentTimeData,
                                            primaryColor = if (isSelected) FocusAmber else Color.White,
                                            secondaryColor = Color(0xFF7E7E88),
                                            accentColor = FocusAmber,
                                            showDate = false,
                                            showDayOfWeek = false,
                                            scale = 0.52f
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(FocusAmber)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = style.displayName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.sp
                                            ),
                                            color = if (isSelected) FocusAmber else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SettingsSectionHeader(title = "Time & Date Format")
            SettingsCard {
                SettingsToggleRow(
                    title = "24-Hour Time Format",
                    subtitle = if (preferences.timeFormat24Hour) "Using 24-hour cycle (e.g. 14:25)" else "Using 12-hour cycle (e.g. 02:25 PM)",
                    icon = Icons.Default.Schedule,
                    checked = preferences.timeFormat24Hour,
                    testTag = "toggle_24h_format",
                    onCheckedChange = onToggle24Hour
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Show Date",
                    subtitle = "Display current date below the clock",
                    icon = Icons.Default.CalendarToday,
                    checked = preferences.showDate,
                    testTag = "toggle_show_date",
                    onCheckedChange = onToggleShowDate
                )
                HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                SettingsToggleRow(
                    title = "Show Day of Week",
                    subtitle = "Include day abbreviation (e.g. MON, TUE)",
                    icon = Icons.Default.CalendarMonth,
                    checked = preferences.showDayOfWeek,
                    testTag = "toggle_show_day_of_week",
                    onCheckedChange = onToggleShowDayOfWeek
                )
            }

            if (preferences.showDate) {
                SettingsSectionHeader(title = "Date Pattern Style")
                SettingsCard {
                    DateFormatOption.entries.forEachIndexed { index, option ->
                        val selected = preferences.dateFormatOption == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.RadioButton,
                                    onClick = { onSelectDateFormat(option) }
                                )
                                .padding(horizontal = 16.dp, vertical = 13.dp)
                                .testTag("date_format_${option.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    color = if (selected) Color.White else Color(0xFFCACACE)
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = FocusAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (index < DateFormatOption.entries.size - 1) {
                            HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}
