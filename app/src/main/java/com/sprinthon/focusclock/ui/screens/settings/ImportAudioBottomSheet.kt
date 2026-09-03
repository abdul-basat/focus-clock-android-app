package com.sprinthon.focusclock.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.playback.YouTubeStreamHelper
import com.sprinthon.focusclock.playback.YouTubeVideoMetadata
import com.sprinthon.focusclock.ui.theme.AmbientTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Phase 5 Milestone 5.1 & 5.2: Streamlined Import Audio Modal Bottom Sheet.
 *
 * Replaces legacy multi-step dialog with an effortless, high-fidelity Material 3 sheet
 * providing two primary pathways:
 * 1. Device Audio Files (Mass SAF file picker + full directory scanner)
 * 2. YouTube Audio Link (Auto-paste, real-time regex validation, automatic metadata extraction)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImportAudioBottomSheet(
    onDismiss: () -> Unit,
    onLaunchLocalPicker: (String) -> Unit,
    onLaunchFolderPicker: () -> Unit,
    onAddYouTubeTrack: (url: String, title: String) -> Unit,
    isScanningFolder: Boolean = false,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = AmbientTheme.colors
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // 0: Local Device Files, 1: YouTube Audio Link
    var selectedTab by remember { mutableIntStateOf(0) }

    // Form states
    var localTitlePrefix by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var customTrackTitle by remember { mutableStateOf("") }

    // YouTube metadata & validation state
    var isExtractingMetadata by remember { mutableStateOf(false) }
    var extractedMetadata by remember { mutableStateOf<YouTubeVideoMetadata?>(null) }
    var metadataJob by remember { mutableStateOf<Job?>(null) }
    var isPlaylistUrl by remember { mutableStateOf(false) }
    var importError by remember { mutableStateOf<String?>(null) }

    // Real-time URL validation
    val isValidYouTubeUrl = remember(youtubeUrl) {
        val trimmed = youtubeUrl.trim()
        if (trimmed.isBlank()) null
        else {
            val videoId = YouTubeStreamHelper.extractVideoId(trimmed)
            val playlistId = YouTubeStreamHelper.extractPlaylistId(trimmed)
            isPlaylistUrl = playlistId != null && videoId == null
            videoId != null || playlistId != null
        }
    }

    // Auto-fetch YouTube metadata on valid URL input
    LaunchedEffect(youtubeUrl) {
        val trimmed = youtubeUrl.trim()
        val videoId = YouTubeStreamHelper.extractVideoId(trimmed)
        metadataJob?.cancel()

        if (videoId != null) {
            metadataJob = coroutineScope.launch {
                delay(400) // Debounce typing
                isExtractingMetadata = true
                importError = null
                try {
                    val meta = YouTubeStreamHelper.fetchVideoMetadata(trimmed)
                    extractedMetadata = meta
                    if (meta != null && customTrackTitle.isBlank()) {
                        customTrackTitle = meta.title
                    } else if (meta == null) {
                        importError = "Failed to fetch video metadata. Check your connection."
                    }
                } catch (e: Exception) {
                    extractedMetadata = null
                    importError = "Import failed — check your connection"
                } finally {
                    isExtractingMetadata = false
                }
            }
        } else {
            extractedMetadata = null
            isExtractingMetadata = false
            importError = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.ambientSurface,
        contentColor = colors.ambientOnSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = colors.ambientAccent.copy(alpha = 0.35f)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Import Custom Audio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = colors.ambientOnSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Add offline files from your device or stream from YouTube",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = colors.ambientOnSurfaceMuted
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.ambientOnSurfaceMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // =========================================================================
            // PATHWAY SELECTOR (Pill Switcher)
            // =========================================================================
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.ambientSurfaceVariant,
                border = BorderStroke(1.dp, colors.ambientOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Local Device Files Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedTab == 0) colors.ambientAccent else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable(role = Role.Tab) { selectedTab = 0 }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color.Black else colors.ambientOnSurfaceMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Device Audio",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (selectedTab == 0) Color.Black else colors.ambientOnSurface
                            )
                        }
                    }

                    // YouTube Audio Link Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedTab == 1) colors.ambientAccent else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable(role = Role.Tab) { selectedTab = 1 }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.Black else colors.ambientOnSurfaceMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YouTube Link",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (selectedTab == 1) Color.Black else colors.ambientOnSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // PATHWAY 1: LOCAL DEVICE FILES
            // =========================================================================
            if (selectedTab == 0) {
                // Optional Title Prefix field
                OutlinedTextField(
                    value = localTitlePrefix,
                    onValueChange = { localTitlePrefix = it },
                    label = { Text("Custom Title Prefix (Optional)") },
                    placeholder = {
                        Text(
                            "Auto-detects from filename if empty",
                            color = colors.ambientOnSurfaceMuted.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.ambientOnSurface,
                        unfocusedTextColor = colors.ambientOnSurface,
                        focusedBorderColor = colors.ambientAccent,
                        unfocusedBorderColor = colors.ambientOutline,
                        focusedLabelColor = colors.ambientAccent,
                        unfocusedLabelColor = colors.ambientOnSurfaceMuted
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Card 1: Mass SAF File Picker
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.ambientSurfaceVariant,
                    border = BorderStroke(1.dp, colors.ambientOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) {
                            onLaunchLocalPicker(localTitlePrefix)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colors.ambientAccentDim,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = colors.ambientAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select Audio Files",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = colors.ambientOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pick single or multiple audio files directly from device storage",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = colors.ambientOnSurfaceMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Card 2: Full Folder / Directory Scanner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.ambientSurfaceVariant,
                    border = BorderStroke(1.dp, colors.ambientOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button, enabled = !isScanningFolder) {
                            onLaunchFolderPicker()
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = colors.ambientAccentDim,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = colors.ambientAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Import Entire Folder",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = colors.ambientOnSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Recursively scans chosen directory and subfolders for audio",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = colors.ambientOnSurfaceMuted
                                )
                            }
                        }

                        if (isScanningFolder) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = colors.ambientAccent,
                                trackColor = colors.ambientOutline
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Scanning folder for audio tracks...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = colors.ambientAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Supported Formats Pills
                Text(
                    text = "Supported Formats",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = colors.ambientOnSurfaceMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val formats = listOf("MP3", "WAV", "M4A", "FLAC", "OGG", "AAC", "OPUS")
                    formats.forEach { fmt ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = colors.ambientSurfaceVariant,
                            border = BorderStroke(0.5.dp, colors.ambientOutline)
                        ) {
                            Text(
                                text = fmt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.ambientOnSurfaceMuted,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // PATHWAY 2: YOUTUBE AUDIO LINK
            // =========================================================================
            if (selectedTab == 1) {
                // URL input with Paste & Clear buttons
                OutlinedTextField(
                    value = youtubeUrl,
                    onValueChange = { youtubeUrl = it },
                    label = { Text("YouTube URL") },
                    placeholder = {
                        Text(
                            "https://youtube.com/watch?v=... or youtu.be/...",
                            color = colors.ambientOnSurfaceMuted.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = colors.ambientAccent
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (youtubeUrl.isNotBlank()) {
                                IconButton(onClick = {
                                    youtubeUrl = ""
                                    extractedMetadata = null
                                    customTrackTitle = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear URL",
                                        tint = colors.ambientOnSurfaceMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        val clipText = clipboardManager.getText()?.text
                                        if (!clipText.isNullOrBlank()) {
                                            youtubeUrl = clipText.trim()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste from clipboard",
                                        tint = colors.ambientAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    isError = isValidYouTubeUrl == false,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("youtube_url_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.ambientOnSurface,
                        unfocusedTextColor = colors.ambientOnSurface,
                        focusedBorderColor = if (isValidYouTubeUrl == true) colors.ambientAccent else colors.ambientAccent,
                        unfocusedBorderColor = colors.ambientOutline,
                        errorBorderColor = colors.ambientError,
                        focusedLabelColor = colors.ambientAccent,
                        unfocusedLabelColor = colors.ambientOnSurfaceMuted
                    )
                )

                // Inline validation feedback
                if (importError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = colors.ambientError,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = importError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = colors.ambientError
                        )
                    }
                } else if (isValidYouTubeUrl == false) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = colors.ambientError,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Please enter a valid YouTube video or playlist link",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = colors.ambientError
                        )
                    }
                } else if (isValidYouTubeUrl == true) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.ambientAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPlaylistUrl) "Valid YouTube playlist link detected" else "Valid YouTube video link detected",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = colors.ambientAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input (Auto-filled from metadata or editable)
                OutlinedTextField(
                    value = customTrackTitle,
                    onValueChange = { customTrackTitle = it },
                    label = { Text("Track Title") },
                    placeholder = {
                        Text(
                            "Auto-detected from YouTube",
                            color = colors.ambientOnSurfaceMuted.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (isExtractingMetadata) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = colors.ambientAccent
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.ambientOnSurface,
                        unfocusedTextColor = colors.ambientOnSurface,
                        focusedBorderColor = colors.ambientAccent,
                        unfocusedBorderColor = colors.ambientOutline,
                        focusedLabelColor = colors.ambientAccent,
                        unfocusedLabelColor = colors.ambientOnSurfaceMuted
                    )
                )

                // Metadata Preview Card (Milestone 5.2)
                AnimatedVisibility(
                    visible = extractedMetadata != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    extractedMetadata?.let { meta ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.ambientSurfaceVariant,
                            border = BorderStroke(1.dp, colors.ambientOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = colors.ambientAccentDim,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SmartDisplay,
                                            contentDescription = null,
                                            tint = colors.ambientAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meta.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        ),
                                        color = colors.ambientOnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${meta.author} · Auto-detected from YouTube",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = colors.ambientAccent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Single-tap Confirm Button
                Button(
                    onClick = {
                        val finalTitle = customTrackTitle.ifBlank {
                            extractedMetadata?.title ?: "YouTube Audio Stream"
                        }
                        onAddYouTubeTrack(youtubeUrl.trim(), finalTitle)
                    },
                    enabled = isValidYouTubeUrl == true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.ambientAccent,
                        contentColor = Color.Black,
                        disabledContainerColor = colors.ambientSurfaceVariant,
                        disabledContentColor = colors.ambientOnSurfaceMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_youtube_track_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlaylistUrl) "Add YouTube Playlist" else "Add YouTube Track",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
