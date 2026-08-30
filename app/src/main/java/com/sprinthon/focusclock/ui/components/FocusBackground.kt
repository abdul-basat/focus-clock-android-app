package com.sprinthon.focusclock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Independent, battery-efficient background layer for the Active Focus screen
 * and Background Settings preview.
 *
 * Supports:
 * - Curated & Custom Solid Colors (defaulting to pure AMOLED black #000000)
 * - Single Photo Background with center crop
 * - Multi-Photo Slideshow with configurable interval, shuffle, and smooth crossfade
 * - Configurable Readability Dim Overlay (0% - 70%) to ensure clock remains the hero
 */
@Composable
fun FocusBackground(
    preferences: FocusPreferences,
    modifier: Modifier = Modifier,
    isInteractivePreview: Boolean = false
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(preferences.solidBackgroundColor))
            .testTag("focus_background_layer")
    ) {
        when (preferences.backgroundType) {
            BackgroundType.SOLID_COLOR -> {
                // Base background color already set on the container Box
            }
            BackgroundType.SINGLE_IMAGE -> {
                if (!preferences.backgroundImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(preferences.backgroundImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Background Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("focus_background_single_image")
                    )
                }
            }
            BackgroundType.SLIDESHOW -> {
                val imageUris = preferences.slideshowImageUris
                if (imageUris.isNotEmpty()) {
                    var currentIndex by remember(imageUris) { mutableIntStateOf(0) }

                    // Only run slideshow timing loop if interval is set and more than 1 image exists
                    val intervalSeconds = preferences.slideshowInterval.seconds
                    if (intervalSeconds > 0 && imageUris.size > 1) {
                        LaunchedEffect(
                            key1 = imageUris,
                            key2 = intervalSeconds,
                            key3 = preferences.slideshowShuffle
                        ) {
                            while (true) {
                                delay(intervalSeconds * 1000L)
                                if (preferences.slideshowShuffle) {
                                    if (imageUris.size > 1) {
                                        var nextIndex: Int
                                        do {
                                            nextIndex = Random.nextInt(imageUris.size)
                                        } while (nextIndex == currentIndex && imageUris.size > 1)
                                        currentIndex = nextIndex
                                    }
                                } else {
                                    currentIndex = (currentIndex + 1) % imageUris.size
                                }
                            }
                        }
                    }

                    val safeIndex = currentIndex.coerceIn(0, imageUris.size - 1)
                    val activeUri = imageUris[safeIndex]

                    Crossfade(
                        targetState = activeUri,
                        animationSpec = tween(durationMillis = 800),
                        label = "slideshow_crossfade",
                        modifier = Modifier.fillMaxSize()
                    ) { uri ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .crossfade(false) // Handled by Compose Crossfade
                                .build(),
                            contentDescription = "Slideshow Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("focus_background_slideshow_image")
                        )
                    }
                }
            }
        }

        // Readability Dim Overlay: ensures text and clock always remain the visual hero
        val overlayAlpha = preferences.backgroundOverlayStrength.coerceIn(0f, 0.9f)
        if (overlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
                    .testTag("focus_background_dim_overlay")
            )
        }
    }
}

