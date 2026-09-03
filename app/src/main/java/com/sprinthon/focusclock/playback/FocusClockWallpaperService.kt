package com.sprinthon.focusclock.playback

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.ui.clock.calculateCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FocusClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return FocusClockEngine()
    }

    inner class FocusClockEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val drawRunnable = Runnable { drawFrame() }
        private var visible = false
        private var wallpaperConfig = WallpaperConfig()
        private val scope = CoroutineScope(Dispatchers.Main + Job())

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            val repository = FocusPreferencesRepository(applicationContext)
            scope.launch {
                repository.wallpaperConfigFlow.collectLatest { config ->
                    wallpaperConfig = config
                    if (visible) drawFrame()
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(drawRunnable)
            scope.launch { }.cancel()
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val timeData = calculateCurrentTime(wallpaperConfig.timeFormat24Hour)
                    val width = canvas.width
                    val height = canvas.height
                    WallpaperBitmapRenderer.drawWallpaperToCanvas(
                        context = applicationContext,
                        canvas = canvas,
                        config = wallpaperConfig,
                        timeData = timeData,
                        width = width,
                        height = height
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            handler.removeCallbacks(drawRunnable)
            if (visible) {
                handler.postDelayed(drawRunnable, 1000)
            }
        }
    }
}
