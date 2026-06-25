package com.rohan.streaky.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rohan.streaky.R
import com.rohan.streaky.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    var visible by remember { mutableStateOf(false) }
    val logoAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.88f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.scale(logoScale)
        ) {
            Image(
                painter            = painterResource(R.drawable.flame_running),
                contentDescription = "Streaky",
                modifier           = Modifier
                    .size((80 * logoAlpha).coerceAtLeast(1f).dp)
            )
            Text(
                text  = "Streaky",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color      = OrangePrimary
                )
            )
            Text(
                text  = "Build habits that stick",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
