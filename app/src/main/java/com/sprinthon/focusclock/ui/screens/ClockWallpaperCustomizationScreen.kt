package com.sprinthon.focusclock.ui.screens

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.domain.model.ClockAlignment
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.WallpaperBackgroundType
import com.sprinthon.focusclock.domain.model.WallpaperClockPosition
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.playback.FocusClockWallpaperService
import com.sprinthon.focusclock.playback.WallpaperBitmapRenderer
import com.sprinthon.focusclock.ui.clock.ClockFont
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.ClockTimeData
import com.sprinthon.focusclock.ui.theme.FocusAmber
import kotlin.math.roundToInt

enum class PreviewSimulationMode(val displayName: String, val icon: ImageVector) {
    CLEAN("Clean", Icons.Outlined.Visibility),
    HOME("Home Screen", Icons.Outlined.Home),
    LOCK("Lock Screen", Icons.Outlined.Lock)
}

enum class WallpaperDeckTab(val title: String, val icon: ImageVector) {
    POSITION("Position", Icons.Default.OpenWith),
    STYLE("Style", Icons.Default.Style),
    BACKGROUND("Background", Icons.Default.Palette),
    CONTENT("Content", Icons.Default.Widgets)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClockWallpaperCustomizationScreen(
    wallpaperConfig: WallpaperConfig,
    currentTimeData: ClockTimeData,
    onUpdatePosition: (WallpaperClockPosition) -> Unit,
    onUpdateStyle: (ClockStyle) -> Unit,
    onUpdateFont: (ClockFont) -> Unit,
    onUpdateColor: (Long) -> Unit,
    onUpdateAnalogOrientation: (AnalogNumeralOrientation) -> Unit,
    onUpdateAnalogNumeralSize: (com.sprinthon.focusclock.domain.model.AnalogNumeralSize) -> Unit = {},
    onUpdateAnalogNumeralScale: (Float) -> Unit = {},
    onUpdateBackgroundType: (WallpaperBackgroundType) -> Unit,
    onUpdateBackgroundColor: (Long) -> Unit,
    onUpdateBackgroundImageUri: (String?) -> Unit,
    onUpdateScrimOpacity: (Float) -> Unit,
    onUpdateBlurRadius: (Int) -> Unit,
    onUpdateShowDate: (Boolean) -> Unit,
    onUpdateShowSeconds: (Boolean) -> Unit,
    onUpdateMotto: (Boolean, String) -> Unit,
    onUpdateShowStreak: (Boolean) -> Unit,
    onUpdate24Hour: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedSimulationMode by remember { mutableStateOf(PreviewSimulationMode.CLEAN) }
    var selectedDeckTab by remember { mutableStateOf(WallpaperDeckTab.POSITION) }
    var isDeckExpanded by remember { mutableStateOf(true) }
    var showApplyDialog by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some content providers might not support persistable permissions
            }
            onUpdateBackgroundImageUri(uri.toString())
            onUpdateBackgroundType(WallpaperBackgroundType.GALLERY_IMAGE)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallpaper Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("wallpaper_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Apply Wallpaper Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showApplyDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("apply_wallpaper_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.65f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // -------------------------------------------------------------
            // 1. WYSIWYG WALLPAPER CANVAS & DRAG VIEWPORT
            // -------------------------------------------------------------
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("wallpaper_drag_canvas")
            ) {
                val boxWidth = constraints.maxWidth.toFloat()
                val boxHeight = constraints.maxHeight.toFloat()

                var isDragging by remember { mutableStateOf(false) }
                var showHorizontalSnapLine by remember { mutableStateOf(false) }
                var showVerticalSnapLine by remember { mutableStateOf(false) }

                // Background Rendering & Tap-to-minimize Immersion Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isDeckExpanded) {
                                isDeckExpanded = false
                            }
                        }
                ) {
                    if (wallpaperConfig.backgroundType == WallpaperBackgroundType.GALLERY_IMAGE && !wallpaperConfig.backgroundImageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(wallpaperConfig.backgroundImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Wallpaper Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (wallpaperConfig.blurRadius > 0) {
                                        Modifier.blur(wallpaperConfig.blurRadius.dp)
                                    } else Modifier
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(wallpaperConfig.backgroundColorHex))
                        )
                    }

                    // Scrim Tint
                    if (wallpaperConfig.scrimOpacity > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = wallpaperConfig.scrimOpacity))
                        )
                    }
                }

                // Snap Guide Lines
                if (isDragging) {
                    if (showVerticalSnapLine) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxSize()
                                .align(Alignment.Center)
                                .background(FocusAmber.copy(alpha = 0.8f))
                        )
                    }
                    if (showHorizontalSnapLine) {
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .background(FocusAmber.copy(alpha = 0.8f))
                        )
                    }
                }

                // ---------------------------------------------------------
                // DRAGGABLE CLOCK CONTAINER
                // ---------------------------------------------------------
                val xOffsetPx = (wallpaperConfig.position.xPercent * (boxWidth / 2f)).roundToInt()
                val yOffsetPx = (wallpaperConfig.position.yPercent * (boxHeight / 2f)).roundToInt()

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(xOffsetPx, yOffsetPx) }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                },
                                onDragEnd = {
                                    isDragging = false
                                    showHorizontalSnapLine = false
                                    showVerticalSnapLine = false
                                },
                                onDragCancel = {
                                    isDragging = false
                                    showHorizontalSnapLine = false
                                    showVerticalSnapLine = false
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newXOffsetPx = xOffsetPx + dragAmount.x
                                    val newYOffsetPx = yOffsetPx + dragAmount.y

                                    var newXPercent = (newXOffsetPx / (boxWidth / 2f)).coerceIn(-1.0f, 1.0f)
                                    var newYPercent = (newYOffsetPx / (boxHeight / 2f)).coerceIn(-1.0f, 1.0f)

                                    // Snap check
                                    val xSnapped = kotlin.math.abs(newXPercent) < 0.04f
                                    val ySnapped = kotlin.math.abs(newYPercent) < 0.04f

                                    if (xSnapped) {
                                        if (!showVerticalSnapLine) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        newXPercent = 0.0f
                                    }
                                    showVerticalSnapLine = xSnapped

                                    if (ySnapped) {
                                        if (!showHorizontalSnapLine) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        newYPercent = 0.0f
                                    }
                                    showHorizontalSnapLine = ySnapped

                                    onUpdatePosition(
                                        wallpaperConfig.position.copy(
                                            xPercent = newXPercent,
                                            yPercent = newYPercent,
                                            alignment = ClockAlignment.CUSTOM
                                        )
                                    )
                                }
                            )
                        }
                        .testTag("clock_draggable_target"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Clock Rendering
                        ClockRenderer(
                            timeData = currentTimeData,
                            style = wallpaperConfig.clockStyle,
                            clockFont = wallpaperConfig.clockFont,
                            primaryColor = Color(wallpaperConfig.clockColorHex),
                            numeralOrientation = wallpaperConfig.analogNumeralOrientation,
                            analogNumeralSize = wallpaperConfig.analogNumeralSize,
                            analogNumeralScale = wallpaperConfig.analogNumeralScale,
                            showSeconds = wallpaperConfig.showSeconds,
                            scale = wallpaperConfig.position.scale,
                            showDate = wallpaperConfig.showDate,
                            showDayOfWeek = true
                        )

                        // Custom Motto Overlay
                        if (wallpaperConfig.showMotto && wallpaperConfig.customMotto.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = wallpaperConfig.customMotto,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(wallpaperConfig.clockColorHex).copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Focus Streak Badge Overlay
                        if (wallpaperConfig.showFocusStreak) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color.Black.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "🔥 5 Day Focus Streak",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusAmber,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // ---------------------------------------------------------
                // 2. SIMULATION OVERLAYS (Home / Lock Screen Mockups)
                // ---------------------------------------------------------
                when (selectedSimulationMode) {
                    PreviewSimulationMode.HOME -> {
                        // Mock Home Screen Grid Overlay
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Mock Search Bar Near Top
                            Surface(
                                color = Color.White.copy(alpha = 0.20f),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.88f)
                                    .height(44.dp)
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Search apps...", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                                }
                            }

                            // Mock Launcher Dock at Bottom
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 120.dp), // Clear bottom deck
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(4) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.25f))
                                    )
                                }
                            }
                        }
                    }
                    PreviewSimulationMode.LOCK -> {
                        // Mock Lock Screen Keyguard Overlay
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Lock Icon Top
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp)
                            )

                            // Mock Notification Card
                            Surface(
                                color = Color.Black.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.90f)
                                    .align(Alignment.Center)
                                    .offset(y = 120.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(FocusAmber)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Focus Clock", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text("Deep Focus session in progress • 25m left", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                            }

                            // Bottom Shortcuts
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 24.dp, vertical = 130.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = Color.White)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                    PreviewSimulationMode.CLEAN -> { /* Unobstructed preview */ }
                }

                // Simulation Mode Switcher Pills Floating at Top Center
                Surface(
                    color = Color.Black.copy(alpha = 0.60f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .testTag("simulation_mode_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PreviewSimulationMode.values().forEach { mode ->
                            val isSelected = selectedSimulationMode == mode
                            Surface(
                                color = if (isSelected) FocusAmber else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clickable { selectedSimulationMode = mode }
                                    .testTag("sim_mode_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = mode.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. FLOATING BOTTOM CUSTOMIZATION CONTROL DECK
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                if (!isDeckExpanded) {
                    // Minimized Floating Pill Handle
                    Surface(
                        color = Color(0xFF161618),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2E)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .clickable { isDeckExpanded = true }
                            .testTag("wallpaper_deck_expand_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = FocusAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Customize Wallpaper",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expand",
                                tint = FocusAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    // Expanded Deck Header & Controls
                    Surface(
                        color = Color(0xFF161618),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Integrated Compact Header Bar (24dp height footprint)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // Left: Studio Tag
                                Text(
                                    text = "INSPECTOR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )

                                // Center: Tactile Drag Indicator Pill
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .width(36.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White.copy(alpha = 0.35f))
                                        .clickable { isDeckExpanded = false }
                                )

                                // Right: Subtle Chevron Collapse Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { isDeckExpanded = false }
                                        .testTag("wallpaper_deck_minimize_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Minimize",
                                        tint = FocusAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Compact Pill Tabs Row (Single Line, No Wrapping)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WallpaperDeckTab.values().forEach { tab ->
                                    val isSelected = selectedDeckTab == tab
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) FocusAmber else Color(0xFF222226),
                                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E34)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable {
                                                selectedDeckTab = tab
                                                isDeckExpanded = true
                                            }
                                            .testTag("wallpaper_tab_${tab.name.lowercase()}")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = tab.title,
                                                maxLines = 1,
                                                softWrap = false,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Deck Content Panel (Compact 175dp height footprint)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp)
                                    .background(Color(0xFF161618))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    when (selectedDeckTab) {
                                        WallpaperDeckTab.POSITION -> {
                                            PositionControlsTab(
                                                position = wallpaperConfig.position,
                                                onUpdatePosition = onUpdatePosition
                                            )
                                        }
                                        WallpaperDeckTab.STYLE -> {
                                            ClockStyleControlsTab(
                                                config = wallpaperConfig,
                                                onUpdateStyle = onUpdateStyle,
                                                onUpdateFont = onUpdateFont,
                                                onUpdateColor = onUpdateColor,
                                                onUpdateAnalogOrientation = onUpdateAnalogOrientation,
                                                onUpdateAnalogNumeralSize = onUpdateAnalogNumeralSize,
                                                onUpdateAnalogNumeralScale = onUpdateAnalogNumeralScale,
                                                onUpdateShowSeconds = onUpdateShowSeconds,
                                                onUpdate24Hour = onUpdate24Hour
                                            )
                                        }
                                        WallpaperDeckTab.BACKGROUND -> {
                                            BackgroundControlsTab(
                                                config = wallpaperConfig,
                                                onUpdateBackgroundType = onUpdateBackgroundType,
                                                onUpdateBackgroundColor = onUpdateBackgroundColor,
                                                onUpdateScrimOpacity = onUpdateScrimOpacity,
                                                onUpdateBlurRadius = onUpdateBlurRadius,
                                                onPickPhoto = {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                onClearPhoto = { onUpdateBackgroundImageUri(null) }
                                            )
                                        }
                                        WallpaperDeckTab.CONTENT -> {
                                            ContentControlsTab(
                                                config = wallpaperConfig,
                                                onUpdateShowDate = onUpdateShowDate,
                                                onUpdateMotto = onUpdateMotto,
                                                onUpdateShowStreak = onUpdateShowStreak
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Apply Wallpaper Dialog
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        tint = FocusAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Apply Wallpaper",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose where to activate your customized Focus Clock wallpaper:",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )

                    // Home Screen
                    Surface(
                        color = Color(0xFF27272A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showApplyDialog = false
                                val metrics = context.resources.displayMetrics
                                val bitmap = WallpaperBitmapRenderer.renderWallpaperBitmap(
                                    context,
                                    wallpaperConfig,
                                    currentTimeData,
                                    metrics.widthPixels,
                                    metrics.heightPixels
                                )
                                val success = WallpaperBitmapRenderer.applyWallpaper(
                                    context,
                                    bitmap,
                                    WallpaperManager.FLAG_SYSTEM
                                )
                                if (success) {
                                    Toast.makeText(context, "Home screen wallpaper applied!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to apply wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = FocusAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Home Screen", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Apply static clock image to Home Screen", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Lock Screen
                    Surface(
                        color = Color(0xFF27272A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showApplyDialog = false
                                val metrics = context.resources.displayMetrics
                                val bitmap = WallpaperBitmapRenderer.renderWallpaperBitmap(
                                    context,
                                    wallpaperConfig,
                                    currentTimeData,
                                    metrics.widthPixels,
                                    metrics.heightPixels
                                )
                                val success = WallpaperBitmapRenderer.applyWallpaper(
                                    context,
                                    bitmap,
                                    WallpaperManager.FLAG_LOCK
                                )
                                if (success) {
                                    Toast.makeText(context, "Lock screen wallpaper applied!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to apply lock screen wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = FocusAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Lock Screen", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Apply static clock image to Lock Screen", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Both Home & Lock Screen
                    Surface(
                        color = Color(0xFF27272A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showApplyDialog = false
                                val metrics = context.resources.displayMetrics
                                val bitmap = WallpaperBitmapRenderer.renderWallpaperBitmap(
                                    context,
                                    wallpaperConfig,
                                    currentTimeData,
                                    metrics.widthPixels,
                                    metrics.heightPixels
                                )
                                val success = WallpaperBitmapRenderer.applyWallpaper(
                                    context,
                                    bitmap,
                                    WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                                )
                                if (success) {
                                    Toast.makeText(context, "Applied to Home & Lock Screen!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to apply wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = FocusAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Both Home & Lock Screen", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Apply static wallpaper to both screens", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Live Wallpaper
                    Surface(
                        color = Color(0xFF27272A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showApplyDialog = false
                                try {
                                    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                                        putExtra(
                                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                            ComponentName(context, FocusClockWallpaperService::class.java)
                                        )
                                    }
                                    context.startActivity(intent)
                                    Toast.makeText(context, "Opening Live Wallpaper chooser...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Live Wallpaper setup: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = FocusAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Interactive Live Wallpaper", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Live updating clock service", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            },
            containerColor = Color(0xFF1C1C1E)
        )
    }
}

// -------------------------------------------------------------------------
// TAB 1: POSITION CONTROLS (COMPACT FIGMA-STYLE 3x3 MATRIX & INLINE SLIDERS)
// -------------------------------------------------------------------------
@Composable
private fun PositionControlsTab(
    position: WallpaperClockPosition,
    onUpdatePosition: (WallpaperClockPosition) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Alignment Row: Visual 3x3 Matrix + Preset Info & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Alignment Anchor",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (position.alignment == ClockAlignment.CUSTOM) "Custom coordinates" else position.alignment.displayName,
                    fontSize = 11.sp,
                    color = FocusAmber
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tactile 3x3 Dot/Square Matrix (Figma / Canva style)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF222226),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E34)),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val matrix = listOf(
                            listOf(ClockAlignment.TOP_START, ClockAlignment.TOP_CENTER, ClockAlignment.TOP_END),
                            listOf(ClockAlignment.CENTER_START, ClockAlignment.CENTER, ClockAlignment.CENTER_END),
                            listOf(ClockAlignment.BOTTOM_START, ClockAlignment.BOTTOM_CENTER, ClockAlignment.BOTTOM_END)
                        )

                        matrix.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                row.forEach { align ->
                                    val isSelected = position.alignment == align
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (isSelected) FocusAmber else Color.White.copy(alpha = 0.22f)
                                            )
                                            .clickable {
                                                onUpdatePosition(
                                                    position.copy(
                                                        xPercent = align.xPercent,
                                                        yPercent = align.yPercent,
                                                        alignment = align
                                                    )
                                                )
                                            }
                                            .testTag("alignment_${align.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Reset Button
                IconButton(
                    onClick = {
                        onUpdatePosition(WallpaperClockPosition.DEFAULT)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF27272A))
                        .testTag("reset_position_button")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset Position",
                        tint = FocusAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Inline Compact Sliders (Label + Slider + Numerical Value on single rows)
        // 1. Vertical Offset Slider (Y-Axis)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Y Offset",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                modifier = Modifier.width(54.dp)
            )
            Slider(
                value = position.yPercent,
                onValueChange = { y ->
                    onUpdatePosition(
                        position.copy(yPercent = y, alignment = ClockAlignment.CUSTOM)
                    )
                },
                valueRange = -0.90f..0.90f,
                colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .testTag("slider_y_offset")
            )
            Text(
                text = "${(position.yPercent * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FocusAmber,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }

        // 2. Horizontal Offset Slider (X-Axis)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "X Offset",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                modifier = Modifier.width(54.dp)
            )
            Slider(
                value = position.xPercent,
                onValueChange = { x ->
                    onUpdatePosition(
                        position.copy(xPercent = x, alignment = ClockAlignment.CUSTOM)
                    )
                },
                valueRange = -0.85f..0.85f,
                colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .testTag("slider_x_offset")
            )
            Text(
                text = "${(position.xPercent * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FocusAmber,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }

        // 3. Clock Scale Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Scale",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                modifier = Modifier.width(54.dp)
            )
            Slider(
                value = position.scale,
                onValueChange = { s ->
                    onUpdatePosition(position.copy(scale = s))
                },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .testTag("slider_clock_scale")
            )
            Text(
                text = String.format("%.2fx", position.scale),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FocusAmber,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

// -------------------------------------------------------------------------
// TAB 2: CLOCK STYLE & NUMERAL ORIENTATION CONTROLS (COMPACT CAROUSELS & SWATCHES)
// -------------------------------------------------------------------------
@Composable
private fun ClockStyleControlsTab(
    config: WallpaperConfig,
    onUpdateStyle: (ClockStyle) -> Unit,
    onUpdateFont: (ClockFont) -> Unit,
    onUpdateColor: (Long) -> Unit,
    onUpdateAnalogOrientation: (AnalogNumeralOrientation) -> Unit,
    onUpdateAnalogNumeralSize: (com.sprinthon.focusclock.domain.model.AnalogNumeralSize) -> Unit = {},
    onUpdateAnalogNumeralScale: (Float) -> Unit = {},
    onUpdateShowSeconds: (Boolean) -> Unit,
    onUpdate24Hour: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Clock Style Selector Row
        Text(
            text = "Renderer Style",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ClockStyle.values().forEach { style ->
                val isSelected = config.clockStyle == style
                Surface(
                    color = if (isSelected) FocusAmber else Color(0xFF222226),
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E34)),
                    modifier = Modifier
                        .clickable { onUpdateStyle(style) }
                        .testTag("style_${style.name.lowercase()}")
                ) {
                    Text(
                        text = style.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Analog Numeral Orientation Options (When Analog style active - Compact Segmented Selector)
        if (config.clockStyle == ClockStyle.ANALOG) {
            Text(
                text = "Analog Dial Mode",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AnalogNumeralOrientation.values().forEach { orientation ->
                    val isSelected = config.analogNumeralOrientation == orientation
                    Surface(
                        color = if (isSelected) Color(0xFF2A2A30) else Color(0xFF1E1E22),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FocusAmber else Color(0xFF2E2E34)
                        ),
                        modifier = Modifier
                            .clickable { onUpdateAnalogOrientation(orientation) }
                            .testTag("orientation_${orientation.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) FocusAmber else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = orientation.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Analog Numeral Sizing Presets (Normal, Large, Extra Large, Cardinal)
            Text(
                text = "Dial Numeral Size",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                com.sprinthon.focusclock.domain.model.AnalogNumeralSize.values().forEach { sizeOption ->
                    val isSizeSelected = config.analogNumeralSize == sizeOption
                    Surface(
                        color = if (isSizeSelected) Color(0xFF2A2A30) else Color(0xFF1E1E22),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSizeSelected) FocusAmber else Color(0xFF2E2E34)
                        ),
                        modifier = Modifier
                            .clickable {
                                onUpdateAnalogNumeralSize(sizeOption)
                                onUpdateAnalogNumeralScale(sizeOption.scale)
                            }
                            .testTag("wallpaper_numeral_size_${sizeOption.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSizeSelected) FocusAmber else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sizeOption.displayName,
                                fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSizeSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Fine-Tuning Numeral Scale Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Numeral Scale",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray,
                    modifier = Modifier.width(80.dp)
                )
                Slider(
                    value = config.analogNumeralScale,
                    onValueChange = onUpdateAnalogNumeralScale,
                    valueRange = 0.80f..2.00f,
                    colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .testTag("slider_wallpaper_numeral_scale")
                )
                Text(
                    text = String.format("%.2fx", config.analogNumeralScale),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusAmber,
                    modifier = Modifier.width(38.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }

        // Font Family Selector (Compact horizontal pill chips)
        Text(
            text = "Typography Font",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ClockFont.values().forEach { font ->
                val isSelected = config.clockFont == font
                Surface(
                    color = if (isSelected) FocusAmber else Color(0xFF222226),
                    shape = RoundedCornerShape(14.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E34)),
                    modifier = Modifier
                        .clickable { onUpdateFont(font) }
                        .testTag("font_${font.name.lowercase()}")
                ) {
                    Text(
                        text = font.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Color Palette Swatches (Sleek 28dp color dots with selection rings)
        Text(
            text = "Text & Hand Accent",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        val colors = listOf(
            0xFFFFFFFFL to "Pure White",
            0xFFFFB703L to "Focus Amber",
            0xFF94A3B8L to "Slate Gray",
            0xFF38BDF8L to "Sky Cyan",
            0xFF34D399L to "Emerald Green",
            0xFFF43F5EL to "Rose Coral",
            0xFFC084FCL to "Soft Violet"
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            colors.forEach { (hex, _) ->
                val isSelected = config.clockColorHex == hex
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(hex))
                        .border(
                            2.dp,
                            if (isSelected) FocusAmber else Color.White.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .clickable { onUpdateColor(hex) }
                        .testTag("clock_color_${hex}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (hex == 0xFFFFFFFFL || hex == 0xFFFFB703L) Color.Black else Color.White)
                        )
                    }
                }
            }
        }

        // Inline Seconds Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Seconds Hand / Digits",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = config.showSeconds,
                onCheckedChange = onUpdateShowSeconds,
                colors = SwitchDefaults.colors(checkedThumbColor = FocusAmber, checkedTrackColor = FocusAmber.copy(alpha = 0.5f)),
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------
// TAB 3: BACKGROUND CONTROLS (COMPACT SEGMENTED PILL SELECTOR)
// -------------------------------------------------------------------------
@Composable
private fun BackgroundControlsTab(
    config: WallpaperConfig,
    onUpdateBackgroundType: (WallpaperBackgroundType) -> Unit,
    onUpdateBackgroundColor: (Long) -> Unit,
    onUpdateScrimOpacity: (Float) -> Unit,
    onUpdateBlurRadius: (Int) -> Unit,
    onPickPhoto: () -> Unit,
    onClearPhoto: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Compact 3-Option Segmented Pill Selector
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF222226),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E34)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(2.dp)) {
                WallpaperBackgroundType.values().forEach { type ->
                    val isSelected = config.backgroundType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) FocusAmber else Color.Transparent)
                            .clickable { onUpdateBackgroundType(type) }
                            .padding(vertical = 6.dp)
                            .testTag("bg_type_${type.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (config.backgroundType == WallpaperBackgroundType.SOLID_COLOR) {
            Text(
                text = "Solid AMOLED Color",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            val solidColors = listOf(
                0xFF000000L to "AMOLED Black",
                0xFF121212L to "Charcoal Dark",
                0xFF0F172AL to "Midnight Navy",
                0xFF052E16L to "Deep Forest",
                0xFF1E1B4BL to "Deep Indigo",
                0xFF18181BL to "Slate Gray"
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                solidColors.forEach { (hex, _) ->
                    val isSelected = config.backgroundColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(hex))
                            .border(2.dp, if (isSelected) FocusAmber else Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { onUpdateBackgroundColor(hex) }
                            .testTag("solid_bg_${hex}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FocusAmber)
                            )
                        }
                    }
                }
            }
        } else {
            // Custom Photo Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPickPhoto,
                    colors = ButtonDefaults.buttonColors(containerColor = FocusAmber),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("pick_photo_button")
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Photo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                if (!config.backgroundImageUri.isNullOrEmpty()) {
                    IconButton(
                        onClick = onClearPhoto,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear Photo",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Blur Radius Inline Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Blur",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray,
                    modifier = Modifier.width(48.dp)
                )
                Slider(
                    value = config.blurRadius.toFloat(),
                    onValueChange = { b -> onUpdateBlurRadius(b.roundToInt()) },
                    valueRange = 0f..25f,
                    colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .testTag("slider_blur_radius")
                )
                Text(
                    text = "${config.blurRadius}dp",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusAmber,
                    modifier = Modifier.width(36.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }

        // Dark Scrim Overlay Inline Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Scrim",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                modifier = Modifier.width(48.dp)
            )
            Slider(
                value = config.scrimOpacity,
                onValueChange = onUpdateScrimOpacity,
                valueRange = 0.0f..0.85f,
                colors = SliderDefaults.colors(thumbColor = FocusAmber, activeTrackColor = FocusAmber),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .testTag("slider_scrim_opacity")
            )
            Text(
                text = "${(config.scrimOpacity * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FocusAmber,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

// -------------------------------------------------------------------------
// TAB 4: CONTENT & OVERLAY CONTROLS (COMPACT TOGGLES & INLINE INPUT)
// -------------------------------------------------------------------------
@Composable
private fun ContentControlsTab(
    config: WallpaperConfig,
    onUpdateShowDate: (Boolean) -> Unit,
    onUpdateMotto: (Boolean, String) -> Unit,
    onUpdateShowStreak: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Content & Badges",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // 1. Current Date Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Display Current Date", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Switch(
                checked = config.showDate,
                onCheckedChange = onUpdateShowDate,
                colors = SwitchDefaults.colors(checkedThumbColor = FocusAmber, checkedTrackColor = FocusAmber.copy(alpha = 0.5f)),
                modifier = Modifier.height(24.dp)
            )
        }

        // 2. Focus Streak Badge Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Display Focus Streak Badge", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Switch(
                checked = config.showFocusStreak,
                onCheckedChange = onUpdateShowStreak,
                colors = SwitchDefaults.colors(checkedThumbColor = FocusAmber, checkedTrackColor = FocusAmber.copy(alpha = 0.5f)),
                modifier = Modifier.height(24.dp)
            )
        }

        // 3. Custom Motto Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Display Custom Motto Line", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Switch(
                checked = config.showMotto,
                onCheckedChange = { checked -> onUpdateMotto(checked, config.customMotto) },
                colors = SwitchDefaults.colors(checkedThumbColor = FocusAmber, checkedTrackColor = FocusAmber.copy(alpha = 0.5f)),
                modifier = Modifier.height(24.dp)
            )
        }

        // Contextual Inline Motto Input
        if (config.showMotto) {
            OutlinedTextField(
                value = config.customMotto,
                onValueChange = { newMotto -> onUpdateMotto(true, newMotto) },
                label = { Text("Personal Motto / Goal", fontSize = 11.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FocusAmber,
                    unfocusedBorderColor = Color(0xFF2E2E34),
                    focusedLabelColor = FocusAmber,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_motto_input")
            )
        }
    }
}
