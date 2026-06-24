package com.rohan.streaky.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohan.streaky.ui.theme.OrangePrimary

@Composable
fun AnimatedCheckButton(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    enabled: Boolean = true
) {
    val haptic  = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.82f else 1f,
        animationSpec = tween(120, easing = FastOutSlowInEasing),
        label         = "btnScale",
        finishedListener = { pressed = false }
    )

    val bgColor by animateColorAsState(
        targetValue   = if (checked) OrangePrimary else Color.Transparent,
        animationSpec = tween(180),
        label         = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue   = if (checked) OrangePrimary else Color(0xFFDDDDDD),
        animationSpec = tween(180),
        label         = "border"
    )
    val iconAlpha by animateFloatAsState(
        targetValue   = if (checked) 1f else 0f,
        animationSpec = tween(150),
        label         = "iconAlpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(
                if (!checked) Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(borderColor.copy(alpha = 0.12f))
                else Modifier
            )
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                pressed = true
                onToggle()
            },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector     = Icons.Filled.Check,
                contentDescription = "Done",
                tint            = Color.White.copy(alpha = iconAlpha),
                modifier        = Modifier.size(size * 0.55f)
            )
        }
    }
}
