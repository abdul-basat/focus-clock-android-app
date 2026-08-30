package com.sprinthon.focusclock.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.CuratedColor
import com.sprinthon.focusclock.domain.model.CuratedColors
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.domain.model.SlideshowTransition
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.components.FocusBackground
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BackgroundSettingsScreen(
    preferences: FocusPreferences,
    onSelectBackgroundType: (BackgroundType) -> Unit,
    onSelectSolidColor: (Long) -> Unit,
    onSelectSingleImage: (String?) -> Unit,
    onAddSlideshowImages: (List<String>) -> Unit,
    onRemoveSlideshowImage: (String) -> Unit,
    onSelectSlideshowInterval: (SlideshowInterval) -> Unit,
    onToggleSlideshowShuffle: (Boolean) -> Unit,
    onSelectOverlayStrength: (Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val timeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)

    var hexInputValue by remember { mutableStateOf("") }
    var hexInputError by remember { mutableStateOf(false) }

    // Single photo picker launcher
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
                // Some content providers might not support persistable permissions
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
                    Text(
                        text = "Background Environment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("background_settings_back_button")
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
        containerColor = AmoledBlack,
        modifier = modifier
            .fillMaxSize()
            .testTag("background_settings_screen")
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
            Spacer(modifier = Modifier.height(10.dp))

            // LIVE REALISTIC PREVIEW FRAME
            SectionTitle(title = "LIVE PREVIEW", icon = Icons.Outlined.Wallpaper)

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, DarkOutline, RoundedCornerShape(16.dp))
                    .testTag("background_live_preview_box")
            ) {
                // 1. Background layer
                FocusBackground(
                    preferences = preferences,
                    isInteractivePreview = true,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Foreground mockup (Clock + Date + Timer)
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
                        scale = 0.55f,
                        showDate = preferences.showDate,
                        showDayOfWeek = preferences.showDayOfWeek,
                        isLandscape = false
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "REMAINING · 24:40",
                        style = MaterialTheme.typography.labelSmall,
                        color = FocusAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BACKGROUND TYPE SELECTOR
            SectionTitle(title = "BACKGROUND TYPE", icon = Icons.Outlined.PhotoLibrary)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BackgroundTypeTab(
                    label = "Solid Color",
                    isSelected = preferences.backgroundType == BackgroundType.SOLID_COLOR,
                    onClick = { onSelectBackgroundType(BackgroundType.SOLID_COLOR) },
                    modifier = Modifier.weight(1f)
                )
                BackgroundTypeTab(
                    label = "Photo",
                    isSelected = preferences.backgroundType == BackgroundType.SINGLE_IMAGE,
                    onClick = { onSelectBackgroundType(BackgroundType.SINGLE_IMAGE) },
                    modifier = Modifier.weight(1f)
                )
                BackgroundTypeTab(
                    label = "Slideshow",
                    isSelected = preferences.backgroundType == BackgroundType.SLIDESHOW,
                    onClick = { onSelectBackgroundType(BackgroundType.SLIDESHOW) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONDITIONAL CONTENT BASED ON SELECTED TYPE
            when (preferences.backgroundType) {
                BackgroundType.SOLID_COLOR -> {
                    SolidColorSection(
                        selectedColorHex = preferences.solidBackgroundColor,
                        onSelectColor = onSelectSolidColor,
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
                        }
                    )
                }
                BackgroundType.SINGLE_IMAGE -> {
                    SingleImageSection(
                        imageUri = preferences.backgroundImageUri,
                        onPickPhoto = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemovePhoto = {
                            onSelectSingleImage(null)
                        }
                    )
                }
                BackgroundType.SLIDESHOW -> {
                    SlideshowSection(
                        imageUris = preferences.slideshowImageUris,
                        interval = preferences.slideshowInterval,
                        shuffle = preferences.slideshowShuffle,
                        onAddPhotos = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemovePhoto = onRemoveSlideshowImage,
                        onSelectInterval = onSelectSlideshowInterval,
                        onToggleShuffle = onToggleSlideshowShuffle
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BACKGROUND DIM / READABILITY SLIDER
            SectionTitle(title = "READABILITY & DIM", icon = Icons.Outlined.BrightnessMedium)

            Spacer(modifier = Modifier.height(10.dp))

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
                        text = "Background Dim",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
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
                    steps = 13, // 5% increments
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
                    text = "Darkens background images and colors to guarantee maximum contrast and readability for the hero clock.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
        }
    }
}

@Composable
private fun SectionTitle(
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
private fun BackgroundTypeTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (isSelected) FocusAmber else DarkElevatedSurface
    val textColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground

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
            .padding(vertical = 12.dp)
            .minimumInteractiveComponentSize()
            .testTag("background_type_tab_${label.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolidColorSection(
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
            text = "Curated Palette",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Neutrals
        Text(
            text = "NEUTRALS",
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
            text = "ACCENT SHADES",
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
            color = MaterialTheme.colorScheme.onBackground,
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
                placeholder = { Text("#101820", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                isError = hexInputError,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FocusAmber,
                    unfocusedBorderColor = DarkOutline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
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
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(swatchColor)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) FocusAmber else Color(0xFF383842),
                shape = RoundedCornerShape(14.dp)
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
private fun SingleImageSection(
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
            text = "Photo Background",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
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
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Ready for Active Focus",
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber.copy(alpha = 0.5f)),
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
                    text = "Choose Background Photo",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SlideshowSection(
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )

            OutlinedButton(
                onClick = onAddPhotos,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FocusAmber),
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No images selected",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Add photos to create a calming slideshow.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Horizontal scrollable thumbnail strip
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
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
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
                    tint = if (shuffle) FocusAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Shuffle Images",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Switch(
                checked = shuffle,
                onCheckedChange = onToggleShuffle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = FocusAmber,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = DarkCardSurface
                ),
                modifier = Modifier.testTag("slideshow_shuffle_switch")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transition Indicator
        Text(
            text = "Transition: Smooth Crossfade (800ms)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}
