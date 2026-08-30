package com.sprinthon.focusclock.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusTrack
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber

/**
 * Dedicated Soundscape & Music Player customizer screen (Pillar 2).
 * Allows users to browse built-in soundscapes, import local audio files via SAF,
 * add YouTube streaming URLs, audition preview playback, and configure volume/loop behaviors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundSettingsScreen(
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    customTracks: List<FocusTrack>,
    onSelectTrack: (String, Boolean) -> Unit,
    onAddCustomTrack: (String, String, Boolean) -> Unit,
    onDeleteCustomTrack: (String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleLoop: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleShowWaveform: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var trackTitleForLocal by remember { mutableStateOf("") }
    val context = LocalContext.current

    val allTracks = remember(customTracks) {
        FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
    }
    val activeTrack = allTracks.find { it.id == preferences.selectedTrackId } ?: allTracks.first()

    // SAF Local File Picker Launcher with Persistable Permissions
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Provider may not support persistable permissions
            }
            val title = trackTitleForLocal.ifBlank { "Local Track" }
            onAddCustomTrack(uri.toString(), title, false)
            showAddTrackDialog = false
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
                            text = "Soundscape & Music",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${activeTrack.title} · ${if (playerState.isPlaying) "Auditioning" else "Ready"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = FocusAmber
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
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ==========================================
                // 1. ACTIVE TRACK & MASTER OUTPUT HERO CARD
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF141418),
                    border = BorderStroke(1.dp, Color(0xFF262630)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (playerState.isPlaying) FocusAmber else Color(0xFF22222A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (playerState.isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (playerState.isPlaying) Color.Black else FocusAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeTrack.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = Color.White
                                    )
                                    Text(
                                        text = activeTrack.artist,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = FocusAmber
                                    )
                                }
                            }

                            // Quick Master Play/Pause Audition Button
                            Surface(
                                shape = CircleShape,
                                color = FocusAmber,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable(
                                        role = Role.Button,
                                        onClick = {
                                            if (preferences.selectedTrackId == activeTrack.id && playerState.isPlaying) {
                                                onTogglePlayPause()
                                            } else {
                                                onSelectTrack(activeTrack.id, true)
                                            }
                                        }
                                    )
                                    .testTag("master_preview_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF24242E), thickness = 0.75.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Master Volume Slider
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

                        Spacer(modifier = Modifier.height(6.dp))

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

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 2. SOUNDSCAPE CATALOG & CUSTOM TRACKS
                // ==========================================
                SettingsSectionHeader(title = "Soundscape Catalog")
                SettingsCard {
                    allTracks.forEachIndexed { index, track ->
                        val isSelected = preferences.selectedTrackId == track.id
                        val isCurrentlyPlaying = isSelected && playerState.isPlaying

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.RadioButton,
                                    onClick = { onSelectTrack(track.id, false) }
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("track_row_${track.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Play/Pause icon button for instant preview
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) FocusAmber else Color(0xFF222228),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(
                                        role = Role.Button,
                                        onClick = {
                                            if (isSelected) {
                                                onTogglePlayPause()
                                            } else {
                                                onSelectTrack(track.id, true)
                                            }
                                        }
                                    )
                                    .minimumInteractiveComponentSize()
                                    .testTag("preview_btn_${track.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isCurrentlyPlaying) "Pause" else "Play",
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        fontSize = 15.sp
                                    ),
                                    color = if (isSelected) Color.White else Color(0xFFD4D4D8)
                                )
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = if (isSelected) FocusAmber else Color(0xFF888892)
                                )
                            }

                            if (!track.isBuiltIn) {
                                IconButton(
                                    onClick = { onDeleteCustomTrack(track.id) },
                                    modifier = Modifier.testTag("delete_track_${track.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Track",
                                        tint = Color(0xFF888892)
                                    )
                                }
                            }

                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FocusAmber.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "SELECTED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = FocusAmber,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (index < allTracks.size - 1) {
                            HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)

                    // Add Custom Track Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddTrackDialog = true }
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

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 3. PLAYBACK BEHAVIORS & PREFERENCES
                // ==========================================
                SettingsSectionHeader(title = "Playback Preferences")
                SettingsCard {
                    SettingsToggleRow(
                        title = "Auto-Play on Focus Start",
                        subtitle = "Automatically start ambient audio when you begin a session",
                        icon = Icons.Default.PlayArrow,
                        checked = preferences.autoPlayMusicOnFocus,
                        testTag = "toggle_auto_play",
                        onCheckedChange = onToggleAutoPlay
                    )
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                    SettingsToggleRow(
                        title = "Loop Ambient Audio",
                        subtitle = "Continuously repeat soundscape during your session",
                        icon = Icons.Default.Repeat,
                        checked = preferences.musicLoop,
                        testTag = "toggle_audio_loop",
                        onCheckedChange = onToggleLoop
                    )
                    HorizontalDivider(color = Color(0xFF1F1F24), thickness = 0.75.dp)
                    SettingsToggleRow(
                        title = "Audio Waveform Visualizer",
                        subtitle = "Display subtle ambient waveform animation on focus canvas",
                        icon = Icons.Default.GraphicEq,
                        checked = preferences.showWaveform,
                        testTag = "toggle_show_waveform",
                        onCheckedChange = onToggleShowWaveform
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    if (showAddTrackDialog) {
        AddCustomTrackDialog(
            onDismiss = { showAddTrackDialog = false },
            onLaunchLocalPicker = { title ->
                launchLocalPicker(title)
            },
            onAddYouTubeTrack = { url, title ->
                onAddCustomTrack(url, title, true)
                showAddTrackDialog = false
            }
        )
    }
}

@Composable
private fun AddCustomTrackDialog(
    onDismiss: () -> Unit,
    onLaunchLocalPicker: (String) -> Unit,
    onAddYouTubeTrack: (String, String) -> Unit
) {
    var isYouTubeMode by remember { mutableStateOf(false) }
    var youtubeUrl by remember { mutableStateOf("") }
    var trackTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141417),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Custom Audio Track",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = !isYouTubeMode,
                        onClick = { isYouTubeMode = false },
                        label = { Text("Local File") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FocusAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isYouTubeMode,
                        onClick = { isYouTubeMode = true },
                        label = { Text("YouTube URL") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FocusAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = trackTitle,
                    onValueChange = { trackTitle = it },
                    label = { Text("Track Title (Optional)") },
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

                if (isYouTubeMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        label = { Text("YouTube Video URL") },
                        placeholder = { Text("https://youtube.com/watch?v=...", color = Color.Gray) },
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
                        text = "YouTube tracks stream directly during your focus sessions.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isYouTubeMode) {
                        if (youtubeUrl.isNotBlank()) {
                            val title = trackTitle.ifBlank { "YouTube Track" }
                            onAddYouTubeTrack(youtubeUrl, title)
                        }
                    } else {
                        onLaunchLocalPicker(trackTitle)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FocusAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isYouTubeMode) "Add Track" else "Choose File",
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
