package com.sprinthon.focusclock.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.animation.rememberSplineBasedDecay
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
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import com.sprinthon.focusclock.analytics.AmbientAnalytics
import com.sprinthon.focusclock.domain.model.CollectionPlaybackMode
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusTrack
import com.sprinthon.focusclock.domain.model.TrackCollection
import com.sprinthon.focusclock.playback.AudioFileHelper
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.AmbientColorTokens
import com.sprinthon.focusclock.ui.theme.AmbientTheme
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkAmbientColorTokens
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusActiveGlow
import com.sprinthon.focusclock.ui.theme.FocusAmber
import com.sprinthon.focusclock.ui.theme.LightAmbientColorTokens
import com.sprinthon.focusclock.ui.theme.LocalAmbientColors

/**
 * Phase 4 Milestone 4.2: Unified filter type enum for ambient soundscape categories.
 */
enum class AmbientFilterType(val title: String, val icon: ImageVector) {
    ALL("All", Icons.Default.MusicNote),
    FAVORITES("Favorites", Icons.Default.Favorite),
    SOUNDSCAPES("Soundscapes", Icons.Default.GraphicEq),
    COLLECTIONS("Collections", Icons.Default.Folder),
    CUSTOM("Custom", Icons.Default.Add)
}

typealias AmbientTab = AmbientFilterType

/**
 * Phase 2 Milestone 2.2: Mini-player vertical swipe-to-expand drag anchors.
 */
enum class MiniPlayerDragAnchor {
    Collapsed,
    Expanded
}

/**
 * Phase 4 Milestone 4.1: Normalized ambient playback state representation.
 * Decouples high-frequency player state updates from UI components that only need
 * stable playback flags.
 */
@Stable
data class AmbientPlaybackUiState(
    val isPlaying: Boolean,
    val selectedTrackId: String,
    val musicVolume: Float,
    val activeCollectionId: String?,
    val collectionPlaybackMode: CollectionPlaybackMode
)

val COLLECTION_PALETTE = listOf(
    0xFFF59E0B, // Amber
    0xFF10B981, // Emerald
    0xFF06B6D4, // Cyan
    0xFF8B5CF6, // Purple
    0xFFEC4899, // Pink
    0xFF3B82F6  // Blue
)

