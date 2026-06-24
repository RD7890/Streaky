package com.rohan.streaky.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Confetti(
    val x: Float,
    val y: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val angle: Float,
    val speed: Float,
    val drift: Float,
    val rotationSpeed: Float
)

private val ConfettiColors = listOf(
    Color(0xFFFF6B1A), Color(0xFFFFB347), Color(0xFF22C55E),
    Color(0xFF3B82F6), Color(0xFFEF4444), Color(0xFF8B5CF6),
    Color(0xFFEC4899), Color(0xFFF59E0B)
)

@Composable
fun ConfettiOverlay(
    active: Boolean,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {}
) {
    if (!active) return

    val particles = remember {
        (0 until 70).map {
            Confetti(
                x             = Random.nextFloat(),
                y             = Random.nextFloat() * -0.25f,        // start just above screen
                color         = ConfettiColors.random(),
                width         = Random.nextFloat() * 10f + 6f,      // 6–16px wide
                height        = Random.nextFloat() * 6f + 3f,       // 3–9px tall
                angle         = Random.nextFloat() * 360f,
                speed         = Random.nextFloat() * 0.4f + 0.3f,   // 0.3–0.7 screen/s (normalised to duration)
                drift         = Random.nextFloat() * 0.12f - 0.06f, // ±6% horizontal over full run
                rotationSpeed = Random.nextFloat() * 240f - 120f    // ±120°/s
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        onDone()
    }

    val time = progress.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val cx  = (p.x + p.drift * time) * w
            val cy  = (p.y + p.speed * time) * h
            val rot = p.angle + p.rotationSpeed * time

            // fade out in the last 30% of the animation
            val alpha = when {
                time > 0.7f -> ((1f - time) / 0.3f).coerceIn(0f, 1f)
                else        -> 1f
            }

            if (cy < h + p.height * 2) {
                rotate(rot, Offset(cx, cy)) {
                    drawRect(
                        color   = p.color.copy(alpha = alpha),
                        topLeft = Offset(cx - p.width / 2f, cy - p.height / 2f),
                        size    = androidx.compose.ui.geometry.Size(p.width, p.height)
                    )
                }
            }
        }
    }
}
