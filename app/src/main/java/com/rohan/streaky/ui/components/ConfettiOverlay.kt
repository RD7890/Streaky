package com.rohan.streaky.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.*
import kotlin.random.Random

private data class Confetti(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
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
        (0 until 60).map {
            Confetti(
                x = Random.nextFloat(),
                y = Random.nextFloat(-0.2f, 0f),
                color = ConfettiColors.random(),
                size = Random.nextFloat(8f, 20f),
                angle = Random.nextFloat(0f, 360f),
                speed = Random.nextFloat(0.003f, 0.009f),
                drift = Random.nextFloat(-0.002f, 0.002f),
                rotationSpeed = Random.nextFloat(-8f, 8f)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "time"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        onDone()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val cx = (p.x + p.drift * time * 1000f) * size.width
            val cy = (p.y + p.speed * time * 1000f) * size.height
            val rot = p.angle + p.rotationSpeed * time * 100f
            if (cy < size.height + 50f) {
                rotate(rot, Offset(cx, cy)) {
                    drawRect(
                        color = p.color.copy(alpha = (1f - time).coerceAtLeast(0f)),
                        topLeft = Offset(cx - p.size / 2, cy - p.size / 4),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size / 2)
                    )
                }
            }
        }
    }
}