/**
 * Premium Ambient Soundscapes & Track Collections Customizer screen.
 * Supports browsing built-in soundscapes, managing custom collections/playlists,
 * organizing famous/favorite tracks, single/loop playback modes, and SAF/YouTube audio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundSettingsScreen(
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    customTracks: List<FocusTrack>,
    collections: List<TrackCollection>,
    favoriteTrackIds: Set<String>,
    onSelectTrack: (String, Boolean) -> Unit,
    onAddCustomTrack: (String, String, Boolean) -> Unit,
    onDeleteCustomTrack: (String) -> Unit,
    onCreateCollection: (String, String, List<String>, CollectionPlaybackMode, Long, String) -> Unit,
    onUpdateCollection: (TrackCollection) -> Unit,
    onDeleteCollection: (String) -> Unit,
    onToggleFavoriteTrack: (String) -> Unit,
    onAddTrackToCollection: (String, String) -> Unit,
    onRemoveTrackFromCollection: (String, String) -> Unit,
    onPlayCollection: (String, String?, Boolean) -> Unit,
    onClearActiveCollection: () -> Unit,
    onSetCollectionPlaybackMode: (CollectionPlaybackMode) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleLoop: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleShowWaveform: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    externalMediaState: com.sprinthon.focusclock.domain.model.ExternalMediaSessionState? = null,
    onSelectAudioSourceType: ((com.sprinthon.focusclock.domain.model.AudioSourceType) -> Unit)? = null,
    onToggleExternalPlayPause: (() -> Unit)? = null,
    onSkipExternalNext: (() -> Unit)? = null,
    onSkipExternalPrevious: (() -> Unit)? = null,
    onLaunchMusicApp: ((String) -> Unit)? = null,
    onOpenPermissionSettings: (() -> Unit)? = null,
    onTransferExternalToFocus: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(AmbientTab.ALL) }
    var showPlaybackDetails by remember { mutableStateOf(false) }
    var showQuickSettings by remember { mutableStateOf(false) }
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var collectionToEdit by remember { mutableStateOf<TrackCollection?>(null) }
    var trackForAddToCollection by remember { mutableStateOf<FocusTrack?>(null) }
    var trackTitleForLocal by remember { mutableStateOf("") }
    var isScanningFolder by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Phase 8 Milestone 8.3: UX Analytics screen lifecycle tracking
    val screenStartTime = remember { System.currentTimeMillis() }
    var hasLoggedFirstPlay by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        AmbientAnalytics.logScreenOpened()
        onDispose {
            val sessionSec = (System.currentTimeMillis() - screenStartTime) / 1000
            AmbientAnalytics.logSessionDuration(sessionSec)
        }
    }

    // Phase 1 Milestone 1.4: Destructive action delete handler with 5-second Undo window
    val handleDeleteTrackWithUndo = { trackId: String ->
        AmbientAnalytics.logGestureUsed("swipe_delete", trackId)
        val trackToDelete = customTracks.find { it.id == trackId }
        onDeleteCustomTrack(trackId)
        if (trackToDelete != null) {
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Removed '${trackToDelete.title}'",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onAddCustomTrack(trackToDelete.uri, trackToDelete.title, trackToDelete.isYouTube)
                }
            }
        }
    }

    val isDark = isSystemInDarkTheme()
    val ambientTokens = remember(isDark) {
        if (isDark) DarkAmbientColorTokens else LightAmbientColorTokens
    }

    val allTracks = remember(customTracks) {
        FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
    }
    val activeTrack by remember(allTracks, preferences.selectedTrackId) {
        derivedStateOf { allTracks.find { it.id == preferences.selectedTrackId } ?: allTracks.first() }
    }
    val activeCollection by remember(collections, preferences.activeCollectionId) {
        derivedStateOf { collections.find { it.id == preferences.activeCollectionId } }
    }
    val favoriteTracks by remember(allTracks, favoriteTrackIds) {
        derivedStateOf { allTracks.filter { favoriteTrackIds.contains(it.id) } }
    }
    val builtInTracks by remember {
        derivedStateOf { FocusAudioCatalog.BUILT_IN_TRACKS }
    }

    val playbackUiState by remember(
        playerState.isPlaying,
        preferences.selectedTrackId,
        preferences.musicVolume,
        preferences.activeCollectionId,
        preferences.collectionPlaybackMode
    ) {
        derivedStateOf {
            AmbientPlaybackUiState(
                isPlaying = playerState.isPlaying,
                selectedTrackId = preferences.selectedTrackId,
                musicVolume = preferences.musicVolume,
                activeCollectionId = preferences.activeCollectionId,
                collectionPlaybackMode = preferences.collectionPlaybackMode
            )
        }
    }

    // SAF Local File Picker Launcher (Bulk / Multiple Files)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            // Phase 7 Milestone 7.2: Validate file formats
            val supportedFormats = setOf("mp3", "wav", "m4a", "flac", "ogg", "aac", "opus")
            var unsupportedCount = 0
            var addedCount = 0
            var lastAddedUri = ""
            var lastAddedTitle = ""

            uris.forEachIndexed { index, uri ->
                var fileName = ""
                var fileExtension = ""
                try {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) {
                                val fullName = it.getString(nameIndex)
                                fileName = fullName.substringBeforeLast(".")
                                fileExtension = fullName.substringAfterLast(".", "").lowercase()
                            }
                        }
                    }
                } catch (e: Exception) {}

                // Phase 7 Milestone 7.2: Check if format is supported
                if (fileExtension !in supportedFormats) {
                    unsupportedCount++
                    return@forEachIndexed
                }

                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Provider may not support persistable permissions
                }

                val title = if (fileName.isNotBlank()) {
                    fileName
                } else if (trackTitleForLocal.isNotBlank()) {
                    if (uris.size > 1) "$trackTitleForLocal ${index + 1}" else trackTitleForLocal
                } else {
                    if (uris.size > 1) "Local Track ${index + 1}" else "Local Track"
                }
                onAddCustomTrack(uri.toString(), title, false)
                lastAddedUri = uri.toString()
                lastAddedTitle = title
                addedCount++
            }

            // Phase 7 Milestone 7.2: Show error for unsupported formats
            if (unsupportedCount > 0) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "$unsupportedCount unsupported format(s). Try MP3, WAV, or OGG.",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            if (addedCount > 0) {
                showAddTrackDialog = false

            // Phase 5 Milestone 5.3: Success feedback with direct Auto-Play CTA
            coroutineScope.launch {
                val msg = if (addedCount > 1) "Imported $addedCount tracks to Custom Audio" else "Added '$lastAddedTitle' to Custom Audio"
                val result = snackbarHostState.showSnackbar(
                    message = msg,
                    actionLabel = "Play Now",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed && lastAddedUri.isNotBlank()) {
                    onSelectTrack(lastAddedUri, true)
                }
            }
        }
    }

    // SAF OpenDocumentTree Launcher (Select full folder)
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        if (treeUri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore if not supported
            }

            coroutineScope.launch {
                isScanningFolder = true
                Toast.makeText(context, "Scanning folder for audio tracks...", Toast.LENGTH_SHORT).show()
                val scannedTracks = AudioFileHelper.scanFolderForAudio(context, treeUri)
                isScanningFolder = false

                // Phase 7 Milestone 7.2: Filter unsupported formats from folder scan
                val supportedFormats = setOf("mp3", "wav", "m4a", "flac", "ogg", "aac", "opus")
                val validTracks = scannedTracks.filter { scanned ->
                    val extension = scanned.displayName.substringAfterLast(".", "").lowercase()
                    extension in supportedFormats
                }
                val unsupportedCount = scannedTracks.size - validTracks.size

                if (validTracks.isEmpty()) {
                    Toast.makeText(context, "No supported audio files found in selected folder", Toast.LENGTH_LONG).show()
                } else {
                    validTracks.forEach { scanned ->
                        try {
                            context.contentResolver.takePersistableUriPermission(
                                scanned.uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: Exception) {}
                        onAddCustomTrack(scanned.uri.toString(), scanned.displayName, false)
                    }
                    showAddTrackDialog = false

                    // Phase 7 Milestone 7.2: Show warning for unsupported formats
                    if (unsupportedCount > 0) {
                        snackbarHostState.showSnackbar(
                            message = "$unsupportedCount unsupported file(s) skipped. Try MP3, WAV, or OGG.",
                            duration = SnackbarDuration.Short
                        )
                    }

                    // Phase 5 Milestone 5.3: Success feedback with direct Auto-Play CTA
                    val firstTrackUri = validTracks.firstOrNull()?.uri?.toString()
                    val result = snackbarHostState.showSnackbar(
                        message = "Imported ${validTracks.size} tracks from folder!",
                        actionLabel = "Play Now",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed && firstTrackUri != null) {
                        onSelectTrack(firstTrackUri, true)
                    }
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(arrayOf("audio/*"))
        } else {
            Toast.makeText(context, "Permission required to access audio files", Toast.LENGTH_SHORT).show()
        }
    }

    val launchLocalPicker = { title: String ->
        trackTitleForLocal = title
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            launcher.launch(arrayOf("audio/*"))
        } else {
            permissionLauncher.launch(permission)
        }
    }

    val launchFolderPicker = {
        folderPickerLauncher.launch(null)
    }

    CompositionLocalProvider(LocalAmbientColors provides ambientTokens) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("ambient_sound_settings_screen"),
            containerColor = ambientTokens.ambientSurface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = ambientTokens.ambientSurfaceVariant,
                            contentColor = ambientTokens.ambientOnSurface,
                            actionColor = ambientTokens.ambientAccent,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                )
            },
            topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Ambient Soundscapes",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${allTracks.size} Tracks Available",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = FocusAmber,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("audio_settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showQuickSettings = true
                            AmbientAnalytics.logSettingsOpened()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("quick_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Quick Settings",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmoledBlack
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
                    .widthIn(max = 620.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // ==========================================
                // 1. COMPACT STICKY MINI-PLAYER BAR (Phase 2 & Phase 4)
                // ==========================================
                AmbientMiniPlayerBar(
                    activeTrack = activeTrack,
                    activeCollection = activeCollection,
                    playbackUiState = playbackUiState,
                    onTogglePlayPause = onTogglePlayPause,
                    onVolumeToggle = {
                        val newVol = if (preferences.musicVolume > 0f) 0f else 0.7f
                        onVolumeChange(newVol)
                    },
                    onExpandDetails = { showPlaybackDetails = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ==========================================
                // SCROLLABLE CONTENT (Phase 2 Sticky Header)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // ==========================================
                    // 2. CATEGORY FILTER CHIPS (Phase 3 Milestone 3.1)
                    // ==========================================
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AmbientTab.values()) { tab ->
                            val isSelected = selectedTab == tab
                            val count = when (tab) {
                                AmbientTab.ALL -> allTracks.size
                                AmbientTab.FAVORITES -> favoriteTrackIds.size
                                AmbientTab.SOUNDSCAPES -> FocusAudioCatalog.BUILT_IN_TRACKS.size
                                AmbientTab.COLLECTIONS -> collections.size
                                AmbientTab.CUSTOM -> customTracks.size
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTab = tab
                                    AmbientAnalytics.logFilterChanged(tab.name)
                                },
                                label = {
                                    Text(
                                        text = "${tab.title} ($count)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isSelected) Color(0xFF141418) else (if (tab == AmbientTab.FAVORITES) ambientTokens.ambientError else ambientTokens.ambientAccent)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = ambientTokens.ambientSurface,
                                    labelColor = ambientTokens.ambientOnSurfaceMuted,
                                    selectedContainerColor = ambientTokens.ambientAccent,
                                    selectedLabelColor = Color(0xFF141418),
                                    selectedLeadingIconColor = Color(0xFF141418)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = ambientTokens.ambientOutline,
                                    selectedBorderColor = ambientTokens.ambientAccent,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("tab_${tab.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ==========================================
                    // 3. TAB CONTENT (AnimatedContent - Phase 3 Milestone 3.2)
                    // ==========================================
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                                slideInVertically(
                                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                                    initialOffsetY = { 24 }
                                )) togetherWith
                                fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                        },
                        label = "tab_content_transition"
                    ) { currentTab ->
                        when (currentTab) {
                            AmbientTab.ALL -> {
                                AllTracksView(
                                    allTracks = allTracks,
                                    preferences = preferences,
                                    playerState = playerState,
                                    favoriteTrackIds = favoriteTrackIds,
                                    onSelectTrack = onSelectTrack,
                                    onTogglePlayPause = onTogglePlayPause,
                                    onToggleFavorite = onToggleFavoriteTrack,
                                    onAddToCollection = { track -> trackForAddToCollection = track },
                                    onDeleteCustomTrack = handleDeleteTrackWithUndo,
                                    onAddCustomClick = { showAddTrackDialog = true }
                                )
                            }
                            AmbientTab.FAVORITES -> {
                                FavoritesOnlyView(
                                    allTracks = allTracks,
                                    preferences = preferences,
                                    playerState = playerState,
                                    favoriteTrackIds = favoriteTrackIds,
                                    onSelectTrack = onSelectTrack,
                                    onTogglePlayPause = onTogglePlayPause,
                                    onToggleFavorite = onToggleFavoriteTrack,
                                    onAddToCollection = { track -> trackForAddToCollection = track },
                                    onDeleteCustomTrack = handleDeleteTrackWithUndo
                                )
                            }
                            AmbientTab.COLLECTIONS -> {
                                CollectionsView(
                                    collections = collections,
                                    allTracks = allTracks,
                                    preferences = preferences,
                                    playerState = playerState,
                                    favoriteTrackIds = favoriteTrackIds,
                                    onCreateCollectionClick = { showCreateCollectionDialog = true },
                                    onEditCollection = { col -> collectionToEdit = col },
                                    onDeleteCollection = onDeleteCollection,
                                    onPlayCollection = onPlayCollection,
                                    onRemoveTrackFromCollection = onRemoveTrackFromCollection,
                                    onToggleFavorite = onToggleFavoriteTrack,
                                    onSelectTrack = onSelectTrack,
                                    onTogglePlayPause = onTogglePlayPause
                                )
                            }
                            AmbientTab.SOUNDSCAPES -> {
                                SoundscapesOnlyView(
                                    builtInTracks = FocusAudioCatalog.BUILT_IN_TRACKS,
                                    preferences = preferences,
                                    playerState = playerState,
                                    favoriteTrackIds = favoriteTrackIds,
                                    onSelectTrack = onSelectTrack,
                                    onTogglePlayPause = onTogglePlayPause,
                                    onToggleFavorite = onToggleFavoriteTrack,
                                    onAddToCollection = { track -> trackForAddToCollection = track }
                                )
                            }
                            AmbientTab.CUSTOM -> {
                                CustomTracksOnlyView(
                                    customTracks = customTracks,
                                    preferences = preferences,
                                    playerState = playerState,
                                    favoriteTrackIds = favoriteTrackIds,
                                    onSelectTrack = onSelectTrack,
                                    onTogglePlayPause = onTogglePlayPause,
                                    onToggleFavorite = onToggleFavoriteTrack,
                                    onAddToCollection = { track -> trackForAddToCollection = track },
                                    onDeleteCustomTrack = handleDeleteTrackWithUndo,
                                    onAddCustomClick = { showAddTrackDialog = true }
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

    // Modal Dialogs & Sheets
    // Phase 6: Quick Settings Sheet
    if (showQuickSettings) {
        AmbientQuickSettingsSheet(
            preferences = preferences,
            onDismiss = { showQuickSettings = false },
            onToggleAutoPlay = onToggleAutoPlay,
            onToggleLoop = onToggleLoop,
            onToggleShowWaveform = onToggleShowWaveform
        )
    }

    // Phase 5: Streamlined Modal Bottom Sheet for Custom Audio Import
    if (showAddTrackDialog) {
        ImportAudioBottomSheet(
            onDismiss = { showAddTrackDialog = false },
            onLaunchLocalPicker = { title ->
                launchLocalPicker(title)
            },
            onLaunchFolderPicker = {
                launchFolderPicker()
            },
            onAddYouTubeTrack = { url, title ->
                onAddCustomTrack(url, title, true)
                showAddTrackDialog = false
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Added '$title' to Custom Audio",
                        actionLabel = "Play Now",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onSelectTrack(url, true)
                    }
                }
            },
            isScanningFolder = isScanningFolder
        )
    }

    if (showCreateCollectionDialog) {
        CreateOrEditCollectionDialog(
            existingCollection = null,
            allAvailableTracks = allTracks,
            onDismiss = { showCreateCollectionDialog = false },
            onSave = { name, desc, trackIds, mode, colorHex ->
                onCreateCollection(name, desc, trackIds, mode, colorHex, "playlist")
                showCreateCollectionDialog = false
            }
        )
    }

    if (collectionToEdit != null) {
        CreateOrEditCollectionDialog(
            existingCollection = collectionToEdit,
            allAvailableTracks = allTracks,
            onDismiss = { collectionToEdit = null },
            onSave = { name, desc, trackIds, mode, colorHex ->
                val updated = collectionToEdit!!.copy(
                    name = name,
                    description = desc,
                    trackIds = trackIds,
                    playbackMode = mode,
                    accentColorHex = colorHex
                )
                onUpdateCollection(updated)
                collectionToEdit = null
            }
        )
    }

    if (trackForAddToCollection != null) {
        AddToCollectionBottomSheet(
            track = trackForAddToCollection!!,
            collections = collections,
            onDismiss = { trackForAddToCollection = null },
            onAddToCollection = { colId ->
                onAddTrackToCollection(colId, trackForAddToCollection!!.id)
                trackForAddToCollection = null
                Toast.makeText(context, "Added to collection", Toast.LENGTH_SHORT).show()
            },
            onCreateNewCollection = {
                trackForAddToCollection = null
                showCreateCollectionDialog = true
            }
        )
    }

    if (showPlaybackDetails) {
        // Phase 2 Milestone 2.2: Scrim overlay at 40% opacity behind expanded sheet
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showPlaybackDetails = false }
                )
                .testTag("ambient_playback_detail_scrim")
        )

        AmbientPlaybackDetailSheet(
            activeTrack = activeTrack,
            activeCollection = activeCollection,
            playerState = playerState,
            preferences = preferences,
            onDismiss = { showPlaybackDetails = false },
            onTogglePlayPause = {
                if (preferences.selectedTrackId == activeTrack.id && playerState.isPlaying) {
                    onTogglePlayPause()
                } else {
                    onSelectTrack(activeTrack.id, true)
                }
            },
            onVolumeChange = onVolumeChange,
            onClearCollection = onClearActiveCollection,
            onSelectPlaybackMode = { mode ->
                onSetCollectionPlaybackMode(mode)
            }
        )
    }
    }
}

// =========================================================================
// FLOATING COMPACT MINI-PLAYER BAR (Phase 2 Milestone 2.1 & 2.2)
// =========================================================================

/**
 * Modern Compact Floating Mini-Player Bar (72dp height).
 * Replaces the bulky 200dp static hero audition card.
 * - Pinned at top of content area (sticky header)
 * - 16dp rounded corners, AMOLED surface with outline border
 * - Left: Track icon with animated waveform when playing + bold track title
 * - Right: Volume toggle icon + master 1-tap play/pause button (48dp touch targets)
 * - Bottom: 2.5dp subtle progress bar indicator
 * - Gestures: Tap track title area or swipe-up expands AmbientPlaybackDetailSheet
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AmbientMiniPlayerBar(
    activeTrack: FocusTrack,
    activeCollection: TrackCollection?,
    playbackUiState: AmbientPlaybackUiState,
    onTogglePlayPause: () -> Unit,
    onVolumeToggle: () -> Unit,
    onExpandDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val colors = AmbientTheme.colors
    val density = LocalDensity.current
    val dragDistancePx = with(density) { 72.dp.toPx() }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val snapAnimationSpec = tween<Float>(
        durationMillis = 400,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f) // EmphasizedDecelerate
    )

    // Phase 2 Milestone 2.2: AnchoredDraggable state with velocity-based snap
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = MiniPlayerDragAnchor.Collapsed,
            positionalThreshold = { distance: Float -> distance * 0.4f },
            velocityThreshold = { with(density) { 120.dp.toPx() } },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = { targetValue ->
                if (targetValue == MiniPlayerDragAnchor.Expanded) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onExpandDetails()
                }
                true
            }
        )
    }

    androidx.compose.runtime.LaunchedEffect(dragDistancePx) {
        anchoredDraggableState.updateAnchors(
            DraggableAnchors {
                MiniPlayerDragAnchor.Collapsed at 0f
                MiniPlayerDragAnchor.Expanded at -dragDistancePx
            }
        )
    }

    androidx.compose.runtime.LaunchedEffect(anchoredDraggableState.currentValue) {
        if (anchoredDraggableState.currentValue == MiniPlayerDragAnchor.Expanded) {
            anchoredDraggableState.snapTo(MiniPlayerDragAnchor.Collapsed)
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.ambientSurface,
        border = BorderStroke(1.dp, colors.ambientOutline),
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Vertical
            )
            .clickable(
                role = Role.Button,
                onClick = onExpandDetails
            )
            .testTag("ambient_mini_player_bar")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Track Icon Box with animated waveform
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (playbackUiState.isPlaying) {
                                Brush.linearGradient(listOf(colors.ambientAccent, colors.ambientActiveGlow))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF22222C), Color(0xFF181820)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (playbackUiState.isPlaying) {
                        AnimatedWaveformBars(isAnimating = true, barColor = Color.Black)
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = colors.ambientAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and collection / subtitle indicator
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeTrack.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = colors.ambientOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (activeCollection != null) {
                            Text(
                                text = "Playlist: ${activeCollection.name}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(activeCollection.accentColorHex),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = if (activeTrack.isYouTube) "YouTube Audio" else activeTrack.artist,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = colors.ambientAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Volume Indicator Toggle (48dp touch target)
                IconButton(
                    onClick = onVolumeToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mini_player_volume_toggle")
                ) {
                    Icon(
                        imageVector = if (playbackUiState.musicVolume == 0f) {
                            Icons.AutoMirrored.Filled.VolumeMute
                        } else {
                            Icons.AutoMirrored.Filled.VolumeDown
                        },
                        contentDescription = if (playbackUiState.musicVolume == 0f) "Unmute" else "Mute",
                        tint = if (playbackUiState.musicVolume == 0f) colors.ambientOnSurfaceMuted else colors.ambientAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Master Play/Pause Button (48dp touch target with 42dp surface)
                Surface(
                    shape = CircleShape,
                    color = colors.ambientAccent,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = onTogglePlayPause
                        )
                        .testTag("master_preview_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playbackUiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackUiState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 2.5dp horizontal progress line embedded at bottom edge of card
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(colors.ambientOutline.copy(alpha = 0.5f))
            ) {
                if (playbackUiState.isPlaying) {
                    // Subtle glowing active indicator line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.ambientAccentDim, colors.ambientAccent, colors.ambientActiveGlow)
                                )
                            )
                    )
                }
            }
        }
    }
}

// =========================================================================
// ALL TRACKS VIEW
// =========================================================================

@Composable
private fun AllTracksView(
    allTracks: List<FocusTrack>,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    favoriteTrackIds: Set<String>,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCollection: (FocusTrack) -> Unit,
    onDeleteCustomTrack: (String) -> Unit,
    onAddCustomClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column {
        SettingsSectionHeader(title = "All Ambient Tracks (${allTracks.size})")
        SettingsCard {
            // Phase 7 Milestone 7.1: Show skeleton rows while loading
            if (isLoading) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) {
                        TrackRowSkeleton()
                    }
                }
            } else {
                // Phase 7 Milestone 7.4: Convert to LazyColumn with keys for performance
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = allTracks,
                        key = { it.id },
                        contentType = { "track" }
                    ) { track ->
                    TrackRowItem(
                        track = track,
                        isSelected = preferences.selectedTrackId == track.id,
                        isPlaying = preferences.selectedTrackId == track.id && playerState.isPlaying,
                        isFavorite = favoriteTrackIds.contains(track.id),
                        onSelect = { onSelectTrack(track.id, false) },
                        onPreviewToggle = {
                            if (preferences.selectedTrackId == track.id) {
                                onTogglePlayPause()
                            } else {
                                onSelectTrack(track.id, true)
                            }
                        },
                        onToggleFavorite = { onToggleFavorite(track.id) },
                        onAddToCollection = { onAddToCollection(track) },
                        onDelete = if (!track.isBuiltIn) { { onDeleteCustomTrack(track.id) } } else null
                    )
                }

                item(contentType = "divider") {
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                }

                item(contentType = "action") {
                    // Add Custom Track Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAddCustomClick)
                            .padding(16.dp)
                            .testTag("add_custom_track_row"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Track",
                            tint = FocusAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Custom Audio / YouTube",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = FocusAmber
                        )
                    }
                }
                }
            }
        }
    }
}

// =========================================================================
// FAVORITES ONLY VIEW (Phase 3 Milestone 3.1 & 3.2)
// =========================================================================

@Composable
private fun FavoritesOnlyView(
    allTracks: List<FocusTrack>,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    favoriteTrackIds: Set<String>,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCollection: (FocusTrack) -> Unit,
    onDeleteCustomTrack: (String) -> Unit,
    isLoading: Boolean = false
) {
    val favoriteTracks = remember(allTracks, favoriteTrackIds) {
        allTracks.filter { favoriteTrackIds.contains(it.id) }
    }

    Column {
        SettingsSectionHeader(title = "Favorite Tracks (${favoriteTracks.size})")
        SettingsCard {
            // Phase 7 Milestone 7.1: Show skeleton rows while loading
            if (isLoading) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) {
                        TrackRowSkeleton()
                    }
                }
            } else if (favoriteTracks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE11D48).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No favorite tracks yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the menu or swipe right on any track to add it to your favorites for instant 1-tap playback.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = Color(0xFF9E9EA8),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // Phase 7 Milestone 7.4: Convert to LazyColumn with keys for performance
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = favoriteTracks,
                        key = { it.id },
                        contentType = { "track" }
                    ) { track ->
                        TrackRowItem(
                            track = track,
                            isSelected = preferences.selectedTrackId == track.id,
                            isPlaying = preferences.selectedTrackId == track.id && playerState.isPlaying,
                            isFavorite = true,
                            onSelect = { onSelectTrack(track.id, false) },
                            onPreviewToggle = {
                                if (preferences.selectedTrackId == track.id) {
                                    onTogglePlayPause()
                                } else {
                                    onSelectTrack(track.id, true)
                                }
                            },
                            onToggleFavorite = { onToggleFavorite(track.id) },
                            onAddToCollection = { onAddToCollection(track) },
                            onDelete = if (!track.isBuiltIn) { { onDeleteCustomTrack(track.id) } } else null
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// COLLECTIONS / PLAYLISTS VIEW
// =========================================================================

@Composable
private fun CollectionsView(
    collections: List<TrackCollection>,
    allTracks: List<FocusTrack>,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    favoriteTrackIds: Set<String>,
    onCreateCollectionClick: () -> Unit,
    onEditCollection: (TrackCollection) -> Unit,
    onDeleteCollection: (String) -> Unit,
    onPlayCollection: (String, String?, Boolean) -> Unit,
    onRemoveTrackFromCollection: (String, String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit
) {
    val colors = AmbientTheme.colors

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsSectionHeader(title = "My Collections & Playlists")
            OutlinedButton(
                onClick = onCreateCollectionClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.ambientAccent
                ),
                border = BorderStroke(1.dp, colors.ambientActiveGlow),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(34.dp).testTag("create_collection_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "New Collection",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 1. SMART "FAMOUS / FAVORITES" COLLECTION CARD
        val favoriteTracks = remember(allTracks, favoriteTrackIds) {
            allTracks.filter { favoriteTrackIds.contains(it.id) }
        }
        SmartFavoritesCard(
            favoriteTracks = favoriteTracks,
            isPlayingCollection = preferences.activeCollectionId == "favorites_smart_id",
            onPlayFavorites = {
                if (favoriteTracks.isNotEmpty()) {
                    onPlayCollection("favorites_smart_id", favoriteTracks.first().id, true)
                }
            },
            onSelectTrack = onSelectTrack,
            onToggleFavorite = onToggleFavorite,
            selectedTrackId = preferences.selectedTrackId,
            isPlayerPlaying = playerState.isPlaying,
            onTogglePlayPause = onTogglePlayPause
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. USER CUSTOM COLLECTIONS
        if (collections.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colors.ambientSurface,
                border = BorderStroke(1.dp, colors.ambientOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colors.ambientAccentDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = colors.ambientAccent,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Organize Your Soundscapes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = colors.ambientOnSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Group your favorite sounds into collections for quick access, custom playlist orders, and seamless loop playback rules.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = colors.ambientOnSurfaceMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onCreateCollectionClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.ambientAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Create First Collection",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        } else {
            collections.forEach { collection ->
                val isCollectionActive = preferences.activeCollectionId == collection.id
                CustomCollectionCard(
                    collection = collection,
                    allAvailableTracks = allTracks,
                    isActive = isCollectionActive,
                    preferences = preferences,
                    playerState = playerState,
                    onPlay = { startTrackId ->
                        onPlayCollection(collection.id, startTrackId, true)
                    },
                    onEdit = { onEditCollection(collection) },
                    onDelete = { onDeleteCollection(collection.id) },
                    onRemoveTrack = { trackId ->
                        onRemoveTrackFromCollection(collection.id, trackId)
                    },
                    onSelectTrack = onSelectTrack,
                    onTogglePlayPause = onTogglePlayPause
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// =========================================================================
// SMART FAVORITES CARD
// =========================================================================

@Composable
private fun SmartFavoritesCard(
    favoriteTracks: List<FocusTrack>,
    isPlayingCollection: Boolean,
    onPlayFavorites: () -> Unit,
    onSelectTrack: (String, Boolean) -> Unit,
    onToggleFavorite: (String) -> Unit,
    selectedTrackId: String,
    isPlayerPlaying: Boolean,
    onTogglePlayPause: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF161414),
        border = BorderStroke(1.dp, Color(0xFF382C18)),
        modifier = Modifier.fillMaxWidth().testTag("favorites_collection_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFE11D48), Color(0xFFF43F5E)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Famous & Favorites",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE11D48).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${favoriteTracks.size} tracks",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    color = Color(0xFFF43F5E),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tracks you starred for quick focus access",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF9E9EA8)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (favoriteTracks.isNotEmpty()) {
                        IconButton(
                            onClick = onPlayFavorites,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FocusAmber)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Favorites",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = Color(0xFF9E9EA8)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color(0xFF2B221B), thickness = 0.75.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (favoriteTracks.isEmpty()) {
                        Text(
                            text = "No tracks favorited yet. Tap the heart icon next to any track to add it here!",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFF888892),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        favoriteTracks.forEach { track ->
                            val isSelected = selectedTrackId == track.id
                            val isPlaying = isSelected && isPlayerPlaying

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectTrack(track.id, false) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isSelected) onTogglePlayPause() else onSelectTrack(track.id, true)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isSelected) FocusAmber else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        ),
                                        color = if (isSelected) FocusAmber else Color.White
                                    )
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF888892)
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleFavorite(track.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Unfavorite",
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(18.dp)
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

// =========================================================================
// CUSTOM COLLECTION CARD COMPONENT
// =========================================================================

@Composable
private fun CustomCollectionCard(
    collection: TrackCollection,
    allAvailableTracks: List<FocusTrack>,
    isActive: Boolean,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    onPlay: (String?) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRemoveTrack: (String) -> Unit,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val accentColor = Color(collection.accentColorHex)

    val containedTracks = remember(collection.trackIds, allAvailableTracks) {
        collection.trackIds.mapNotNull { id ->
            allAvailableTracks.find { it.id == id } ?: FocusAudioCatalog.getTrackById(id)
        }
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF131318),
        border = BorderStroke(
            1.25.dp,
            if (isActive) accentColor else Color(0xFF22222C)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("collection_card_${collection.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = collection.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = accentColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = accentColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${containedTracks.size} tracks",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF9E9EA8)
                            )
                            Text(text = "•", color = Color(0xFF666670), fontSize = 10.sp)
                            Text(
                                text = collection.playbackMode.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = accentColor
                            )
                        }
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onPlay(null) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                            .testTag("play_collection_${collection.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Collection",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Collection Options",
                                tint = Color(0xFF9E9EA8)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Color(0xFF1C1C24))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Collection", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = FocusAmber) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Collection", color = Color(0xFFEF4444)) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = Color(0xFF9E9EA8)
                        )
                    }
                }
            }

            if (collection.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = collection.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF888894)
                )
            }

            // Expanded Tracks inside collection
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color(0xFF22222E), thickness = 0.75.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (containedTracks.isEmpty()) {
                        Text(
                            text = "No tracks in this collection. Edit collection to add tracks.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFF888892),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        containedTracks.forEachIndexed { idx, track ->
                            val isSelected = preferences.selectedTrackId == track.id
                            val isPlaying = isSelected && playerState.isPlaying

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectTrack(track.id, false) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF6E6E78),
                                    modifier = Modifier.width(22.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (isSelected) onTogglePlayPause() else onPlay(track.id)
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isSelected) accentColor else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        ),
                                        color = if (isSelected) accentColor else Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF888892)
                                    )
                                }

                                IconButton(
                                    onClick = { onRemoveTrack(track.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove from collection",
                                        tint = Color(0xFF888892),
                                        modifier = Modifier.size(16.dp)
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

// =========================================================================
// SOUNDSCAPES ONLY VIEW
// =========================================================================

@Composable
private fun SoundscapesOnlyView(
    builtInTracks: List<FocusTrack>,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    favoriteTrackIds: Set<String>,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCollection: (FocusTrack) -> Unit,
    isLoading: Boolean = false
) {
    Column {
        SettingsSectionHeader(title = "Built-in Soundscapes (${builtInTracks.size})")
        SettingsCard {
            // Phase 7 Milestone 7.1: Show skeleton rows while loading
            if (isLoading) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) {
                        TrackRowSkeleton()
                    }
                }
            } else {
                // Phase 7 Milestone 7.4: Convert to LazyColumn with keys for performance
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = builtInTracks,
                        key = { it.id },
                        contentType = { "track" }
                    ) { track ->
                    TrackRowItem(
                        track = track,
                        isSelected = preferences.selectedTrackId == track.id,
                        isPlaying = preferences.selectedTrackId == track.id && playerState.isPlaying,
                        isFavorite = favoriteTrackIds.contains(track.id),
                        onSelect = { onSelectTrack(track.id, false) },
                        onPreviewToggle = {
                            if (preferences.selectedTrackId == track.id) {
                                onTogglePlayPause()
                            } else {
                                onSelectTrack(track.id, true)
                            }
                        },
                        onToggleFavorite = { onToggleFavorite(track.id) },
                        onAddToCollection = { onAddToCollection(track) },
                        onDelete = null
                    )
                }
                }
            }
        }
    }
}

// =========================================================================
// CUSTOM TRACKS ONLY VIEW
// =========================================================================

@Composable
private fun CustomTracksOnlyView(
    customTracks: List<FocusTrack>,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    favoriteTrackIds: Set<String>,
    onSelectTrack: (String, Boolean) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCollection: (FocusTrack) -> Unit,
    onDeleteCustomTrack: (String) -> Unit,
    onAddCustomClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column {
        SettingsSectionHeader(title = "Custom Audio & Streaming Links (${customTracks.size})")
        SettingsCard {
            // Phase 7 Milestone 7.1: Show skeleton rows while loading
            if (isLoading) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) {
                        TrackRowSkeleton()
                    }
                }
            } else if (customTracks.isEmpty()) {
                // Phase 7 Milestone 7.3: Empty state with illustration and CTA
                val colors = AmbientTheme.colors
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colors.ambientAccentDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = colors.ambientAccent,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Add Your Own Sounds",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = colors.ambientOnSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Import local audio files or add streaming YouTube links to personalize your focus experience.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = colors.ambientOnSurfaceMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onAddCustomClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.ambientAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Import Audio",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // Phase 7 Milestone 7.4: Convert to LazyColumn with keys for performance
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = customTracks,
                        key = { it.id },
                        contentType = { "track" }
                    ) { track ->
                        TrackRowItem(
                            track = track,
                            isSelected = preferences.selectedTrackId == track.id,
                            isPlaying = preferences.selectedTrackId == track.id && playerState.isPlaying,
                            isFavorite = favoriteTrackIds.contains(track.id),
                            onSelect = { onSelectTrack(track.id, false) },
                            onPreviewToggle = {
                                if (preferences.selectedTrackId == track.id) {
                                    onTogglePlayPause()
                                } else {
                                    onSelectTrack(track.id, true)
                                }
                            },
                            onToggleFavorite = { onToggleFavorite(track.id) },
                            onAddToCollection = { onAddToCollection(track) },
                            onDelete = { onDeleteCustomTrack(track.id) }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddCustomClick)
                    .padding(16.dp)
                    .testTag("add_custom_track_row_tab"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Track",
                    tint = FocusAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Import Local File / YouTube Link",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = FocusAmber
                )
            }
        }
    }
}

// =========================================================================
// REUSABLE TRACK ROW ITEM COMPONENT (Phase 1 Redesign)
// =========================================================================

/**
 * Redesigned track row with:
 * - 68dp height, full-row 1-tap play/pause
 * - Animated amber glow border + tinted background for active state
 * - Single 3-dot overflow menu (replaces inline Heart/Plus/Delete icons)
 * - Swipe-right to toggle favorite, swipe-left to delete (custom only)
 * - Long-press to open context menu with haptic feedback
 * - Integrated animated waveform for currently playing track
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TrackRowItem(
    track: FocusTrack,
    isSelected: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onPreviewToggle: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCollection: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val colors = AmbientTheme.colors

    // Animated colors for active/inactive state transitions (300ms EmphasizedDecelerate)
    val rowBorderColor by animateColorAsState(
        targetValue = if (isSelected) colors.ambientActiveGlow else Color.Transparent,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "rowBorder"
    )
    val rowBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) colors.ambientActiveBg else Color.Transparent,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "rowBg"
    )

    // Overflow menu state
    var showOverflowMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    // Phase 1 Milestone 1.4: Material 3 SwipeToDismiss gesture with favorite/delete & rubber-band
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToEnd -> {
                    // Right swipe: toggle favorite
                    AmbientAnalytics.logGestureUsed("swipe_favorite", track.id)
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleFavorite()
                    false // Return false so item snaps back and stays in list
                }
                DismissValue.DismissedToStart -> {
                    // Left swipe: delete custom track, or rubber-band for built-in
                    if (onDelete != null) {
                        AmbientAnalytics.logGestureUsed("swipe_delete", track.id)
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                        true // Dismiss custom track
                    } else {
                        AmbientAnalytics.logGestureUsed("swipe_delete_rejected_builtin", track.id)
                        // Built-in track resists delete swipe with rubber-band effect!
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        false // Snap back without deleting!
                    }
                }
                DismissValue.Default -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .testTag("track_row_${track.id}"),
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            when (direction) {
                DismissDirection.StartToEnd -> {
                    // Right swipe: Favorite star reveal
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.ambientAccentDim)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                tint = colors.ambientAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = if (isFavorite) "Unfavorite" else "Favorite",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.ambientAccent
                            )
                        }
                    }
                }
                DismissDirection.EndToStart -> {
                    // Left swipe: Delete (custom) or Rubber-band non-deletable (built-in)
                    if (onDelete != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.ambientErrorContainer)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Remove",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.ambientError
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Track",
                                    tint = colors.ambientError,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    } else {
                        // Built-in track rubber-band visual feedback
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.ambientSurfaceVariant)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Built-in Sound (Cannot Delete)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = colors.ambientOnSurfaceMuted
                                )
                            }
                        }
                    }
                }
            }
        },
        dismissContent = {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = rowBackgroundColor,
                border = if (isSelected) BorderStroke(1.dp, rowBorderColor) else null,
                modifier = Modifier
                    .fillMaxSize()
                    .semantics {
                        // Phase 8 Milestone 8.1: TalkBack accessibility state description
                        stateDescription = if (isSelected && isPlaying) {
                            "Playing, Active ambient track"
                        } else if (isSelected) {
                            "Paused, Active ambient track"
                        } else {
                            "Inactive ambient track"
                        }
                    }
            ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        onClick = onPreviewToggle,
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showOverflowMenu = true
                        },
                        role = Role.Button
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Track icon / Animated waveform for active playing track
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected && isPlaying) {
                                Brush.linearGradient(listOf(colors.ambientAccent, colors.ambientActiveGlow))
                            } else if (isSelected) {
                                Brush.linearGradient(listOf(colors.ambientAccentDim, colors.ambientAccent.copy(alpha = 0.08f)))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF1E1E26), Color(0xFF16161C)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected && isPlaying) {
                        AnimatedWaveformBars(isAnimating = true)
                    } else {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Pause else Icons.Default.MusicNote,
                            contentDescription = if (isSelected) "Pause" else "Track",
                            tint = if (isSelected) Color.Black else colors.ambientAccent.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Center: Track title + artist subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.5.sp
                            ),
                            color = if (isSelected) colors.ambientOnSurface else colors.ambientOnSurface.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (track.isYouTube) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFF0000).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "YT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = Color(0xFFFF4444),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        if (isFavorite) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorited",
                                tint = colors.ambientAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = if (isSelected) colors.ambientAccent else colors.ambientOnSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Single overflow menu (3-dot) — 48dp touch target
                Box {
                    IconButton(
                        onClick = { showOverflowMenu = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("overflow_btn_${track.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Track options",
                            tint = if (isSelected) colors.ambientOnSurface else colors.ambientOnSurfaceMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Context menu dropdown
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onToggleFavorite()
                                showOverflowMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Add to Collection",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onAddToCollection()
                                showOverflowMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        if (onDelete != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Remove Track",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE11D48)
                                    )
                                },
                                onClick = {
                                    onDelete()
                                    showOverflowMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFE11D48)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

// =========================================================================
// ANIMATED WAVEFORM BARS (Phase 1 Visual Polish)
// =========================================================================

@Composable
internal fun AnimatedWaveformBars(
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color.Black
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(18.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((16 * if (isAnimating) bar1 else 0.4f).dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((16 * if (isAnimating) bar2 else 0.8f).dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((16 * if (isAnimating) bar3 else 0.5f).dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(barColor)
        )
    }
}

// =========================================================================
// PHASE 7.1: SKELETON LOADING ROW
// =========================================================================

/**
 * Phase 7 Milestone 7.1: Skeleton loading row for track lists.
 * Displays shimmer animation with gradient sweep during content loading.
 * 68dp height matching actual track rows.
 */
