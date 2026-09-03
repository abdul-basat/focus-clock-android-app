package com.sprinthon.focusclock

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.ui.clock.AnalogClockDialEngine
import com.sprinthon.focusclock.ui.clock.ClockTimeData
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnalogClockDialEngineTest {

    @Test
    fun testDrawDialExecutionForHorizontalUprightMode() {
        val timeData = ClockTimeData(
            hourString = "10",
            minuteString = "10",
            secondString = "30",
            dayOfWeek = "Tuesday",
            dateString = "Sep 1",
            fullDateString = "Tuesday, Sep 1",
            hourInt = 10,
            minuteInt = 10,
            secondInt = 30,
            is24Hour = false,
            amPm = "AM"
        )

        val drawScope = CanvasDrawScope()
        val canvas = androidx.compose.ui.graphics.Canvas(android.graphics.Canvas(android.graphics.Bitmap.createBitmap(300, 300, android.graphics.Bitmap.Config.ARGB_8888)))
        
        drawScope.draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = Size(300f, 300f)
        ) {
            AnalogClockDialEngine.drawDial(
                drawScope = this,
                timeData = timeData,
                primaryColor = Color.White,
                secondaryColor = Color.Gray,
                accentColor = Color.Red,
                numeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
                showSeconds = true
            )
        }

        assertNotNull(canvas)
    }

    @Test
    fun testDrawDialExecutionForAllOrientations() {
        val timeData = ClockTimeData(
            hourString = "03",
            minuteString = "45",
            secondString = "00",
            dayOfWeek = "Tuesday",
            dateString = "Sep 1",
            fullDateString = "Tuesday, Sep 1",
            hourInt = 3,
            minuteInt = 45,
            secondInt = 0,
            is24Hour = false,
            amPm = "PM"
        )

        val drawScope = CanvasDrawScope()
        val canvas = androidx.compose.ui.graphics.Canvas(android.graphics.Canvas(android.graphics.Bitmap.createBitmap(300, 300, android.graphics.Bitmap.Config.ARGB_8888)))

        for (orientation in AnalogNumeralOrientation.values()) {
            drawScope.draw(
                density = Density(1f),
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = Size(300f, 300f)
            ) {
                AnalogClockDialEngine.drawDial(
                    drawScope = this,
                    timeData = timeData,
                    primaryColor = Color.White,
                    secondaryColor = Color.LightGray,
                    accentColor = Color.Yellow,
                    numeralOrientation = orientation,
                    showSeconds = false
                )
            }
        }

        assertNotNull(canvas)
    }

    @Test
    fun testDrawDialWithNumeralSizesAndEnlargedScales() {
        val timeData = ClockTimeData(
            hourString = "12",
            minuteString = "00",
            secondString = "00",
            dayOfWeek = "Wednesday",
            dateString = "Sep 2",
            fullDateString = "Wednesday, Sep 2",
            hourInt = 12,
            minuteInt = 0,
            secondInt = 0,
            is24Hour = false,
            amPm = "PM"
        )

        val drawScope = CanvasDrawScope()
        val canvas = androidx.compose.ui.graphics.Canvas(android.graphics.Canvas(android.graphics.Bitmap.createBitmap(400, 400, android.graphics.Bitmap.Config.ARGB_8888)))

        for (size in com.sprinthon.focusclock.domain.model.AnalogNumeralSize.values()) {
            drawScope.draw(
                density = Density(2f),
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = Size(400f, 400f)
            ) {
                AnalogClockDialEngine.drawDial(
                    drawScope = this,
                    timeData = timeData,
                    primaryColor = Color.White,
                    secondaryColor = Color.LightGray,
                    accentColor = Color.Yellow,
                    numeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
                    showSeconds = true,
                    analogNumeralSize = size,
                    analogNumeralScale = size.scale
                )
            }
        }

        assertNotNull(canvas)
    }
}
