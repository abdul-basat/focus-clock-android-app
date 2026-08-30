package com.sprinthon.focusclock.ui.navigation

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalScaffold(
    navController: NavHostController,
    immersiveFullscreenEnabled: Boolean,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Never show nav bars on onboarding or active focus regardless of settings
    val forceHideNav = currentRoute == Screen.Onboarding.route || currentRoute == Screen.ActiveFocus.route
    
    val showNav = !immersiveFullscreenEnabled && !forceHideNav

    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(immersiveFullscreenEnabled, forceHideNav, view) {
        val activity = context as? Activity ?: run {
            var currentContext = context
            while (currentContext is android.content.ContextWrapper) {
                if (currentContext is Activity) {
                    return@run currentContext
                }
                currentContext = currentContext.baseContext
            }
            null
        }

        var windowInsetsController: WindowInsetsControllerCompat? = null
        if (activity != null) {
            val window = activity.window
            windowInsetsController = WindowCompat.getInsetsController(window, view)
            
            if (immersiveFullscreenEnabled || forceHideNav) {
                // Hide system bars for immersive mode
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        
        onDispose {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Edge to edge
        containerColor = Color.Transparent,
        bottomBar = {
            AnimatedVisibility(
                visible = showNav,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
            ) {
                NavigationBar(
                    containerColor = Color(0xF70C0C0E), // Solid sleek dark surface
                    contentColor = Color.White
                ) {
                    GlobalNavigationItem.values().forEach { item ->
                        val isSelected = when (item) {
                            GlobalNavigationItem.Home -> currentRoute == Screen.Home.route
                            GlobalNavigationItem.StartFocus -> currentRoute == Screen.StartFocus.route
                            GlobalNavigationItem.Ambient -> currentRoute == Screen.AudioSettings.route
                            GlobalNavigationItem.Settings -> currentRoute?.startsWith("settings") == true
                        }
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.sprinthon.focusclock.ui.theme.FocusAmber,
                                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                selectedTextColor = com.sprinthon.focusclock.ui.theme.FocusAmber,
                                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                indicatorColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}
