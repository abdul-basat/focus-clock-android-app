package com.sprinthon.focusclock.playback

import android.net.Uri
import android.util.Log
import com.sprinthon.focusclock.domain.model.FocusTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class YouTubeVideoMetadata(
    val videoId: String,
    val title: String,
    val author: String,
    val durationSeconds: Long = 0L,
    val thumbnailUrl: String = ""
)

data class YouTubePlaylistInfo(
    val playlistId: String,
    val title: String,
    val author: String,
    val tracks: List<FocusTrack>
)

object YouTubeStreamHelper {

    private const val TAG = "YouTubeStreamHelper"
    private const val USER_AGENT_ANDROID = "com.google.android.youtube/19.29.35 (Linux; U; Android 11; Pixel 5) gzip"
    private const val USER_AGENT_BROWSER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    // Cache resolved stream URLs with expiration (YouTube stream URLs are valid for ~6 hours)
    private data class CachedStream(
        val url: String,
        val fetchedAt: Long
    )

    private val streamCache = ConcurrentHashMap<String, CachedStream>()
    private const val CACHE_EXPIRATION_MS = 4 * 60 * 60 * 1000L // 4 hours

    /**
     * Extracts an 11-character YouTube video ID from various URL patterns.
     */
    fun extractVideoId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }

        val patterns = listOf(
            Regex("(?:https?:\\/\\/)?(?:www\\.|m\\.|music\\.)?youtube\\.com\\/watch\\?(?:[^&]*&)*v=([a-zA-Z0-9_-]{11})"),
            Regex("(?:https?:\\/\\/)?youtu\\.be\\/([a-zA-Z0-9_-]{11})"),
            Regex("(?:https?:\\/\\/)?(?:www\\.|m\\.)?youtube\\.com\\/embed\\/([a-zA-Z0-9_-]{11})"),
            Regex("(?:https?:\\/\\/)?(?:www\\.|m\\.)?youtube\\.com\\/v\\/([a-zA-Z0-9_-]{11})"),
            Regex("(?:https?:\\/\\/)?(?:www\\.|m\\.)?youtube\\.com\\/shorts\\/([a-zA-Z0-9_-]{11})"),
            Regex("(?:https?:\\/\\/)?(?:www\\.|m\\.)?youtube\\.com\\/live\\/([a-zA-Z0-9_-]{11})")
        )

        for (pattern in patterns) {
            val match = pattern.find(trimmed)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1]
            }
        }
        return null
    }

    /**
     * Extracts playlist ID from playlist URLs.
     */
    fun extractPlaylistId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.startsWith("PL") || trimmed.startsWith("RD") || trimmed.startsWith("UU") || trimmed.startsWith("OLAK5uy_")) {
            if (trimmed.matches(Regex("^[a-zA-Z0-9_-]{12,}$"))) {
                return trimmed
            }
        }

        val pattern = Regex("[?&]list=([a-zA-Z0-9_-]+)")
        val match = pattern.find(trimmed)
        return match?.groupValues?.getOrNull(1)
    }

    /**
     * Resolves a fresh, playable HTTPS audio stream URL for the given video ID.
     * Tries multiple Innertube client contexts (ANDROID_TESTSUITE, ANDROID, IOS, TVHTML5)
     * which return direct (unthrottled / unciphered) audio streams.
     */
    suspend fun resolveAudioStreamUrl(videoIdOrUrl: String, forceRefresh: Boolean = false): String? = withContext(Dispatchers.IO) {
        val videoId = extractVideoId(videoIdOrUrl) ?: run {
            Log.e(TAG, "[DIAGNOSTIC] Failed to extract videoId from input: $videoIdOrUrl")
            return@withContext null
        }

        Log.d(TAG, "[DIAGNOSTIC] YouTube stream resolution starting for videoId=$videoId (forceRefresh=$forceRefresh)")

        if (!forceRefresh) {
            val cached = streamCache[videoId]
            if (cached != null && (System.currentTimeMillis() - cached.fetchedAt) < CACHE_EXPIRATION_MS) {
                val ageSec = (System.currentTimeMillis() - cached.fetchedAt) / 1000
                val cachedUri = Uri.parse(cached.url)
                Log.d(TAG, "[DIAGNOSTIC] Returning cached stream for $videoId (age=${ageSec}s, scheme=${cachedUri.scheme}, host=${cachedUri.host})")
                return@withContext cached.url
            }
        }

        // Try clients in order of reliability for direct audio URLs
        val clients = listOf(
            "ANDROID_TESTSUITE",
            "ANDROID",
            "IOS",
            "TVHTML5_SIMPLY_EMBEDDED_PLAYER"
        )

        for (clientType in clients) {
            try {
                val streamUrl = fetchStreamUrlFromInnertube(videoId, clientType)
                if (!streamUrl.isNullOrBlank()) {
                    streamCache[videoId] = CachedStream(streamUrl, System.currentTimeMillis())
                    val parsedUri = Uri.parse(streamUrl)
                    val mime = parsedUri.getQueryParameter("mime") ?: "audio"
                    val expire = parsedUri.getQueryParameter("expire")
                    val expireInfo = if (expire != null) "expireSec=$expire" else "no-expire-param"
                    Log.d(TAG, "[DIAGNOSTIC] Successfully resolved audio stream for $videoId via $clientType: scheme=${parsedUri.scheme}, host=${parsedUri.host}, mime=$mime, $expireInfo, urlLength=${streamUrl.length}")
                    return@withContext streamUrl
                }
            } catch (e: Exception) {
                Log.w(TAG, "[DIAGNOSTIC] Innertube resolution failed for client $clientType: ${e.message}")
            }
        }

        Log.e(TAG, "[DIAGNOSTIC] Failed to resolve audio stream URL for videoId: $videoId across all clients")
        null
    }

    /**
     * Calls the Innertube player endpoint with the specified client context.
     */
    private fun fetchStreamUrlFromInnertube(videoId: String, clientType: String): String? {
        val url = URL("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 8000
            readTimeout = 8000
            doOutput = true
            doInput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("User-Agent", USER_AGENT_ANDROID)
            setRequestProperty("Origin", "https://www.youtube.com")
        }

        val requestJson = buildInnertubeRequestBody(videoId, clientType)
        OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
            writer.write(requestJson)
            writer.flush()
        }

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            conn.disconnect()
            return null
        }

        val responseText = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8")).use { reader ->
            reader.readText()
        }
        conn.disconnect()

        return extractAudioUrlFromPlayerResponse(responseText)
    }

    private fun buildInnertubeRequestBody(videoId: String, clientType: String): String {
        val json = JSONObject()
        json.put("videoId", videoId)

        val context = JSONObject()
        val client = JSONObject()

        when (clientType) {
            "ANDROID_TESTSUITE" -> {
                client.put("clientName", "ANDROID_TESTSUITE")
                client.put("clientVersion", "1.9")
                client.put("androidSdkVersion", 30)
                client.put("hl", "en")
                client.put("gl", "US")
            }
            "ANDROID" -> {
                client.put("clientName", "ANDROID")
                client.put("clientVersion", "19.29.35")
                client.put("androidSdkVersion", 30)
                client.put("userAgent", USER_AGENT_ANDROID)
                client.put("hl", "en")
                client.put("gl", "US")
            }
            "IOS" -> {
                client.put("clientName", "IOS")
                client.put("clientVersion", "19.29.1")
                client.put("deviceModel", "iPhone16,2")
                client.put("hl", "en")
                client.put("gl", "US")
            }
            "TVHTML5_SIMPLY_EMBEDDED_PLAYER" -> {
                client.put("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER")
                client.put("clientVersion", "2.0")
                client.put("hl", "en")
                client.put("gl", "US")
                val thirdParty = JSONObject()
                thirdParty.put("embedUrl", "https://www.youtube.com")
                context.put("thirdParty", thirdParty)
            }
        }

        context.put("client", client)
        json.put("context", context)
        json.put("playbackContext", JSONObject().apply {
            put("contentPlaybackContext", JSONObject().apply {
                put("html5Preference", "HTML5_PREF_WANTS")
            })
        })

        return json.toString()
    }

    private fun extractAudioUrlFromPlayerResponse(responseText: String): String? {
        val root = JSONObject(responseText)

        val playabilityStatus = root.optJSONObject("playabilityStatus")
        val status = playabilityStatus?.optString("status")
        if (status != null && status != "OK") {
            val reason = playabilityStatus.optString("reason", "Unknown playability error")
            Log.w(TAG, "YouTube playabilityStatus not OK: $status, reason: $reason")
            return null
        }

        val streamingData = root.optJSONObject("streamingData") ?: return null

        val audioFormats = mutableListOf<JSONObject>()

        // 1. Check adaptiveFormats (where separate audio streams live)
        val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats")
        if (adaptiveFormats != null) {
            for (i in 0 until adaptiveFormats.length()) {
                val format = adaptiveFormats.optJSONObject(i) ?: continue
                val mimeType = format.optString("mimeType", "")
                if (mimeType.startsWith("audio/")) {
                    audioFormats.add(format)
                }
            }
        }

        // 2. Check regular progressive formats as fallback
        val formats = streamingData.optJSONArray("formats")
        if (formats != null) {
            for (i in 0 until formats.length()) {
                val format = formats.optJSONObject(i) ?: continue
                audioFormats.add(format)
            }
        }

        // Filter for formats that have a direct "url" property
        val validFormatsWithUrl = audioFormats.filter { it.has("url") && !it.getString("url").isNullOrBlank() }

        if (validFormatsWithUrl.isEmpty()) {
            return null
        }

        // Sort by audio bitrate descending (best audio quality)
        val sorted = validFormatsWithUrl.sortedByDescending { it.optInt("bitrate", 0) }

        // Prefer audio/mp4 (m4a) or audio/webm with good bitrate
        val selectedFormat = sorted.firstOrNull {
            val mime = it.optString("mimeType", "")
            mime.contains("audio/mp4") || mime.contains("audio/webm")
        } ?: sorted.first()

        val rawUrl = selectedFormat.optString("url")
        if (rawUrl.isNotEmpty()) {
            val uri = Uri.parse(rawUrl)
            val hasSigCipher = selectedFormat.has("signatureCipher") || selectedFormat.has("cipher")
            val hasSigParam = uri.getQueryParameter("sig") != null || uri.getQueryParameter("signature") != null
            val hasSParam = uri.getQueryParameter("s") != null
            val itag = selectedFormat.optInt("itag", 0)
            val mimeType = selectedFormat.optString("mimeType", "unknown")
            val bitrate = selectedFormat.optInt("bitrate", 0)
            val avgBitrate = selectedFormat.optInt("averageBitrate", 0)
            val contentLength = selectedFormat.optString("contentLength", "unknown")
            val approxDurationMs = selectedFormat.optString("approxDurationMs", "unknown")

            Log.d(
                TAG,
                """
                |[DIAGNOSTIC STREAM INFO]
                | Scheme: ${uri.scheme}
                | Host: ${uri.host}
                | Path: ${uri.path}
                | itag: $itag
                | MIME / Codec: $mimeType
                | Bitrate: $bitrate bps (avg: $avgBitrate)
                | ContentLength: $contentLength bytes
                | ApproxDurationMs: $approxDurationMs
                | Query Parameters Present: ${uri.queryParameterNames.size} params (${uri.queryParameterNames.joinToString(", ")})
                | Expiration (expire param): ${uri.getQueryParameter("expire") ?: "none"}
                | Cipher/Signature in JSON: $hasSigCipher
                | Pre-signed param (sig/signature): $hasSigParam
                | Encrypted 's' param present: $hasSParam
                """.trimMargin()
            )
        }

        return rawUrl
    }

    /**
     * Fetches video metadata (title, author, thumbnail) using the public oEmbed API.
     * No API key required.
     */
    suspend fun fetchVideoMetadata(videoIdOrUrl: String): YouTubeVideoMetadata? = withContext(Dispatchers.IO) {
        val videoId = extractVideoId(videoIdOrUrl) ?: return@withContext null
        try {
            val oEmbedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
            val conn = (URL(oEmbedUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", USER_AGENT_BROWSER)
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                val json = JSONObject(response)
                val title = json.optString("title", "YouTube Track")
                val author = json.optString("author_name", "YouTube")
                val thumb = json.optString("thumbnail_url", "https://img.youtube.com/vi/$videoId/hqdefault.jpg")

                return@withContext YouTubeVideoMetadata(
                    videoId = videoId,
                    title = title,
                    author = author,
                    thumbnailUrl = thumb
                )
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "oEmbed metadata fetch failed for $videoId: ${e.message}")
        }

        // Fallback default metadata
        YouTubeVideoMetadata(
            videoId = videoId,
            title = "YouTube Focus Track",
            author = "YouTube Audio",
            thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        )
    }

    /**
     * Fetches playlist information and all contained video tracks from a playlist URL or ID.
     * Scrapes YouTube playlist web data without requiring YouTube Data API keys.
     */
    suspend fun fetchPlaylistTracks(playlistIdOrUrl: String): YouTubePlaylistInfo? = withContext(Dispatchers.IO) {
        val playlistId = extractPlaylistId(playlistIdOrUrl) ?: return@withContext null

        try {
            val playlistUrl = "https://www.youtube.com/playlist?list=$playlistId"
            val conn = (URL(playlistUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("User-Agent", USER_AGENT_BROWSER)
                setRequestProperty("Accept-Language", "en-US,en;q=0.9")
            }

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                conn.disconnect()
                return@withContext null
            }

            val html = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val ytInitialData = extractYtInitialData(html) ?: return@withContext null

            val playlistTitle = extractPlaylistTitle(ytInitialData) ?: "YouTube Playlist"
            val playlistAuthor = extractPlaylistAuthor(ytInitialData) ?: "YouTube"

            val tracks = extractPlaylistVideoTracks(ytInitialData)

            if (tracks.isNotEmpty()) {
                return@withContext YouTubePlaylistInfo(
                    playlistId = playlistId,
                    title = playlistTitle,
                    author = playlistAuthor,
                    tracks = tracks
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching playlist tracks for $playlistId", e)
        }

        null
    }

    private fun extractYtInitialData(html: String): JSONObject? {
        val markers = listOf(
            "var ytInitialData = ",
            "window[\"ytInitialData\"] = ",
            "ytInitialData = "
        )

        for (marker in markers) {
            val startIndex = html.indexOf(marker)
            if (startIndex != -1) {
                val jsonStart = startIndex + marker.length
                // Extract matching JSON object
                var braceCount = 0
                var insideString = false
                var isEscaped = false
                var endIndex = -1

                for (i in jsonStart until html.length) {
                    val c = html[i]
                    if (isEscaped) {
                        isEscaped = false
                        continue
                    }
                    if (c == '\\') {
                        isEscaped = true
                        continue
                    }
                    if (c == '"') {
                        insideString = !insideString
                        continue
                    }
                    if (!insideString) {
                        if (c == '{') {
                            braceCount++
                        } else if (c == '}') {
                            braceCount--
                            if (braceCount == 0) {
                                endIndex = i + 1
                                break
                            }
                        }
                    }
                }

                if (endIndex != -1) {
                    val jsonStr = html.substring(jsonStart, endIndex)
                    return try {
                        JSONObject(jsonStr)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
        return null
    }

    private fun extractPlaylistTitle(json: JSONObject): String? {
        try {
            val metadata = json.optJSONObject("metadata")?.optJSONObject("playlistMetadataRenderer")
            if (metadata != null) {
                val title = metadata.optString("title")
                if (title.isNotBlank()) return title
            }
            val header = json.optJSONObject("header")?.optJSONObject("playlistHeaderRenderer")
            if (header != null) {
                val titleObj = header.optJSONObject("title")
                val text = titleObj?.optString("simpleText") ?: titleObj?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrBlank()) return text
            }
        } catch (e: Exception) {}
        return null
    }

    private fun extractPlaylistAuthor(json: JSONObject): String? {
        try {
            val header = json.optJSONObject("header")?.optJSONObject("playlistHeaderRenderer")
            val ownerObj = header?.optJSONObject("ownerText")
            val text = ownerObj?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
            if (!text.isNullOrBlank()) return text
        } catch (e: Exception) {}
        return null
    }

    private fun extractPlaylistVideoTracks(json: JSONObject): List<FocusTrack> {
        val result = mutableListOf<FocusTrack>()
        try {
            findPlaylistVideoRenderers(json, result)
        } catch (e: Exception) {
            Log.w(TAG, "Error traversing playlist JSON: ${e.message}")
        }
        return result
    }

    private fun findPlaylistVideoRenderers(obj: Any, outList: MutableList<FocusTrack>) {
        if (obj is JSONObject) {
            if (obj.has("playlistVideoRenderer")) {
                val item = obj.getJSONObject("playlistVideoRenderer")
                val videoId = item.optString("videoId")
                if (videoId.isNotBlank() && videoId.length == 11) {
                    val titleObj = item.optJSONObject("title")
                    val title = titleObj?.optString("simpleText")
                        ?: titleObj?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                        ?: "YouTube Video"

                    val bylineObj = item.optJSONObject("shortBylineText")
                    val artist = bylineObj?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                        ?: "YouTube"

                    outList.add(
                        FocusTrack(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            artist = artist,
                            uri = "https://www.youtube.com/watch?v=$videoId",
                            isBuiltIn = false,
                            isYouTube = true
                        )
                    )
                }
            } else {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    findPlaylistVideoRenderers(obj.get(key), outList)
                }
            }
        } else if (obj is JSONArray) {
            for (i in 0 until obj.length()) {
                val item = obj.opt(i) ?: continue
                findPlaylistVideoRenderers(item, outList)
            }
        }
    }
}
