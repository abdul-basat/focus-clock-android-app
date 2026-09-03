package com.sprinthon.focusclock.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
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
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

private enum class AmbientTab(val title: String) {
    ALL("All Sounds"),
    COLLECTIONS("Collections"),
    SOUNDSCAPES("Soundscapes"),
    CUSTOM("Custom Audio")
}

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
    var selectedTab by remember { mutableStateOf(AmbientTab.ALL) }
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var collectionToEdit by remember { mutableStateOf<TrackCollection?>(null) }
    var trackForAddToCollection by remember { mutableStateOf<FocusTrack?>(null) }
    var trackTitleForLocal by remember { mutableStateOf("") }
    var isScanningFolder by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val allTracks = remember(customTracks) {
        FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
    }
    val activeTrack = allTracks.find { it.id == preferences.selectedTrackId } ?: allTracks.first()
    val activeCollection = collections.find { it.id == preferences.activeCollectionId }

    // SAF Local File Picker Launcher (Bulk / Multiple Files)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            var addedCount = 0
            uris.forEachIndexed { index, uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Provider may not support persistable permissions
                }
                
                var fileName = ""
                try {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) {
                                fileName = it.getString(nameIndex).substringBeforeLast(".")
                            }
                        }
                    }
                } catch (e: Exception) {}

                val title = if (fileName.isNotBlank()) {
                    fileName
                } else if (trackTitleForLocal.isNotBlank()) {
                    if (uris.size > 1) "$trackTitleForLocal ${index + 1}" else trackTitleForLocal
                } else {
                    if (uris.size > 1) "Local Track ${index + 1}" else "Local Track"
                }
                onAddCustomTrack(uri.toString(), title, false)
                addedCount++
            }
            if (addedCount > 1) {
                Toast.makeText(context, "Successfully added $addedCount tracks", Toast.LENGTH_SHORT).show()
            } else if (addedCount == 1) {
                Toast.makeText(context, "Track added to library", Toast.LENGTH_SHORT).show()
            }
            showAddTrackDialog = false
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
                
                if (scannedTracks.isEmpty()) {
                    Toast.makeText(context, "No audio files found in selected folder", Toast.LENGTH_LONG).show()
                } else {
                    scannedTracks.forEach { scanned ->
                        try {
                            context.contentResolver.takePersistableUriPermission(
                                scanned.uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: Exception) {}
                        onAddCustomTrack(scanned.uri.toString(), scanned.displayName, false)
                    }
                    Toast.makeText(
                        context,
                        "Imported ${scannedTracks.size} tracks from folder!",
                        Toast.LENGTH_LONG
                    ).show()
                    showAddTrackDialog = false
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

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("ambient_sound_settings_screen"),
        containerColor = AmoledBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Soundscape & Collections",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = if (activeCollection != null) {
                                "Collection: ${activeCollection.name} · ${activeCollection.playbackMode.displayName}"
                            } else {
                                "${activeTrack.title} · ${if (playerState.isPlaying) "Auditioning" else "Ready"}"
                            },
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
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ==========================================
                // 1. HERO MASTER AUDITION & PLAYBACK CARD
                // ==========================================
                HeroAuditionCard(
                    activeTrack = activeTrack,
                    activeCollection = activeCollection,
                    playerState = playerState,
                    preferences = preferences,
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

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 2. CATEGORY TABS
                // ==========================================
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color(0xFF101014),
                    contentColor = FocusAmber,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = FocusAmber,
                            height = 2.5.dp
                        )
                    },
                    divider = {
                        HorizontalDivider(color = Color(0xFF22222A), thickness = 0.75.dp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AmbientTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) FocusAmber else Color(0xFF9E9EA8)
                                )
                            },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 3. TAB CONTENT
                // ==========================================
                when (selectedTab) {
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
                            onDeleteCustomTrack = onDeleteCustomTrack,
                            onAddCustomClick = { showAddTrackDialog = true }
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
                            onDeleteCustomTrack = onDeleteCustomTrack,
                            onAddCustomClick = { showAddTrackDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 4. PLAYBACK PREFERENCES & SYSTEM TOGGLES
                // ==========================================
                SettingsSectionHeader(title = "Playback Preferences")
                SettingsCard {
                    SettingsToggleRow(
                        title = "Auto-Play on Focus Start",
                        subtitle = "Automatically trigger ambient audio when starting a focus session",
                        icon = Icons.Default.PlayArrow,
                        checked = preferences.autoPlayMusicOnFocus,
                        testTag = "toggle_auto_play",
                        onCheckedChange = onToggleAutoPlay
                    )
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                    SettingsToggleRow(
                        title = "Loop Ambient Audio",
                        subtitle = "Continuously loop track or collection during session",
                        icon = Icons.Default.Repeat,
                        checked = preferences.musicLoop,
                        testTag = "toggle_audio_loop",
                        onCheckedChange = onToggleLoop
                    )
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                    SettingsToggleRow(
                        title = "Audio Waveform Visualizer",
                        subtitle = "Display animated sound waves on the active focus canvas",
                        icon = Icons.Default.GraphicEq,
                        checked = preferences.showWaveform,
                        testTag = "toggle_show_waveform",
                        onCheckedChange = onToggleShowWaveform
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Modal Dialogs
    if (showAddTrackDialog) {
        AddCustomTrackDialog(
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
            }
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
}

// =========================================================================
// HERO AUDITION CARD COMPONENT
// =========================================================================

@Composable
private fun HeroAuditionCard(
    activeTrack: FocusTrack,
    activeCollection: TrackCollection?,
    playerState: PlayerUiState,
    preferences: FocusPreferences,
    onTogglePlayPause: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onClearCollection: () -> Unit,
    onSelectPlaybackMode: (CollectionPlaybackMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF131318),
        border = BorderStroke(1.dp, Color(0xFF282834)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Collection Tag Badge if active
            if (activeCollection != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(activeCollection.accentColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(activeCollection.accentColorHex).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PLAYLIST: ${activeCollection.name.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Color(activeCollection.accentColorHex)
                            )
                        }
                    }

                    TextButton(
                        onClick = onClearCollection,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Play Solo Track",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = Color(0xFF9E9EA8)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Track Title & Equalizer Row
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
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                if (playerState.isPlaying) {
                                    Brush.linearGradient(listOf(FocusAmber, Color(0xFFFFB74D)))
                                } else {
                                    Brush.linearGradient(listOf(Color(0xFF202028), Color(0xFF181820)))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playerState.isPlaying) {
                            AnimatedWaveformBars(isAnimating = true)
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = FocusAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeTrack.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeTrack.isYouTube) "YouTube Audio Link" else activeTrack.artist,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = FocusAmber,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Master Play/Pause Audition Button
                Surface(
                    shape = CircleShape,
                    color = FocusAmber,
                    modifier = Modifier
                        .size(46.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = onTogglePlayPause
                        )
                        .testTag("master_preview_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF22222E), thickness = 0.75.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Playback Mode Quick Select Chips (Single / Loop 1 / Loop All)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mode:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF9E9EA8)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val currentMode = preferences.collectionPlaybackMode
                    CollectionPlaybackMode.values().forEach { mode ->
                        val isModeSelected = currentMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isModeSelected) FocusAmber.copy(alpha = 0.2f) else Color(0xFF1E1E26),
                            border = BorderStroke(
                                1.dp,
                                if (isModeSelected) FocusAmber else Color(0xFF2C2C38)
                            ),
                            modifier = Modifier.clickable { onSelectPlaybackMode(mode) }
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = if (isModeSelected) FocusAmber else Color(0xFFBBBBC6),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Volume Slider Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Master Soundscape Volume",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFFD4D4D8)
                )
                Text(
                    text = "${(preferences.musicVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FocusAmber
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (preferences.musicVolume > 0f) onVolumeChange(0f) else onVolumeChange(0.7f)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (preferences.musicVolume == 0f) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = "Mute",
                        tint = if (preferences.musicVolume == 0f) FocusAmber else Color(0xFF8E8E96),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Slider(
                    value = preferences.musicVolume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = FocusAmber,
                        activeTrackColor = FocusAmber,
                        inactiveTrackColor = Color(0xFF2C2C36)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("audio_volume_slider")
                )

                IconButton(
                    onClick = { onVolumeChange(1f) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Max Volume",
                        tint = Color(0xFF8E8E96),
                        modifier = Modifier.size(18.dp)
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
    onAddCustomClick: () -> Unit
) {
    Column {
        SettingsSectionHeader(title = "All Ambient Tracks (${allTracks.size})")
        SettingsCard {
            allTracks.forEachIndexed { index, track ->
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

                if (index < allTracks.size - 1) {
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                }
            }

            HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)

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
                    contentColor = FocusAmber
                ),
                border = BorderStroke(1.dp, FocusAmber.copy(alpha = 0.6f)),
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
        val favoriteTracks = allTracks.filter { favoriteTrackIds.contains(it.id) }
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
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131317),
                border = BorderStroke(1.dp, Color(0xFF22222A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFF6E6E78),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No custom collections yet",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFD4D4D8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Create collections to organize your famous tracks and set single or loop playback rules.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF888892),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onCreateCollectionClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FocusAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "+ Create First Collection",
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
    onAddToCollection: (FocusTrack) -> Unit
) {
    Column {
        SettingsSectionHeader(title = "Built-in Soundscapes (${builtInTracks.size})")
        SettingsCard {
            builtInTracks.forEachIndexed { index, track ->
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

                if (index < builtInTracks.size - 1) {
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
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
    onAddCustomClick: () -> Unit
) {
    Column {
        SettingsSectionHeader(title = "Custom Audio & Streaming Links (${customTracks.size})")
        SettingsCard {
            if (customTracks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFF6E6E78),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No custom audio files added",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFD4D4D8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Import local audio files via SAF or add streaming YouTube links.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF888892),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                customTracks.forEachIndexed { index, track ->
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

                    if (index < customTracks.size - 1) {
                        HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
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
// REUSABLE TRACK ROW ITEM COMPONENT
// =========================================================================

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.RadioButton,
                onClick = onSelect
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play/Pause icon button for instant preview
        IconButton(
            onClick = {
                android.util.Log.d("FocusClockApp", "[DIAGNOSTIC] Custom Audio Play clicked: trackId=${track.id}, title='${track.title}', isYouTube=${track.isYouTube}, isSelected=$isSelected")
                onPreviewToggle()
            },
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isSelected) FocusAmber else Color(0xFF222228))
                .testTag("preview_btn_${track.id}")
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 14.5.sp
                    ),
                    color = if (isSelected) Color.White else Color(0xFFD4D4D8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (track.isYouTube) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF0000).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "YouTube",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFFFF4444),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Text(
                text = track.artist,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = if (isSelected) FocusAmber else Color(0xFF888892),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Action Icons: Favorite, Add to Collection, Delete
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp).testTag("fav_btn_${track.id}")
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Favorited" else "Favorite",
                tint = if (isFavorite) Color(0xFFE11D48) else Color(0xFF71717A),
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = onAddToCollection,
            modifier = Modifier.size(32.dp).testTag("add_to_col_btn_${track.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to collection",
                tint = Color(0xFF9E9EA8),
                modifier = Modifier.size(18.dp)
            )
        }

        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp).testTag("delete_track_${track.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Track",
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = FocusAmber.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = FocusAmber,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// =========================================================================
// ANIMATED WAVEFORM BARS
// =========================================================================

@Composable
private fun AnimatedWaveformBars(isAnimating: Boolean) {
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
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((18 * if (isAnimating) bar1 else 0.4f).dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Black)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((18 * if (isAnimating) bar2 else 0.8f).dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Black)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((18 * if (isAnimating) bar3 else 0.5f).dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Black)
        )
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

// =========================================================================
// ADD CUSTOM TRACK DIALOG
// =========================================================================

@Composable
private fun AddCustomTrackDialog(
    onDismiss: () -> Unit,
    onLaunchLocalPicker: (String) -> Unit,
    onLaunchFolderPicker: () -> Unit,
    onAddYouTubeTrack: (String, String) -> Unit
) {
    var selectedMode by remember { mutableStateOf(0) } // 0: Bulk Files, 1: Full Folder, 2: YouTube
    var youtubeUrl by remember { mutableStateOf("") }
    var trackTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141417),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Custom Audio",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedMode == 0,
                        onClick = { selectedMode = 0 },
                        label = { Text("Bulk Files", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FocusAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMode == 1,
                        onClick = { selectedMode = 1 },
                        label = { Text("Full Folder", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FocusAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMode == 2,
                        onClick = { selectedMode = 2 },
                        label = { Text("YouTube", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FocusAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedMode) {
                    2 -> {
                        OutlinedTextField(
                            value = trackTitle,
                            onValueChange = { trackTitle = it },
                            label = { Text("Track Title (Optional)") },
                            placeholder = { Text("e.g. Deep Focus Ambient", color = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = FocusAmber,
                                unfocusedBorderColor = DarkOutline
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = youtubeUrl,
                            onValueChange = { youtubeUrl = it },
                            label = { Text("YouTube Video or Playlist URL") },
                            placeholder = { Text("https://youtube.com/watch?v=... or /playlist?list=...", color = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = FocusAmber,
                                unfocusedBorderColor = DarkOutline
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Supports single videos and full playlists with background audio streaming.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color.Gray
                        )
                    }
                    1 -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E1E24),
                            border = BorderStroke(1.dp, Color(0xFF33333E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = FocusAmber,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Import Whole Folder",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Select any directory on your device or SD card. All audio tracks found inside (and sub-folders) will be automatically scanned and imported.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFFA0A0AA)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        OutlinedTextField(
                            value = trackTitle,
                            onValueChange = { trackTitle = it },
                            label = { Text("Custom Title Prefix (Optional)") },
                            placeholder = { Text("Leave blank to use audio file names", color = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = FocusAmber,
                                unfocusedBorderColor = DarkOutline
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Select one or multiple audio tracks directly from device storage (MP3, WAV, M4A, FLAC, OGG, AAC, OPUS).",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color(0xFFB0B0B8)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (selectedMode) {
                        2 -> {
                            if (youtubeUrl.isNotBlank()) {
                                val title = trackTitle.ifBlank { "YouTube Stream" }
                                onAddYouTubeTrack(youtubeUrl, title)
                            }
                        }
                        1 -> {
                            onLaunchFolderPicker()
                        }
                        else -> {
                            onLaunchLocalPicker(trackTitle)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FocusAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = when (selectedMode) {
                        2 -> "Add YouTube Track"
                        1 -> "Select Folder"
                        else -> "Choose Audio File(s)"
                    },
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

