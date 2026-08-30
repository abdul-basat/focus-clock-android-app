package com.sprinthon.focusclock.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.ui.clock.ClockFontBottomSheet
import com.sprinthon.focusclock.ui.clock.ClockStyleBottomSheet
import com.sprinthon.focusclock.ui.clock.rememberCurrentTimeData
import com.sprinthon.focusclock.ui.components.CustomDurationDialog
import com.sprinthon.focusclock.ui.components.ProfileSelectorBottomSheet
import com.sprinthon.focusclock.ui.screens.ActiveFocusScreen
import com.sprinthon.focusclock.ui.screens.BackgroundSettingsScreen
import com.sprinthon.focusclock.ui.screens.HomeScreen
import com.sprinthon.focusclock.ui.screens.OnboardingScreen
import com.sprinthon.focusclock.ui.screens.StartFocusScreen
import com.sprinthon.focusclock.ui.viewmodel.FocusViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object StartFocus : Screen("start_focus")
    object BackgroundSettings : Screen("background_settings")
    object ActiveFocus : Screen("active_focus")
    object SettingsHub : Screen("settings_hub")
    object FocusSettings : Screen("settings_focus")
    object ClockSettings : Screen("settings_clock")
    object AudioSettings : Screen("settings_audio")
    object GeneralSettings : Screen("settings_general")
    object About : Screen("settings_about")
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTimeData = rememberCurrentTimeData(is24Hour = uiState.preferences.timeFormat24Hour)

    // React to session state transitions if needed
    LaunchedEffect(uiState.session.state) {
        if (uiState.session.state == SessionState.RUNNING && navController.currentDestination?.route != Screen.ActiveFocus.route) {
            navController.navigate(Screen.ActiveFocus.route) {
                launchSingleTop = true
            }
        }
    }

    val startRoute = if (!uiState.preferences.onboardingCompleted) {
        Screen.Onboarding.route
    } else {
        Screen.Home.route
    }

    GlobalScaffold(
        navController = navController,
        immersiveFullscreenEnabled = uiState.preferences.immersiveFullscreenEnabled
    ) { scaffoldModifier ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = scaffoldModifier
        ) {
            composable(
                route = Screen.Onboarding.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            OnboardingScreen(
                onFinishOnboardingStartFocus = { profile ->
                    viewModel.completeOnboarding(profile, customizeFirst = false)
                    viewModel.startFocusSession()
                    navController.navigate(Screen.ActiveFocus.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFinishOnboardingCustomize = {
                    viewModel.completeOnboarding(null, customizeFirst = true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            HomeScreen(
                preferences = uiState.preferences,
                configuredDurationMinutes = uiState.configuredDurationMinutes,
                configuredTimerMode = uiState.configuredTimerMode,
                allProfiles = uiState.allProfiles,
                historyRecords = uiState.historyRecords,
                onStartFocus = {
                    viewModel.startFocusSession()
                    navController.navigate(Screen.ActiveFocus.route) {
                        launchSingleTop = true
                    }
                },
                onOpenStartConfig = {
                    navController.navigate(Screen.StartFocus.route)
                },
                onOpenClockStyleSelector = {
                    viewModel.setShowClockStyleSheet(true)
                },
                onOpenProfileSelector = {
                    viewModel.setShowProfileSelectorSheet(true)
                },
                onOpenSettings = {
                    navController.navigate(Screen.SettingsHub.route)
                }
            )
        }

        composable(
            route = Screen.StartFocus.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            StartFocusScreen(
                preferences = uiState.preferences,
                configuredDurationMinutes = uiState.configuredDurationMinutes,
                isCustomDuration = uiState.isCustomDuration,
                configuredTimerMode = uiState.configuredTimerMode,
                onSelectPresetDuration = { preset ->
                    viewModel.selectPresetDuration(preset)
                },
                onSelectTimerMode = { mode ->
                    viewModel.setTimerDisplayMode(mode)
                },
                onOpenClockStyleSelector = {
                    viewModel.setShowClockStyleSheet(true)
                },
                onOpenBackgroundSettings = {
                    navController.navigate(Screen.BackgroundSettings.route)
                },
                onOpenAudioSettings = {
                    navController.navigate(Screen.AudioSettings.route)
                },
                onStartFocus = {
                    viewModel.startFocusSession()
                    navController.navigate(Screen.ActiveFocus.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.SettingsHub.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.SettingsHubScreen(
                preferences = uiState.preferences,
                session = uiState.session,
                onNavigateToFocusSettings = { navController.navigate(Screen.FocusSettings.route) },
                onNavigateToClockSettings = { navController.navigate(Screen.ClockSettings.route) },
                onNavigateToBackgroundSettings = { navController.navigate(Screen.BackgroundSettings.route) },
                onNavigateToAudioSettings = { navController.navigate(Screen.AudioSettings.route) },
                onNavigateToGeneralSettings = { navController.navigate(Screen.GeneralSettings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FocusSettings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.FocusSettingsScreen(
                preferences = uiState.preferences,
                session = uiState.session,
                allProfiles = uiState.allProfiles,
                onOpenProfileSelector = { viewModel.setShowProfileSelectorSheet(true) },
                onSelectDefaultDuration = { minutes ->
                    viewModel.setDefaultDuration(minutes)
                },
                onOpenCustomDurationDialog = {
                    viewModel.setShowCustomDurationDialog(true)
                },
                onSelectTimerMode = { mode ->
                    viewModel.setTimerDisplayMode(mode)
                },
                onToggleVibrateOnCompletion = { vibrate ->
                    viewModel.setVibrateOnCompletion(vibrate)
                },
                onToggleNotifyOnCompletion = { notify ->
                    viewModel.setNotifyOnCompletion(notify)
                },
                onToggleSoundOnCompletion = { sound ->
                    viewModel.setSoundOnCompletion(sound)
                },
                onToggleConfirmBeforeExit = { confirm ->
                    viewModel.setConfirmBeforeExit(confirm)
                },
                onClearHistory = {
                    viewModel.clearHistory()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ClockSettings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.ClockSettingsScreen(
                preferences = uiState.preferences,
                onSelectClockStyle = { style ->
                    viewModel.setClockStyle(style)
                },
                onSelectClockFont = { font ->
                    viewModel.setClockFont(font)
                },
                onToggle24Hour = { is24h ->
                    viewModel.setTimeFormat(is24h)
                },
                onToggleShowDate = { show ->
                    viewModel.setShowDate(show)
                },
                onToggleShowDayOfWeek = { show ->
                    viewModel.setShowDayOfWeek(show)
                },
                onSelectDateFormat = { option ->
                    viewModel.setDateFormatOption(option)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AudioSettings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.AmbientSoundSettingsScreen(
                preferences = uiState.preferences,
                playerState = uiState.playerState,
                customTracks = uiState.customTracks,
                onSelectTrack = { trackId, previewPlay ->
                    viewModel.selectTrackInSettings(trackId, previewPlay)
                },
                onAddCustomTrack = { uri, title, isYouTube ->
                    viewModel.addCustomTrack(uri, title, isYouTube)
                },
                onDeleteCustomTrack = { trackId ->
                    viewModel.deleteCustomTrack(trackId)
                },
                onTogglePlayPause = {
                    viewModel.togglePlayerPlayPause()
                },
                onToggleAutoPlay = { autoPlay ->
                    viewModel.setAutoPlayMusic(autoPlay)
                },
                onToggleLoop = { loop ->
                    viewModel.setMusicLoop(loop)
                },
                onVolumeChange = { vol ->
                    viewModel.setMusicVolume(vol)
                },
                onToggleShowWaveform = { show ->
                    viewModel.setShowWaveform(show)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.GeneralSettings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.GeneralSettingsScreen(
                preferences = uiState.preferences,
                onToggleKeepScreenAwake = { awake ->
                    viewModel.setKeepScreenAwake(awake)
                },
                onToggleAutoHideControls = { autoHide ->
                    viewModel.setAutoHideControls(autoHide)
                },
                onToggleBatterySaver = { enabled ->
                    viewModel.setBatterySaverEnabled(enabled)
                },
                onToggleImmersiveFullscreen = { enabled ->
                    viewModel.setImmersiveFullscreen(enabled)
                },
                onToggleVibrateOnCompletion = { vibrate ->
                    viewModel.setVibrateOnCompletion(vibrate)
                },
                onToggleConfirmBeforeExit = { confirm ->
                    viewModel.setConfirmBeforeExit(confirm)
                },
                onResetAllSettings = {
                    viewModel.resetAllSettings()
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.About.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.sprinthon.focusclock.ui.screens.settings.AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BackgroundSettings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            BackgroundSettingsScreen(
                preferences = uiState.preferences,
                onSelectBackgroundType = { type ->
                    viewModel.setBackgroundType(type)
                },
                onSelectSolidColor = { hex ->
                    viewModel.setSolidBackgroundColor(hex)
                },
                onSelectSingleImage = { uri ->
                    viewModel.setBackgroundImageUri(uri)
                },
                onAddSlideshowImages = { uris ->
                    viewModel.addSlideshowImageUris(uris)
                },
                onRemoveSlideshowImage = { uri ->
                    viewModel.removeSlideshowImageUri(uri)
                },
                onSelectSlideshowInterval = { interval ->
                    viewModel.setSlideshowInterval(interval)
                },
                onToggleSlideshowShuffle = { shuffle ->
                    viewModel.setSlideshowShuffle(shuffle)
                },
                onSelectOverlayStrength = { strength ->
                    viewModel.setBackgroundOverlayStrength(strength)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ActiveFocus.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            ActiveFocusScreen(
                session = uiState.session,
                preferences = uiState.preferences,
                playerState = uiState.playerState,
                controlsVisible = uiState.controlsVisible,
                showExitDialog = uiState.showExitConfirmationDialog,
                onScreenTapped = { viewModel.onScreenTapped() },
                onPause = { viewModel.pauseFocusSession() },
                onResume = { viewModel.resumeFocusSession() },
                onCancelRequest = { viewModel.setExitConfirmationDialogVisible(true) },
                onConfirmEndSession = {
                    viewModel.setExitConfirmationDialogVisible(false)
                    viewModel.cancelFocusSession()
                    viewModel.resetSession()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onDismissExitDialog = {
                    viewModel.setExitConfirmationDialogVisible(false)
                },
                onFinishCompletedSession = {
                    viewModel.resetSession()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onStartAgain = {
                    viewModel.startFocusAgain()
                },
                onPlayPauseToggle = { viewModel.togglePlayerPlayPause() },
                onStopPlayer = { viewModel.stopPlayer() },
                onNextTrack = { viewModel.nextPlayerTrack() },
                onPreviousTrack = { viewModel.previousPlayerTrack() },
                onToggleLoop = { viewModel.togglePlayerLoop() }
            )
        }
    }
    } // End of GlobalScaffold

    // Modal Clock Style Selector Sheet
    if (uiState.showClockStyleSheet) {
        ClockStyleBottomSheet(
            selectedStyle = uiState.preferences.clockStyle,
            timeData = currentTimeData,
            clockFont = uiState.preferences.clockFont,
            onStyleSelected = { style ->
                viewModel.setClockStyle(style)
                viewModel.setShowClockStyleSheet(false)
            },
            onDismiss = {
                viewModel.setShowClockStyleSheet(false)
            }
        )
    }

    // Modal Clock Font Selector Sheet
    if (uiState.showClockFontSheet) {
        ClockFontBottomSheet(
            selectedFont = uiState.preferences.clockFont,
            onFontSelected = { font ->
                viewModel.setClockFont(font)
                viewModel.setShowClockFontSheet(false)
            },
            onDismiss = {
                viewModel.setShowClockFontSheet(false)
            }
        )
    }

    // Modal Profile Selector Sheet
    if (uiState.showProfileSelectorSheet) {
        ProfileSelectorBottomSheet(
            allProfiles = uiState.allProfiles,
            activeProfileId = uiState.preferences.activeProfileId,
            onSelectProfile = { profile ->
                viewModel.applyProfile(profile)
                viewModel.setShowProfileSelectorSheet(false)
            },
            onSaveCurrentAsProfile = { name ->
                viewModel.saveCurrentAsProfile(name)
            },
            onDeleteProfile = { profileId ->
                viewModel.deleteCustomProfile(profileId)
            },
            onDismiss = {
                viewModel.setShowProfileSelectorSheet(false)
            }
        )
    }

    // Custom Duration Dialog
    if (uiState.showCustomDurationDialog) {
        CustomDurationDialog(
            initialMinutes = if (uiState.isCustomDuration) uiState.configuredDurationMinutes else 30,
            onConfirm = { minutes ->
                viewModel.setCustomDuration(minutes)
            },
            onDismiss = {
                viewModel.setShowCustomDurationDialog(false)
            }
        )
    }
}
