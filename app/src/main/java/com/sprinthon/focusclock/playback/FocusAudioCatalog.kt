package com.sprinthon.focusclock.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sprinthon.focusclock.domain.model.FocusTrack

object FocusAudioCatalog {

    val BUILT_IN_TRACKS = listOf(
        FocusTrack(
            id = "deep_focus",
            title = "Deep Focus",
            artist = "432Hz Ambient Warmth",
            uri = "focus://track/deep_focus",
            isBuiltIn = true
        ),
        FocusTrack(
            id = "gentle_rain",
            title = "Gentle Rain",
            artist = "Natural Pink Noise",
            uri = "focus://track/gentle_rain",
            isBuiltIn = true
        ),
        FocusTrack(
            id = "white_noise",
            title = "Smooth Noise",
            artist = "Steady Concentration",
            uri = "focus://track/white_noise",
            isBuiltIn = true
        ),
        FocusTrack(
            id = "ambient_flow",
            title = "Ambient Flow",
            artist = "Binaural Meditation",
            uri = "focus://track/ambient_flow",
            isBuiltIn = true
        ),
        FocusTrack(
            id = "forest_waves",
            title = "Forest Waves",
            artist = "Theta Harmonic Flow",
            uri = "focus://track/forest_waves",
            isBuiltIn = true
        )
    )

    fun getTrackById(id: String): FocusTrack {
        return BUILT_IN_TRACKS.find { it.id == id } ?: BUILT_IN_TRACKS.first()
    }

    suspend fun createMediaItem(context: Context, track: FocusTrack): MediaItem {
        val fileUri = if (track.isBuiltIn) {
            AudioFileHelper.getOrCreateTrackUri(context, track.id)
        } else if (track.isYouTube) {
            // Provide a silent or empty URI for YouTube tracks so Media3 doesn't crash 
            // while we handle YouTube visually. Or just use the original URI and let it fail gracefully.
            // Let's use a dummy generated silent URI or just the original URI if it's safe.
            // Actually, if we just use the original URI, Media3 might throw HttpDataSourceException, 
            // but we can just provide a dummy silent URI by passing a special ID.
            AudioFileHelper.getOrCreateTrackUri(context, "silent_dummy")
        } else {
            try {
                Uri.parse(track.uri)
            } catch (e: Exception) {
                AudioFileHelper.getOrCreateTrackUri(context, track.id)
            }
        }
        
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setDisplayTitle(track.title)
            .build()

        return MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(fileUri)
            .setMediaMetadata(metadata)
            .build()
    }

    suspend fun getAllMediaItems(context: Context): List<MediaItem> {
        return BUILT_IN_TRACKS.map { createMediaItem(context, it) }
    }
}
