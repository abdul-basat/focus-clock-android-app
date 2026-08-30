package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.components.SettingsCard
import com.sprinthon.focusclock.ui.components.SettingsSectionHeader
import com.sprinthon.focusclock.ui.components.SettingsToggleRow
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundSettingsScreen(
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    customTracks: List<com.sprinthon.focusclock.domain.model.FocusTrack>,
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
    val context = androidx.compose.ui.platform.LocalContext.current

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore if provider doesn't support it
            }
            val title = trackTitleForLocal.ifBlank { "Local Track" }
            onAddCustomTrack(uri.toString(), title, false)
            showAddTrackDialog = false
        }
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(arrayOf("audio/*"))
        } else {
            android.widget.Toast.makeText(context, "Permission required to access audio files", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val launchLocalPicker = { title: String ->
        trackTitleForLocal = title
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launcher.launch(arrayOf("audio/*"))
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0C0E),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ambient Sound",
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
            SettingsSectionHeader(title = "Soundscape Catalog")
            SettingsCard {
                val allTracks = FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
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
                            IconButton(onClick = { onDeleteCustomTrack(track.id) }) {
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddTrackDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Track",
                        tint = FocusAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Custom Track",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = FocusAmber
                    )
                }
            }

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

            SettingsSectionHeader(title = "Ambient Volume")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF141417),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Output Level",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                        Text(
                            text = "${(preferences.musicVolume * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FocusAmber
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                            contentDescription = null,
                            tint = Color(0xFF888892),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = preferences.musicVolume,
                            onValueChange = onVolumeChange,
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = FocusAmber,
                                activeTrackColor = FocusAmber,
                                inactiveTrackColor = Color(0xFF2C2C35)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("audio_volume_slider")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = Color(0xFF888892),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141417),
        title = {
            Text(text = "Add Custom Track", color = Color.White)
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    androidx.compose.material3.FilterChip(
                        selected = !isYouTubeMode,
                        onClick = { isYouTubeMode = false },
                        label = { Text("Local File") }
                    )
                    androidx.compose.material3.FilterChip(
                        selected = isYouTubeMode,
                        onClick = { isYouTubeMode = true },
                        label = { Text("YouTube URL") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = trackTitle,
                    onValueChange = { trackTitle = it },
                    label = { Text("Track Title (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = FocusAmber
                    )
                )

                if (isYouTubeMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        label = { Text("YouTube URL") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = FocusAmber
                        )
                    )
                    Text(
                        text = "Note: Background playback for YouTube may be limited by system restrictions. For best results, use standard audio URLs.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    if (isYouTubeMode) {
                        if (youtubeUrl.isNotBlank()) {
                            val title = trackTitle.ifBlank { "YouTube Track" }
                            onAddYouTubeTrack(youtubeUrl, title)
                        }
                    } else {
                        onLaunchLocalPicker(trackTitle)
                    }
                }
            ) {
                Text(if (isYouTubeMode) "Add" else "Choose File", color = FocusAmber)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
