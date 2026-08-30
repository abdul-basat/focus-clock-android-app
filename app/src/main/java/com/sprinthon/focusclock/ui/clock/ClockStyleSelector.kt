package com.sprinthon.focusclock.ui.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.ui.theme.AmoledBlack
import com.sprinthon.focusclock.ui.theme.DarkCardSurface
import com.sprinthon.focusclock.ui.theme.DarkElevatedSurface
import com.sprinthon.focusclock.ui.theme.DarkOutline
import com.sprinthon.focusclock.ui.theme.DarkSurface
import com.sprinthon.focusclock.ui.theme.FocusAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockStyleBottomSheet(
    selectedStyle: ClockStyle,
    timeData: ClockTimeData,
    onStyleSelected: (ClockStyle) -> Unit,
    onDismiss: () -> Unit,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null,
        modifier = Modifier.testTag("clock_style_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Clock Style",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Select visual presentation for your focus clock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_clock_style_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Grid of styles with live previews
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ClockStyle.values()) { style ->
                    ClockStyleCard(
                        style = style,
                        isSelected = style == selectedStyle,
                        timeData = timeData,
                        clockFont = clockFont,
                        onClick = {
                            onStyleSelected(style)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ClockStyleCard(
    style: ClockStyle,
    isSelected: Boolean,
    timeData: ClockTimeData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    clockFont: ClockFont = ClockFont.BEBAS_NEUE
) {
    val cornerShape = RoundedCornerShape(16.dp)
    val borderColor = if (isSelected) FocusAmber else DarkOutline
    val borderWidth = if (isSelected) 1.5.dp else 0.5.dp

    Column(
        modifier = modifier
            .clip(cornerShape)
            .background(if (isSelected) DarkElevatedSurface else DarkCardSurface)
            .border(width = borderWidth, color = borderColor, shape = cornerShape)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("style_card_${style.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Live miniature preview using the actual renderer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AmoledBlack),
            contentAlignment = Alignment.Center
        ) {
            ClockRenderer(
                style = style,
                timeData = timeData,
                clockFont = clockFont,
                scale = 0.65f,
                showDate = false,
                showDayOfWeek = false
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(FocusAmber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = style.displayName,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = if (isSelected) FocusAmber else Color.White,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = style.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color.White.copy(alpha = 0.55f),
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
