package com.sprinthon.focusclock.ui.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ClockTimeData(
    val hourString: String,
    val minuteString: String,
    val secondString: String,
    val dayOfWeek: String,
    val dateString: String,
    val fullDateString: String,
    val hourInt: Int,
    val minuteInt: Int,
    val secondInt: Int,
    val is24Hour: Boolean,
    val amPm: String
)

@Composable
fun rememberCurrentTimeData(is24Hour: Boolean = true): ClockTimeData {
    var timeData by remember(is24Hour) {
        mutableStateOf(calculateCurrentTime(is24Hour))
    }

    LaunchedEffect(is24Hour) {
        while (isActive) {
            timeData = calculateCurrentTime(is24Hour)
            // Synchronize with next second tick
            val currentMillis = System.currentTimeMillis()
            val delayMillis = 1000L - (currentMillis % 1000L)
            delay(delayMillis.coerceAtLeast(100L))
        }
    }

    return timeData
}

fun calculateCurrentTime(is24Hour: Boolean): ClockTimeData {
    val now = Date()
    val calendar = Calendar.getInstance().apply { time = now }

    val hourFormat = if (is24Hour) "HH" else "hh"
    val hourStr = SimpleDateFormat(hourFormat, Locale.getDefault()).format(now)
    val minuteStr = SimpleDateFormat("mm", Locale.getDefault()).format(now)
    val secondStr = SimpleDateFormat("ss", Locale.getDefault()).format(now)
    val dayOfWeekStr = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
    val shortDayStr = SimpleDateFormat("EEE", Locale.getDefault()).format(now)
    val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(now)
    val fullDateStr = SimpleDateFormat("EEEE · MMMM d", Locale.getDefault()).format(now)
    val amPmStr = SimpleDateFormat("a", Locale.getDefault()).format(now)

    return ClockTimeData(
        hourString = hourStr,
        minuteString = minuteStr,
        secondString = secondStr,
        dayOfWeek = dayOfWeekStr,
        dateString = "$shortDayStr · $dateStr",
        fullDateString = fullDateStr,
        hourInt = calendar.get(Calendar.HOUR_OF_DAY),
        minuteInt = calendar.get(Calendar.MINUTE),
        secondInt = calendar.get(Calendar.SECOND),
        is24Hour = is24Hour,
        amPm = amPmStr
    )
}
