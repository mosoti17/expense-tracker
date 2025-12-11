package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category

/**
 * Composable that displays a category icon with a colored circular background
 *
 * @param category The category to display
 * @param size Size of the icon container (default 48.dp)
 * @param iconSize Size of the icon itself (default 24.dp)
 * @param modifier Optional modifier
 */
@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(category.color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = category.color,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Composable that displays a category icon with custom colors
 */
@Composable
fun CategoryIconCustom(
    category: Category,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
