package com.sprinthon.focusclock.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.CuratedColor
import com.sprinthon.focusclock.domain.model.CuratedColors
import com.sprinthon.focusclock.domain.model.DateFormatOption
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.ui.clock.ClockFont
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.components.FocusBackground
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber
import com.sprinthon.focusclock.ui.theme.rememberCanvasContrastPalette

enum class CustomizerTab(val title: String, val icon: ImageVector) {
    CLOCK_STYLE("Dial", Icons.Outlined.Schedule),
    TYPOGRAPHY("Font", Icons.Outlined.TextFields),
    BACKGROUND("Canvas", Icons.Outlined.Palette),
    DATE_AND_DIM("Date & Dim", Icons.Outlined.BrightnessMedium)
}

/**
 * Dedicated, studio-grade screen consolidating ALL visual focus clock and atmosphere customization:
 * 1. Dial Style (Clean Digital, Minimal, Flip Clock, Analog)
 * 2. Typography & Google Fonts (23+ tall, condensed display fonts)
 * 3. Background Engine (Solid Color with AMOLED Black/Curated/Hex, Single Photo, Slideshow)
 * 4. Readability Dimming & Date/Time formats (0%-70% contrast protection, 12/24h format, date formats)
 *
 * Features a Sticky Synchronized Live Hero Preview at the top for real-time visual feedback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockCanvasSettingsScreen(
    preferences: FocusPreferences,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectClockFont: (ClockFont) -> Unit,
    onSelectBackgroundType: (BackgroundType) -> Unit,
    onSelectSolidColor: (Long) -> Unit,
    onSelectSingleImage: (String?) -> Unit,
    onAddSlideshowImages: (List<String>) -> Unit,
    onRemoveSlideshowImage: (String) -> Unit,
    onSelectSlideshowInterval: (SlideshowInterval) -> Unit,
    onToggleSlideshowShuffle: (Boolean) -> Unit,
    onSelectOverlayStrength: (Float) -> Unit,
    onToggle24Hour: (Boolean) -> Unit,
    onToggleShowDate: (Boolean) -> Unit,
    onToggleShowDayOfWeek: (Boolean) -> Unit,
    onSelectDateFormat: (DateFormatOption) -> Unit,
    onSelectClockScale: (Float) -> Unit = {},
    onSelectAnalogNumeralSize: (com.sprinthon.focusclock.domain.model.AnalogNumeralSize) -> Unit = {},
    onSelectAnalogNumeralScale: (Float) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: CustomizerTab = CustomizerTab.CLOCK_STYLE
) {
    val context = LocalContext.current
    val timeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabIndex = CustomizerTab.entries.indexOf(selectedTab)

    var hexInputValue by remember { mutableStateOf("") }
    var hexInputError by remember { mutableStateOf(false) }

    // Single photo picker launcher with persistable URI permission
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Provider may not support persistable permissions
            }
            onSelectSingleImage(uri.toString())
            onSelectBackgroundType(BackgroundType.SINGLE_IMAGE)
        }
    }

    // Multiple photo picker launcher for slideshow
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val uriStrings = uris.map { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Safe catch
                }
                uri.toString()
            }
            onAddSlideshowImages(uriStrings)
            onSelectBackgroundType(BackgroundType.SLIDESHOW)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Clock & Canvas",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${preferences.clockStyle.displayName} · ${preferences.clockFont.displayName}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = FocusAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("clock_canvas_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmoledBlack
                )
            )
        },
        containerColor = AmoledBlack,
        modifier = modifier
            .fillMaxSize()
            .testTag("clock_canvas_customizer_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. PINNED / STICKY LIVE SYNCHRONIZED HERO PREVIEW
            // ==========================================
            val previewContrastPalette = rememberCanvasContrastPalette(preferences)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .widthIn(max = 600.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF050507),
                    border = BorderStroke(1.dp, Color(0xFF262630)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .testTag("customizer_hero_live_preview")
                    ) {
                        // Background Layer (renders real background, photo, or slideshow)
                        FocusBackground(
                            preferences = preferences,
                            isInteractivePreview = true,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Clock Layer (renders exact clock style, font, date, and adaptive contrast)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ClockRenderer(
                                style = preferences.clockStyle,
                                timeData = timeData,
                                primaryColor = previewContrastPalette.primaryText,
                                secondaryColor = previewContrastPalette.secondaryText,
                                accentColor = previewContrastPalette.accentColor,
                                cardBackground = previewContrastPalette.cardBackground,
                                cardBorder = previewContrastPalette.cardBorder,
                                cardDivider = previewContrastPalette.cardDivider,
                                clockFont = preferences.clockFont,
                                showDate = preferences.showDate,
                                showDayOfWeek = preferences.showDayOfWeek,
                                scale = 0.70f * preferences.clockScale,
                                analogNumeralSize = preferences.analogNumeralSize,
                                analogNumeralScale = preferences.analogNumeralScale,
                                isLandscape = false
                            )
                        }

                        // Live badge pill in top right corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(0.5.dp, FocusAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = FocusAmber
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 2. PRIMARY TAB ROW NAVIGATION
            // ==========================================
            PrimaryTabRow(
                selectedTabIndex = tabIndex,
                containerColor = AmoledBlack,
                contentColor = FocusAmber,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabIndex),
                        color = FocusAmber,
                        height = 3.dp
                    )
                },
                divider = {
                    HorizontalDivider(color = Color(0xFF1E1E24), thickness = 0.75.dp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("customizer_tab_row")
            ) {
                CustomizerTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) FocusAmber else Color(0xFF8E8E96)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) FocusAmber else Color(0xFF6E6E76)
                            )
                        },
                        modifier = Modifier.testTag("customizer_tab_${tab.name.lowercase()}")
                    )
                }
            }

            // ==========================================
            // 3. TAB CONTENT SECTION (Scrollable)
            // ==========================================
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(scrollState)
                ) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                        },
                        label = "tab_transition"
                    ) { currentTab ->
                        when (currentTab) {
                            CustomizerTab.CLOCK_STYLE -> {
                                ClockStyleTabContent(
                                    selectedStyle = preferences.clockStyle,
                                    timeData = timeData,
                                    clockFont = preferences.clockFont,
                                    clockScale = preferences.clockScale,
                                    analogNumeralSize = preferences.analogNumeralSize,
                                    analogNumeralScale = preferences.analogNumeralScale,
                                    onSelectClockStyle = onSelectClockStyle,
                                    onSelectClockScale = onSelectClockScale,
                                    onSelectAnalogNumeralSize = onSelectAnalogNumeralSize,
                                    onSelectAnalogNumeralScale = onSelectAnalogNumeralScale
                                )
                            }
                            CustomizerTab.TYPOGRAPHY -> {
                                TypographyTabContent(
                                    selectedFont = preferences.clockFont,
                                    onSelectClockFont = onSelectClockFont
                                )
                            }
                            CustomizerTab.BACKGROUND -> {
                                BackgroundTabContent(
                                    preferences = preferences,
                                    onSelectBackgroundType = onSelectBackgroundType,
                                    onSelectSolidColor = onSelectSolidColor,
                                    hexInputValue = hexInputValue,
                                    onHexInputValueChange = {
                                        hexInputValue = it
                                        hexInputError = false
                                    },
                                    hexInputError = hexInputError,
                                    onApplyHex = {
                                        val parsed = CuratedColors.parseHexColor(hexInputValue)
                                        if (parsed != null) {
                                            onSelectSolidColor(parsed)
                                            hexInputError = false
                                        } else {
                                            hexInputError = true
                                        }
                                    },
                                    onPickSinglePhoto = {
                                        singlePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onRemoveSinglePhoto = {
                                        onSelectSingleImage(null)
                                    },
                                    onAddSlideshowPhotos = {
                                        multiplePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onRemoveSlideshowPhoto = onRemoveSlideshowImage,
                                    onSelectSlideshowInterval = onSelectSlideshowInterval,
                                    onToggleSlideshowShuffle = onToggleSlideshowShuffle
                                )
                            }
                            CustomizerTab.DATE_AND_DIM -> {
                                DateAndDimTabContent(
                                    preferences = preferences,
                                    onSelectOverlayStrength = onSelectOverlayStrength,
                                    onToggle24Hour = onToggle24Hour,
                                    onToggleShowDate = onToggleShowDate,
                                    onToggleShowDayOfWeek = onToggleShowDayOfWeek,
                                    onSelectDateFormat = onSelectDateFormat
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

// ==========================================
// SUB-COMPONENTS FOR EACH TAB
// ==========================================

@Composable
private fun ClockStyleTabContent(
    selectedStyle: ClockStyle,
    timeData: com.sprinthon.focusclock.ui.clock.ClockTimeData,
    clockFont: ClockFont,
    clockScale: Float = 1.0f,
    analogNumeralSize: com.sprinthon.focusclock.domain.model.AnalogNumeralSize = com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
    analogNumeralScale: Float = 1.35f,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectClockScale: (Float) -> Unit = {},
    onSelectAnalogNumeralSize: (com.sprinthon.focusclock.domain.model.AnalogNumeralSize) -> Unit = {},
    onSelectAnalogNumeralScale: (Float) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SettingsSectionHeader(title = "Choose Clock Dial Style")

        ClockStyle.entries.chunked(2).forEach { rowStyles ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowStyles.forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        shape = RoundedCornerShape(16.dp),
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
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070709)),
                                contentAlignment = Alignment.Center
                            ) {
                                ClockRenderer(
                                    style = style,
                                    timeData = timeData,
                                    primaryColor = if (isSelected) FocusAmber else Color.White,
                                    secondaryColor = Color(0xFF7E7E88),
                                    accentColor = FocusAmber,
                                    clockFont = clockFont,
                                    showDate = false,
                                    showDayOfWeek = false,
                                    scale = 0.52f,
                                    analogNumeralSize = analogNumeralSize,
                                    analogNumeralScale = analogNumeralScale
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
                                        fontSize = 13.sp
                                    ),
                                    color = if (isSelected) FocusAmber else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // ANALOG NUMERAL SIZE & SCALE CONTROLS (When Analog active)
        // ==========================================
        if (selectedStyle == ClockStyle.ANALOG) {
            SettingsSectionHeader(title = "Analog Dial Numeral Size")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCardSurface)
                    .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 4 Numeral Size Option Cards in 2x2 Grid
                com.sprinthon.focusclock.domain.model.AnalogNumeralSize.entries.chunked(2).forEach { rowSizes ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowSizes.forEach { sizeOption ->
                            val isSizeSelected = analogNumeralSize == sizeOption
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSizeSelected) Color(0xFF241E15) else Color(0xFF16161A),
                                border = BorderStroke(
                                    if (isSizeSelected) 1.5.dp else 0.75.dp,
                                    if (isSizeSelected) FocusAmber else Color(0xFF26262E)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onSelectAnalogNumeralSize(sizeOption)
                                        onSelectAnalogNumeralScale(sizeOption.scale)
                                    }
                                    .testTag("numeral_size_${sizeOption.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSizeSelected) FocusAmber else Color(0xFF4A4A56))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = sizeOption.displayName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp
                                            ),
                                            color = if (isSizeSelected) FocusAmber else Color.White
                                        )
                                        Text(
                                            text = "${(sizeOption.scale * 100).toInt()}% font",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Fine-tune Numeral Scale Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Numeral Scale Fine-Tuning",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = String.format("%.2fx", analogNumeralScale),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FocusAmber,
                            modifier = Modifier.testTag("analog_numeral_scale_label")
                        )
                    }
                    Slider(
                        value = analogNumeralScale,
                        onValueChange = onSelectAnalogNumeralScale,
                        valueRange = 0.80f..2.00f,
                        colors = SliderDefaults.colors(
                            thumbColor = FocusAmber,
                            activeTrackColor = FocusAmber,
                            inactiveTrackColor = DarkElevatedSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_analog_numeral_scale")
                    )
                }
            }
        }

        // ==========================================
        // OVERALL CLOCK SCALE SLIDER (0.75x - 1.60x)
        // ==========================================
        SettingsSectionHeader(title = "Display Size & Scaling")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCardSurface)
                .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hero Clock Scale",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = String.format("%.2fx", clockScale),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusAmber,
                    modifier = Modifier.testTag("clock_scale_value_label")
                )
            }
            Slider(
                value = clockScale,
                onValueChange = onSelectClockScale,
                valueRange = 0.75f..1.60f,
                colors = SliderDefaults.colors(
                    thumbColor = FocusAmber,
                    activeTrackColor = FocusAmber,
                    inactiveTrackColor = DarkElevatedSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("slider_clock_scale_canvas")
            )
            Text(
                text = "Adjust the primary clock footprint across Focus sessions and Home hero view.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TypographyTabContent(
    selectedFont: ClockFont,
    onSelectClockFont: (ClockFont) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsSectionHeader(title = "Choose Font Typography")

        ClockFont.entries.forEach { font ->
            val isFontSelected = selectedFont == font
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
                        .padding(horizontal = 14.dp, vertical = 12.dp),
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

                    // Live sample box
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF08080A))
                            .border(
                                0.5.dp,
                                if (isFontSelected) FocusAmber.copy(alpha = 0.4f) else Color(0xFF202026),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "10:45",
                            style = TextStyle(
                                fontFamily = font.fontFamily,
                                fontWeight = font.defaultWeight,
                                letterSpacing = font.letterSpacing,
                                fontSize = 20.sp,
                                color = if (isFontSelected) FocusAmber else Color.White,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (isFontSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(FocusAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BackgroundTabContent(
    preferences: FocusPreferences,
    onSelectBackgroundType: (BackgroundType) -> Unit,
    onSelectSolidColor: (Long) -> Unit,
    hexInputValue: String,
    onHexInputValueChange: (String) -> Unit,
    hexInputError: Boolean,
    onApplyHex: () -> Unit,
    onPickSinglePhoto: () -> Unit,
    onRemoveSinglePhoto: () -> Unit,
    onAddSlideshowPhotos: () -> Unit,
    onRemoveSlideshowPhoto: (String) -> Unit,
    onSelectSlideshowInterval: (SlideshowInterval) -> Unit,
    onToggleSlideshowShuffle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsSectionHeader(title = "Select Background Mode")

        // Segmented Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackgroundTypeChip(
                label = "Solid Color",
                icon = Icons.Default.Palette,
                isSelected = preferences.backgroundType == BackgroundType.SOLID_COLOR,
                onClick = { onSelectBackgroundType(BackgroundType.SOLID_COLOR) },
                modifier = Modifier.weight(1f)
            )
            BackgroundTypeChip(
                label = "Photo",
                icon = Icons.Default.Image,
                isSelected = preferences.backgroundType == BackgroundType.SINGLE_IMAGE,
                onClick = { onSelectBackgroundType(BackgroundType.SINGLE_IMAGE) },
                modifier = Modifier.weight(1f)
            )
            BackgroundTypeChip(
                label = "Slideshow",
                icon = Icons.Outlined.PhotoLibrary,
                isSelected = preferences.backgroundType == BackgroundType.SLIDESHOW,
                onClick = { onSelectBackgroundType(BackgroundType.SLIDESHOW) },
                modifier = Modifier.weight(1f)
            )
        }

        when (preferences.backgroundType) {
            BackgroundType.SOLID_COLOR -> {
                SolidColorContent(
                    selectedColorHex = preferences.solidBackgroundColor,
                    onSelectColor = onSelectSolidColor,
                    hexInputValue = hexInputValue,
                    onHexInputValueChange = onHexInputValueChange,
                    hexInputError = hexInputError,
                    onApplyHex = onApplyHex
                )
            }
            BackgroundType.SINGLE_IMAGE -> {
                SingleImageContent(
                    imageUri = preferences.backgroundImageUri,
                    onPickPhoto = onPickSinglePhoto,
                    onRemovePhoto = onRemoveSinglePhoto
                )
            }
            BackgroundType.SLIDESHOW -> {
                SlideshowContent(
                    imageUris = preferences.slideshowImageUris,
                    interval = preferences.slideshowInterval,
                    shuffle = preferences.slideshowShuffle,
                    onAddPhotos = onAddSlideshowPhotos,
                    onRemovePhoto = onRemoveSlideshowPhoto,
                    onSelectInterval = onSelectSlideshowInterval,
                    onToggleShuffle = onToggleSlideshowShuffle
                )
            }
        }
    }
}

@Composable
private fun BackgroundTypeChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (isSelected) FocusAmber else DarkElevatedSurface
    val textColor = if (isSelected) Color.Black else Color.White
    val iconTint = if (isSelected) Color.Black else FocusAmber

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(
                if (isSelected) 1.dp else 0.5.dp,
                if (isSelected) FocusAmber else DarkOutline,
                shape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp)
            .minimumInteractiveComponentSize()
            .testTag("background_type_tab_${label.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolidColorContent(
    selectedColorHex: Long,
    onSelectColor: (Long) -> Unit,
    hexInputValue: String,
    onHexInputValueChange: (String) -> Unit,
    hexInputError: Boolean,
    onApplyHex: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardSurface)
            .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Curated AMOLED & Calm Palettes",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Neutrals
        Text(
            text = "NEUTRALS & AMOLED",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CuratedColors.Neutrals.forEach { colorItem ->
                ColorSwatch(
                    colorItem = colorItem,
                    isSelected = selectedColorHex == colorItem.hexValue,
                    onClick = { onSelectColor(colorItem.hexValue) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calm
        Text(
            text = "CALM ATMOSPHERES",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CuratedColors.Calm.forEach { colorItem ->
                ColorSwatch(
                    colorItem = colorItem,
                    isSelected = selectedColorHex == colorItem.hexValue,
                    onClick = { onSelectColor(colorItem.hexValue) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accents
        Text(
            text = "ACCENTS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CuratedColors.Accents.forEach { colorItem ->
                ColorSwatch(
                    colorItem = colorItem,
                    isSelected = selectedColorHex == colorItem.hexValue,
                    onClick = { onSelectColor(colorItem.hexValue) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom HEX Input
        Text(
            text = "Custom HEX Code",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = hexInputValue,
                onValueChange = onHexInputValueChange,
                placeholder = { Text("#101820", color = Color.Gray) },
                isError = hexInputError,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FocusAmber,
                    unfocusedBorderColor = DarkOutline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("custom_hex_input")
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onApplyHex,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FocusAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("apply_hex_button")
            ) {
                Text(
                    text = "Apply",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (hexInputError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Please enter a valid 6-character hex code (e.g. #101820)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ColorSwatch(
    colorItem: CuratedColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val swatchColor = Color(colorItem.hexValue)
    val isVeryLight = colorItem.hexValue == 0xFFF0F0F2

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(swatchColor)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) FocusAmber else Color(0xFF383842),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .testTag("color_swatch_${colorItem.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "${colorItem.name}, selected",
                tint = if (isVeryLight) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SingleImageContent(
    imageUri: String?,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardSurface)
            .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Photo Wallpaper",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!imageUri.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkElevatedSurface)
                    .border(0.5.dp, DarkOutline, RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Selected Photo Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.5.dp, DarkOutline, RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Selected Photo",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Active background wallpaper",
                            style = MaterialTheme.typography.bodySmall,
                            color = FocusAmber,
                            fontSize = 11.sp
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onPickPhoto,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("change_single_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Change photo",
                            tint = FocusAmber
                        )
                    }

                    IconButton(
                        onClick = onRemovePhoto,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("remove_single_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove photo",
                            tint = Color.Gray
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = onPickPhoto,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkElevatedSurface,
                    contentColor = FocusAmber
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, FocusAmber.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("choose_photo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = FocusAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Choose Photo from Gallery",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SlideshowContent(
    imageUris: List<String>,
    interval: SlideshowInterval,
    shuffle: Boolean,
    onAddPhotos: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    onSelectInterval: (SlideshowInterval) -> Unit,
    onToggleShuffle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardSurface)
            .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Slideshow Collection (${imageUris.size})",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            OutlinedButton(
                onClick = onAddPhotos,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.5.dp, FocusAmber),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = FocusAmber
                ),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("add_photos_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Photos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (imageUris.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkElevatedSurface)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No images selected",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        text = "Add photos to create a serene focus slideshow.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                imageUris.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(0.5.dp, DarkOutline, RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Slideshow Thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Delete button overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.75f))
                                .clickable { onRemovePhoto(uri) }
                                .testTag("remove_thumbnail_${uri.hashCode()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interval selector
        Text(
            text = "SLIDESHOW INTERVAL",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val intervals = listOf(
            SlideshowInterval.FIVE_SEC to "5s",
            SlideshowInterval.FIFTEEN_SEC to "15s",
            SlideshowInterval.THIRTY_SEC to "30s",
            SlideshowInterval.ONE_MIN to "1m",
            SlideshowInterval.FIVE_MIN to "5m",
            SlideshowInterval.TEN_MIN to "10m"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            intervals.forEach { (intOption, label) ->
                val isSelected = interval == intOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) FocusAmber else DarkElevatedSurface)
                        .border(
                            0.5.dp,
                            if (isSelected) FocusAmber else DarkOutline,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectInterval(intOption) }
                        .padding(vertical = 10.dp)
                        .testTag("interval_chip_${label.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shuffle Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkElevatedSurface)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    tint = if (shuffle) FocusAmber else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Shuffle Images",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Switch(
                checked = shuffle,
                onCheckedChange = onToggleShuffle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = FocusAmber,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = DarkCardSurface
                ),
                modifier = Modifier.testTag("slideshow_shuffle_switch")
            )
        }
    }
}

@Composable
private fun DateAndDimTabContent(
    preferences: FocusPreferences,
    onSelectOverlayStrength: (Float) -> Unit,
    onToggle24Hour: (Boolean) -> Unit,
    onToggleShowDate: (Boolean) -> Unit,
    onToggleShowDayOfWeek: (Boolean) -> Unit,
    onSelectDateFormat: (DateFormatOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Readability & Dimming Slider Card
        SettingsSectionHeader(title = "Contrast & Readability")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCardSurface)
                .border(0.5.dp, DarkOutline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Background Dimming",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                val percent = (preferences.backgroundOverlayStrength * 100).toInt()
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleSmall,
                    color = FocusAmber,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("dim_percentage_text")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = preferences.backgroundOverlayStrength,
                onValueChange = onSelectOverlayStrength,
                valueRange = 0f..0.70f,
                steps = 13,
                colors = SliderDefaults.colors(
                    thumbColor = FocusAmber,
                    activeTrackColor = FocusAmber,
                    inactiveTrackColor = DarkElevatedSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dim_slider")
            )

            Text(
                text = "Darkens background wallpapers and colors to guarantee maximum contrast and readability for the hero clock.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // Date & Time Formatting Card
        SettingsSectionHeader(title = "Time & Date Preferences")
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
            SettingsSectionHeader(title = "Date Pattern Format")
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
    }
}
