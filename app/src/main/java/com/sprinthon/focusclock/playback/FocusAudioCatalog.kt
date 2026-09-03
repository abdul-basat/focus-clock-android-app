package com.sprinthon.focusclock.playback

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sprinthon.focusclock.domain.model.FocusTrack

class YouTubeStreamResolutionException(message: String) : Exception(message)

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

    /**
     * Creates a MediaItem for the given track.
     * Note: For YouTube tracks, resolves the direct audio stream asynchronously.
     * If resolution fails, throws [YouTubeStreamResolutionException] to prevent silent fallback to ambient audio.
     */
    suspend fun createMediaItem(context: Context, track: FocusTrack, forceRefreshStream: Boolean = false): MediaItem {
        val fileUri: Uri = if (track.isBuiltIn) {
            AudioFileHelper.getOrCreateTrackUri(context, track.id)
        } else if (track.isYouTube) {
            // Resolve direct playable stream URL via Innertube client contexts without API keys
            val streamUrl = YouTubeStreamHelper.resolveAudioStreamUrl(track.uri, forceRefresh = forceRefreshStream)
            if (!streamUrl.isNullOrBlank()) {
                Uri.parse(streamUrl)
            } else {
                throw YouTubeStreamResolutionException("Unable to resolve audio stream for YouTube track '${track.title}'. Please check internet connection or retry.")
            }
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

        Log.d("FocusAudioCatalog", "[DIAGNOSTIC] Created MediaItem: trackId=${track.id}, title='${track.title}', isYouTube=${track.isYouTube}, scheme=${fileUri.scheme}, host=${fileUri.host}, uriLength=${fileUri.toString().length}")

        return MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(fileUri)
            .setMediaMetadata(metadata)
            .build()
    }

    suspend fun createMediaItemOrNull(context: Context, track: FocusTrack, forceRefreshStream: Boolean = false): MediaItem? {
        return try {
            createMediaItem(context, track, forceRefreshStream)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllMediaItems(context: Context): List<MediaItem> {
        return BUILT_IN_TRACKS.mapNotNull { createMediaItemOrNull(context, it) }
    }
}
