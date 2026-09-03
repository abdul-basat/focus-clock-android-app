package com.sprinthon.focusclock.analytics

import android.util.Log

/**
 * Phase 8 Milestone 8.3: UX Analytics Event Definitions and Tracker.
 * Instruments key user actions across the Ambient Soundscape screen to enable
 * data-driven validation of redesign effectiveness.
 */
object AmbientAnalytics {

    private const val TAG = "AmbientAnalytics"

    // Listeners for external telemetry / Firebase if configured
    var onEventLogged: ((eventName: String, params: Map<String, Any>) -> Unit)? = null

    /**
     * Dispatches an event to attached listeners and logs to Logcat.
     */
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "Event: $eventName, Params: $params")
        onEventLogged?.invoke(eventName, params)
    }

    // --- Specific Event Helpers ---

    fun logScreenOpened() {
        logEvent("ambient_screen_opened")
    }

    fun logFirstPlay(trackId: String, timeToFirstPlayMs: Long) {
        logEvent(
            "ambient_first_play",
            mapOf("track_id" to trackId, "time_to_play_ms" to timeToFirstPlayMs)
        )
    }

    fun logTrackPlayed(trackId: String, trackTitle: String, isYouTube: Boolean) {
        logEvent(
            "ambient_track_played",
            mapOf("track_id" to trackId, "title" to trackTitle, "is_youtube" to isYouTube)
        )
    }

    fun logFilterChanged(filterName: String) {
        logEvent("ambient_filter_changed", mapOf("filter" to filterName))
    }

    fun logGestureUsed(gestureType: String, trackId: String) {
        logEvent(
            "ambient_gesture_used",
            mapOf("gesture" to gestureType, "track_id" to trackId)
        )
    }

    fun logImportStarted(importType: String) {
        logEvent("ambient_import_started", mapOf("type" to importType))
    }

    fun logImportCompleted(trackTitle: String, isYouTube: Boolean, count: Int = 1) {
        logEvent(
            "ambient_import_completed",
            mapOf("title" to trackTitle, "is_youtube" to isYouTube, "count" to count)
        )
    }

    fun logImportFailed(errorReason: String) {
        logEvent("ambient_import_failed", mapOf("reason" to errorReason))
    }

    fun logSettingsOpened() {
        logEvent("ambient_settings_opened")
    }

    fun logScrollDepth(maxScrollPx: Int) {
        logEvent("ambient_scroll_depth", mapOf("max_scroll_px" to maxScrollPx))
    }

    fun logSessionDuration(durationSeconds: Long) {
        logEvent("ambient_session_duration", mapOf("duration_sec" to durationSeconds))
    }
}