@Composable
private fun TrackRowSkeleton(
    modifier: Modifier = Modifier
) {
    val colors = AmbientTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerBrush = Brush.horizontalGradient(
        colors = listOf(
            colors.ambientSurfaceVariant,
            colors.ambientSurface,
            colors.ambientSurfaceVariant
        ),
        startX = shimmerOffset,
        endX = shimmerOffset + 400f
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.ambientSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skeleton icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(shimmerBrush)
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Skeleton title and subtitle
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Skeleton overflow icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

// =========================================================================
// CREATE / EDIT COLLECTION DIALOG
// =========================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateOrEditCollectionDialog(
    existingCollection: TrackCollection?,
    allAvailableTracks: List<FocusTrack>,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, trackIds: List<String>, mode: CollectionPlaybackMode, colorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf(existingCollection?.name ?: "") }
    var description by remember { mutableStateOf(existingCollection?.description ?: "") }
    var selectedMode by remember { mutableStateOf(existingCollection?.playbackMode ?: CollectionPlaybackMode.LOOP_COLLECTION) }
    var selectedColor by remember { mutableStateOf(existingCollection?.accentColorHex ?: COLLECTION_PALETTE.first()) }
    val selectedTrackIds = remember {
        mutableStateListOf<String>().apply {
            if (existingCollection != null) {
                addAll(existingCollection.trackIds)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141418),
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = if (existingCollection != null) "Edit Collection" else "New Track Collection",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name") },
                    placeholder = { Text("e.g. Famous Coding Loops", color = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("collection_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(selectedColor),
                        unfocusedBorderColor = DarkOutline
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g. Deep focus soundscape mix", color = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(selectedColor),
                        unfocusedBorderColor = DarkOutline
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Playback Mode Selector
                Text(
                    text = "Playback Mode",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFD4D4D8)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CollectionPlaybackMode.values().forEach { mode ->
                        val isModeSelected = selectedMode == mode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isModeSelected) Color(selectedColor).copy(alpha = 0.18f) else Color(0xFF1E1E26),
                            border = BorderStroke(
                                1.dp,
                                if (isModeSelected) Color(selectedColor) else Color(0xFF2B2B36)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMode = mode }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        CollectionPlaybackMode.LOOP_COLLECTION -> Icons.Default.Repeat
                                        CollectionPlaybackMode.LOOP_SINGLE -> Icons.Default.RepeatOne
                                        CollectionPlaybackMode.PLAY_ONCE -> Icons.Default.PlayArrow
                                        CollectionPlaybackMode.PLAY_COLLECTION_ONCE -> Icons.Default.PlayArrow
                                    },
                                    contentDescription = null,
                                    tint = if (isModeSelected) Color(selectedColor) else Color(0xFF9E9EA8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = mode.displayName,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isModeSelected) Color.White else Color(0xFFD4D4D8)
                                    )
                                    Text(
                                        text = mode.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                        color = Color(0xFF888892)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Accent Color Selector
                Text(
                    text = "Accent Color",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFD4D4D8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    COLLECTION_PALETTE.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .clickable { selectedColor = colorHex }
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Tracks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Included Tracks (${selectedTrackIds.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD4D4D8)
                    )
                    Text(
                        text = "Tap to toggle",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF888892)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF191920))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allAvailableTracks.forEach { track ->
                        val isIncluded = selectedTrackIds.contains(track.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isIncluded) Color(selectedColor).copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    if (isIncluded) selectedTrackIds.remove(track.id) else selectedTrackIds.add(track.id)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isIncluded) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isIncluded) Color(selectedColor) else Color(0xFF6E6E78),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isIncluded) Color.White else Color(0xFFBBBBC6)
                                )
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF7E7E8A)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = name.ifBlank { "My Collection" }
                    val finalTracks = if (selectedTrackIds.isEmpty() && allAvailableTracks.isNotEmpty()) {
                        listOf(allAvailableTracks.first().id)
                    } else {
                        selectedTrackIds.toList()
                    }
                    onSave(finalName, description, finalTracks, selectedMode, selectedColor)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(selectedColor),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_collection_button")
            ) {
                Text(
                    text = if (existingCollection != null) "Update Collection" else "Create Collection",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

// =========================================================================
// ADD TO COLLECTION BOTTOM SHEET / DIALOG
// =========================================================================

@Composable
private fun AddToCollectionBottomSheet(
    track: FocusTrack,
    collections: List<TrackCollection>,
    onDismiss: () -> Unit,
    onAddToCollection: (collectionId: String) -> Unit,
    onCreateNewCollection: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141418),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add \"${track.title}\" to Collection",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                if (collections.isEmpty()) {
                    Text(
                        text = "You don't have any collections created yet. Create one now to organize your favorite tracks!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF9E9EA8),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "Choose a collection:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFFD4D4D8),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        collections.forEach { col ->
                            val isAlreadyIn = col.trackIds.contains(track.id)
                            val accent = Color(col.accentColorHex)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E1E26),
                                border = BorderStroke(1.dp, if (isAlreadyIn) accent else Color(0xFF282834)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isAlreadyIn) { onAddToCollection(col.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(accent)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = col.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${col.trackIds.size} tracks · ${col.playbackMode.displayName}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = Color(0xFF888892)
                                            )
                                        }
                                    }

                                    if (isAlreadyIn) {
                                        Text(
                                            text = "ADDED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            color = accent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCreateNewCollection,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FocusAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ New Collection", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        }
    )
}



