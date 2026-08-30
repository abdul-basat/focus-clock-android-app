package com.sprinthon.focusclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.ui.theme.FocusAmber

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141417),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = FocusAmber,
    badgeValue: String? = null,
    testTag: String? = null,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360
    val iconSize = if (isSmallScreen) 32.dp else 38.dp
    val iconInnerSize = if (isSmallScreen) 18.dp else 20.dp
    val spacing = if (isSmallScreen) 10.dp else 14.dp
    val horizontalPadding = if (isSmallScreen) 10.dp else 12.dp
    val verticalPadding = if (isSmallScreen) 10.dp else 12.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconInnerSize)
            )
        }

        Spacer(modifier = Modifier.width(spacing))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isSmallScreen) 14.sp else 16.sp
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = if (isSmallScreen) 11.sp else 13.sp
                    ),
                    color = Color(0xFF9E9EA4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (badgeValue != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF222228),
                modifier = Modifier
                    .padding(start = 4.dp, end = 2.dp)
                    .widthIn(max = if (isSmallScreen) 100.dp else 120.dp)
            ) {
                Text(
                    text = badgeValue,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = if (isSmallScreen) 11.sp else 12.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = FocusAmber,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = if (isSmallScreen) 6.dp else 8.dp, vertical = if (isSmallScreen) 3.dp else 4.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF6B6B73),
            modifier = Modifier.size(if (isSmallScreen) 18.dp else 20.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = FocusAmber,
    checked: Boolean,
    testTag: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360
    val iconSize = if (isSmallScreen) 32.dp else 38.dp
    val iconInnerSize = if (isSmallScreen) 18.dp else 20.dp
    val spacing = if (isSmallScreen) 10.dp else 14.dp
    val horizontalPadding = if (isSmallScreen) 10.dp else 12.dp
    val verticalPadding = if (isSmallScreen) 10.dp else 12.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconInnerSize)
                )
            }
            Spacer(modifier = Modifier.width(spacing))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isSmallScreen) 14.sp else 15.sp
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = if (isSmallScreen) 11.sp else 12.sp
                    ),
                    color = Color(0xFF9E9EA4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = FocusAmber,
                uncheckedThumbColor = Color(0xFFA0A0A5),
                uncheckedTrackColor = Color(0xFF282830)
            )
        )
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360
    val horizontalPadding = if (isSmallScreen) 12.dp else 16.dp
    val topPadding = if (isSmallScreen) 16.dp else 20.dp
    
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = if (isSmallScreen) 10.sp else 11.sp
        ),
        color = Color(0xFF888890),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = topPadding, bottom = 8.dp)
    )
}
