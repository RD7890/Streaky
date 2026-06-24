package com.rohan.streaky.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohan.streaky.data.db.entity.HabitEntity
import com.rohan.streaky.ui.theme.OrangePrimary
import com.rohan.streaky.ui.utils.CategoryIcons

@Composable
fun HabitCompletionRow(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tapped by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue      = if (tapped) 0.97f else 1f,
        animationSpec    = tween(120, easing = FastOutSlowInEasing),
        label            = "cardScale",
        finishedListener = { tapped = false }
    )

    val iconRes = CategoryIcons.drawableRes(habit.iconEmoji)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable {
                tapped = true
                onHabitClick()
            },
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                OrangePrimary.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border    = if (isCompleted)
            BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.3f))
        else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedCounter(
                count    = habit.currentStreak,
                modifier = Modifier.width(40.dp),
                style    = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize   = 22.sp,
                    color      = if (habit.currentStreak > 0) OrangePrimary
                                 else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Color(android.graphics.Color.parseColor(habit.colorHex)).copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(iconRes),
                    contentDescription = habit.category,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = habit.name,
                    style    = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                Text(
                    text     = habit.category + if (habit.currentStreak > 0) " · ${habit.currentStreak}d streak" else "",
                    style    = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }

            Spacer(Modifier.width(12.dp))

            AnimatedCheckButton(
                checked  = isCompleted,
                onToggle = onToggle,
                size     = 44.dp
            )
        }
    }
}
