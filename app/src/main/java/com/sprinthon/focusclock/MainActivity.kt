package com.sprinthon.focusclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sprinthon.focusclock.ui.navigation.AppNavigation
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.FocusClockTheme
import com.sprinthon.focusclock.ui.viewmodel.FocusViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Force light (white) status bar icons globally for dark AMOLED theme
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    @Suppress("DEPRECATION")
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    setContent {
      FocusClockTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = AmoledBlack
        ) {
          val focusViewModel: FocusViewModel = viewModel()
          AppNavigation(viewModel = focusViewModel)
        }
      }
    }
  }
}

