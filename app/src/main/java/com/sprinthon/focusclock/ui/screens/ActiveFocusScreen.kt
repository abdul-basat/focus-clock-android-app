package com.sprinthon.focusclock.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.ui.clock.ClockRenderer
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.components.EndFocusDialog
import com.sprinthon.focusclock.ui.components.FocusBackground
import com.sprinthon.focusclock.ui.components.FocusPlayerArea
import com.sprinthon.focusclock.ui.components.FocusTimerWidget
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.FocusAmber
import com.sprinthon.focusclock.ui.theme.FocusCompleted

/**
 * Modern, distraction-free Active Focus Screen.
 * Places the Clock at the visual center, supported by the secondary Focus Timer,
 * calm ambient waveform/player controls, and independent background architecture.
 */
@Composable
fun ActiveFocusScreen(
    session: SessionSnapshot,
    preferences: FocusPreferences,
    playerState: PlayerUiState,
    controlsVisible: Boolean,
    showExitDialog: Boolean,
    onScreenTapped: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmEndSession: () -> Unit,
    onDismissExitDialog: () -> Unit,
    onFinishCompletedSession: () -> Unit,
    modifier: Modifier = Modifier,
    onStartAgain: () -> Unit = {},
    onPlayPauseToggle: () -> Unit,
    onStopPlayer: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onToggleLoop: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val timeData = rememberCurrentTimeData(is24Hour = preferences.timeFormat24Hour)

    val isPaused = session.state == SessionState.PAUSED
    val isCompleted = session.state == SessionState.COMPLETED

    // Keep Screen Awake, enforce high-contrast status bar icons, and battery saver brightness management
    val view = LocalView.current
    DisposableEffect(preferences.keepScreenAwake, preferences.batterySaverEnabled, view) {
        val activity = context.findActivity()
        var originalLightStatusBars: Boolean? = null
        if (activity != null) {
            val window = activity.window
            if (preferences.keepScreenAwake) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            // Force light (white) status bar icons for dark focus backgrounds
            val insetsController = WindowCompat.getInsetsController(window, view)
            originalLightStatusBars = insetsController.isAppearanceLightStatusBars
            insetsController.isAppearanceLightStatusBars = false

            // Transparent status bar to let background show through
            @Suppress("DEPRECATION")
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }
        
        onDispose {
            activity?.let {
                if (preferences.keepScreenAwake) {
                    it.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                // Restore original status bar appearance
                val insetsController = WindowCompat.getInsetsController(it.window, view)
                originalLightStatusBars?.let { original ->
                    insetsController.isAppearanceLightStatusBars = original
                }
                // Restore system brightness
                val layoutParams = it.window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.window.attributes = layoutParams
            }
        }
    }

    // Battery Saver: Auto-dim screen brightness after 30s of inactivity during active focus
    if (preferences.batterySaverEnabled && session.state == SessionState.RUNNING) {
        val activity = context.findActivity()
        androidx.compose.runtime.LaunchedEffect(controlsVisible, preferences.batterySaverEnabled) {
            if (activity != null) {
                // Restore brightness immediately when controls become visible (user tapped)
                if (controlsVisible) {
                    val layoutParams = activity.window.attributes
                    layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    activity.window.attributes = layoutParams
                }
                // Wait 30 seconds then dim to minimal brightness
                kotlinx.coroutines.delay(30_000L)
                if (!controlsVisible) {
                    val layoutParams = activity.window.attributes
                    layoutParams.screenBrightness = 0.05f
                    activity.window.attributes = layoutParams
                }
            }
        }
    }

    // Intercept Back button during active session to show deliberate confirmation
    BackHandler(enabled = !isCompleted) {
        onCancelRequest()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_focus_screen")
    ) {
        // Independent Background Layer (Clean separation for Phase 5)
        FocusBackground(preferences = preferences)
        
        // YouTube Player (Hidden but active if track is YouTube)
        if (playerState.currentTrack?.isYouTube == true && (playerState.isPlaying || playerState.isConnected)) {
            val youtubeUrl = playerState.currentTrack.uri
            val videoIdMatch = Regex("(?:youtube\\.com\\/(?:[^/]+\\/.+\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([^\"&?/\\s]{11})").find(youtubeUrl)
            val videoId = videoIdMatch?.groupValues?.get(1)
            
            val context = LocalContext.current
            androidx.compose.runtime.LaunchedEffect(videoId) {
                if (videoId == null && playerState.isPlaying) {
                    android.widget.Toast.makeText(
                        context,
                        "Invalid YouTube link",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (videoId != null) {
                androidx.compose.ui.viewinterop.AndroidView(
                    modifier = Modifier.size(1.dp).alpha(0.01f),
                    factory = { ctx ->
                        try {
                            android.webkit.WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                webChromeClient = android.webkit.WebChromeClient()
                                webViewClient = object : android.webkit.WebViewClient() {
                                    override fun onReceivedError(
                                        view: android.webkit.WebView?,
                                        request: android.webkit.WebResourceRequest?,
                                        error: android.webkit.WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        android.widget.Toast.makeText(
                                            ctx, 
                                            "Could not load YouTube track. Please check internet connection.", 
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                // Force playing when loaded via iframe API or just embed autoplay
                                val embedHtml = """
                                    <!DOCTYPE html>
                                    <html>
                                      <body style="margin:0;padding:0;">
                                        <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId?autoplay=1&controls=0&playsinline=1" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                                      </body>
                                    </html>
                                """.trimIndent()
                                loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                            }
                        } catch (e: Exception) {
                            // Fallback if WebView is not available on the device
                            android.widget.Toast.makeText(
                                ctx, 
                                "WebView is not available to play YouTube audio.", 
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            android.view.View(ctx)
                        }
                    },
                    update = { view ->
                        if (view is android.webkit.WebView) {
                            if (isPaused || isCompleted || !playerState.isPlaying) {
                                view.onPause()
                            } else {
                                view.onResume()
                            }
                        }
                    }
                )
            }
        }

        if (isCompleted) {
            // Serene Session Completion Screen
            SessionCompletedView(
                session = session,
                onReturnHome = onFinishCompletedSession,
                onStartAgain = onStartAgain,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            // Main Active Focus Experience with full tap interaction layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onScreenTapped
                    )
            ) {
                if (isLandscape) {
                    ActiveFocusLandscapeContent(
                        session = session,
                        preferences = preferences,
                        timeData = timeData,
                        playerState = playerState,
                        controlsVisible = controlsVisible,
                        isPaused = isPaused,
                        onPause = onPause,
                        onResume = onResume,
                        onEndFocusClick = onCancelRequest,
                        onPlayPauseToggle = onPlayPauseToggle,
                        onStopPlayer = onStopPlayer,
                        onNextTrack = onNextTrack,
                        onPreviousTrack = onPreviousTrack,
                        onToggleLoop = onToggleLoop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                } else {
                    ActiveFocusPortraitContent(
                        session = session,
                        preferences = preferences,
                        timeData = timeData,
                        playerState = playerState,
                        controlsVisible = controlsVisible,
                        isPaused = isPaused,
                        onPause = onPause,
                        onResume = onResume,
                        onEndFocusClick = onCancelRequest,
                        onPlayPauseToggle = onPlayPauseToggle,
                        onStopPlayer = onStopPlayer,
                        onNextTrack = onNextTrack,
                        onPreviousTrack = onPreviousTrack,
                        onToggleLoop = onToggleLoop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }
        }

        // Exit Confirmation Dialog
        if (showExitDialog) {
            EndFocusDialog(
                onKeepFocusing = onDismissExitDialog,
                onEndSession = onConfirmEndSession
            )
        }
    }
}

/**
 * Dedicated Portrait Composition for Active Focus.
 * Priority: 1. Hero Clock -> 2. Date -> 3. Focus Timer -> 4. Track/Waveform -> 5. Controls.
 */
@Composable
private fun ActiveFocusPortraitContent(
    session: SessionSnapshot,
    preferences: FocusPreferences,
    timeData: com.sprinthon.focusclock.ui.clock.ClockTimeData,
    playerState: PlayerUiState,
    controlsVisible: Boolean,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEndFocusClick: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onStopPlayer: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onToggleLoop: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val screenHeight = maxHeight
        val isCompact = screenHeight < 680.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Area: Clean quiet breathing room
            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 16.dp))

            // Center Area: Dominant Clock + Date + Focus Timer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Dominant Clock Renderer
                ClockRenderer(
                    style = session.clockStyle,
                    timeData = timeData,
                    clockFont = preferences.clockFont,
                    scale = if (isCompact) 0.9f else 1.0f,
                    showDate = preferences.showDate,
                    showDayOfWeek = preferences.showDayOfWeek,
                    isLandscape = false
                )

                // Show timer only in Countdown mode when enabled (suppressed in Elapsed mode)
                if (preferences.showTimer && session.displayMode == TimerDisplayMode.COUNTDOWN && !session.isUnlimited) {
                    Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 16.dp))

                    // Secondary Focus Timer
                    FocusTimerWidget(
                        session = session,
                        fontSize = if (isCompact) 22.sp else 26.sp,
                        textColor = Color.White.copy(alpha = 0.92f),
                        accentColor = FocusAmber
                    )
                }
            }

            // Bottom Area: Player area & Minimal Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .padding(bottom = if (isCompact) 4.dp else 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FocusPlayerArea(
                    playerState = playerState,
                    controlsVisible = controlsVisible,
                    showWaveform = preferences.showWaveform,
                    batterySaverActive = preferences.batterySaverEnabled && session.state == SessionState.RUNNING,
                    onPlayPauseToggle = onPlayPauseToggle,
                    onStop = onStopPlayer,
                    onNext = onNextTrack,
                    onPrevious = onPreviousTrack,
                    onToggleLoop = onToggleLoop
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Session Actions (Pause/Resume & End Focus)
                AnimatedVisibility(
                    visible = controlsVisible,
                    enter = fadeIn(animationSpec = tween(250)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        // Pause / Resume Session Button (Clear visual contrast)
                        Button(
                            onClick = if (isPaused) onResume else onPause,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaused) FocusAmber else DarkElevatedSurface,
                                contentColor = if (isPaused) Color.Black else Color.White
                            ),
                            border = if (!isPaused) BorderStroke(1.dp, Color(0xFF383840)) else null,
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("focus_pause_resume_button")
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Resume Timer" else "Pause Timer",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPaused) "Resume Timer" else "Pause Timer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // End Focus Button (Subtle, non-distracting)
                        OutlinedButton(
                            onClick = onEndFocusClick,
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(1.dp, Color(0xFF383840)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White.copy(alpha = 0.75f)
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("end_focus_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "End Focus",
                                modifier = Modifier.size(15.dp),
                                tint = Color.White.copy(alpha = 0.75f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "End Focus",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Landscape Composition for Active Focus.
 * Left: Hero Clock, Date, and Focus Timer.
 * Right: Player Area, Waveform, Playback Controls, and Session Actions.
 */
@Composable
private fun ActiveFocusLandscapeContent(
    session: SessionSnapshot,
    preferences: FocusPreferences,
    timeData: com.sprinthon.focusclock.ui.clock.ClockTimeData,
    playerState: PlayerUiState,
    controlsVisible: Boolean,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEndFocusClick: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onStopPlayer: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onToggleLoop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side: Dominant Clock, Date, and Timer
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ClockRenderer(
                style = session.clockStyle,
                timeData = timeData,
                clockFont = preferences.clockFont,
                scale = 1.0f,
                showDate = preferences.showDate,
                showDayOfWeek = preferences.showDayOfWeek,
                isLandscape = true
            )

            // Show timer only in Countdown mode when enabled (suppressed in Elapsed mode)
            if (preferences.showTimer && session.displayMode == TimerDisplayMode.COUNTDOWN && !session.isUnlimited) {
                Spacer(modifier = Modifier.height(10.dp))
                FocusTimerWidget(
                    session = session,
                    fontSize = 22.sp,
                    textColor = Color.White.copy(alpha = 0.92f),
                    accentColor = FocusAmber
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Right Side: Ambient Audio Player Area & Subtle Controls
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FocusPlayerArea(
                playerState = playerState,
                controlsVisible = controlsVisible,
                showWaveform = preferences.showWaveform,
                waveformWidth = 140.dp,
                batterySaverActive = preferences.batterySaverEnabled && session.state == SessionState.RUNNING,
                onPlayPauseToggle = onPlayPauseToggle,
                onStop = onStopPlayer,
                onNext = onNextTrack,
                onPrevious = onPreviousTrack,
                onToggleLoop = onToggleLoop
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Controls Row
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(animationSpec = tween(250)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Pause / Resume Timer
                    Button(
                        onClick = if (isPaused) onResume else onPause,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaused) FocusAmber else DarkElevatedSurface,
                            contentColor = if (isPaused) Color.Black else Color.White
                        ),
                        border = if (!isPaused) BorderStroke(1.dp, Color(0xFF383840)) else null,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("focus_pause_resume_button_landscape")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume Timer" else "Pause Timer",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPaused) "Resume" else "Pause",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // End Focus
                    OutlinedButton(
                        onClick = onEndFocusClick,
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color(0xFF383840)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.75f)
                        ),
                        modifier = Modifier
                            .height(42.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("end_focus_action_button_landscape")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End Focus",
                            modifier = Modifier.size(15.dp),
                            tint = Color.White.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "End",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Serene Session Completion Screen.
 */
@Composable
private fun SessionCompletedView(
    session: SessionSnapshot,
    onReturnHome: () -> Unit,
    onStartAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Focus Session Completed",
            tint = FocusCompleted,
            modifier = Modifier
                .size(64.dp)
                .testTag("session_completed_icon")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Session Complete",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val durationSummary = if (session.isUnlimited) {
            "Total time focused: ${session.formattedDisplayTime}"
        } else {
            "Focused for ${session.durationMinutes} minutes · ${session.profileName}"
        }

        Text(
            text = durationSummary,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onStartAgain,
            colors = ButtonDefaults.buttonColors(
                containerColor = FocusAmber,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .widthIn(min = 200.dp, max = 300.dp)
                .height(50.dp)
                .minimumInteractiveComponentSize()
                .testTag("start_again_button")
        ) {
            Text(
                text = "Focus Again",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onReturnHome,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .widthIn(min = 200.dp, max = 300.dp)
                .height(50.dp)
                .minimumInteractiveComponentSize()
                .testTag("return_home_button")
        ) {
            Text(
                text = "Return Home",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }
    }
}

/**
 * Helper to safely extract Activity from Context hierarchy.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
