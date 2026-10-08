package com.sepideh.lilo.core.presentation.components.brand

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sepideh.lilo.ui.theme.HomeBrandLight
import kotlin.math.cos
import kotlin.math.sin

/** Shared vector geometry: the clip unfolds into lil, and its circular backdrop becomes o. */
@Composable
fun LiloLogoMorph(progress: Float, modifier: Modifier = Modifier, background: Color = Color.White) {
    Canvas(modifier.semantics { contentDescription = "lilo" }) {
        translate(size.width / 2f, size.height / 2f) {
            scale(size.width / 360f, pivot = Offset.Zero) {
                drawLiloFrame(progress, HomeBrandLight, background)
            }
        }
    }
}

/** Static wordmark uses the exact letter geometry of the completed splash morph. */
@Composable
fun LiloWordmark(modifier: Modifier = Modifier, color: Color = HomeBrandLight) {
    Canvas(modifier.semantics { contentDescription = "lilo" }) {
        val factor = minOf(size.width / 134.5f, size.height / 68f)
        translate((size.width - 134.5f * factor) / 2f, (size.height - 68f * factor) / 2f) {
            scale(factor, pivot = Offset.Zero) {
                translate(65.5f, 36.5f) {
                    drawLiloFrame(1f, color, Color.Transparent)
                }
            }
        }
    }
}

/** Both the header and splash render this same frame; only their display scale differs. */
private fun DrawScope.drawLiloFrame(progress: Float, color: Color, background: Color) {
    val p = progress.coerceIn(0f, 1f)
    val t = smooth(p)
    val ink = lerp(Color.White, color, smooth(((p - .13f) / .48f).coerceIn(0f, 1f)))
    val circleCenter = Offset(43f * t, 5.5f * t)
    if (p == 1f) {
        drawCircle(color, radius = 20.5f, center = circleCenter, style = Stroke(11f))
    } else {
        drawCircle(color, radius = mix(54f, 26f, t), center = circleCenter)
        val hole = 15f * smooth(((p - .22f) / .78f).coerceIn(0f, 1f))
        if (hole > 0f) drawCircle(background, radius = hole, center = circleCenter)
    }
    LogoGeometry.clipParts.forEachIndexed { index, start ->
        val end = LogoGeometry.letters[index]
        val path = Path()
        start.forEachIndexed { i, point ->
            val x = mix(point.x, end[i].x, t)
            val y = mix(point.y, end[i].y, t)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, ink, style = Stroke(mix(5.5f, 11f, t), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
    val dot = smooth(((p - .36f) / .42f).coerceIn(0f, 1f))
    if (dot > 0f) drawCircle(ink, radius = 5.5f * dot, center = Offset(-28f * t, mix(-20f, -29f, t)))
}

private fun mix(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun smooth(t: Float) = t * t * (3f - 2f * t)

/** Sample once by arc length so every morph frame has matching points and smooth bends. */
private object LogoGeometry {
    private fun cubic(points: MutableList<Offset>, a: Offset, b: Offset, c: Offset, d: Offset) {
        for (i in 1..40) {
            val t = i / 40f
            val u = 1f - t
            points += a * (u * u * u) + b * (3f * u * u * t) + c * (3f * u * t * t) + d * (t * t * t)
        }
    }

    private fun sample(points: List<Offset>, count: Int): List<Offset> {
        val distances = FloatArray(points.size)
        for (i in 1 until points.size) distances[i] = distances[i - 1] + (points[i] - points[i - 1]).getDistance()
        var segment = 1
        return List(count) { i ->
            val distance = distances.last() * i / (count - 1)
            while (segment < points.lastIndex && distances[segment] < distance) segment++
            val t = (distance - distances[segment - 1]) / (distances[segment] - distances[segment - 1])
            points[segment - 1] + (points[segment] - points[segment - 1]) * t
        }
    }

    private val clip = run {
        val points = mutableListOf(Offset(4f, -13f), Offset(4f, 18f))
        cubic(points, Offset(4f, 18f), Offset(4f, 22.9706f), Offset(-.0294f, 27f), Offset(-5f, 27f))
        cubic(points, Offset(-5f, 27f), Offset(-9.9706f, 27f), Offset(-14f, 22.9706f), Offset(-14f, 18f))
        points += Offset(-14f, -19f)
        cubic(points, Offset(-14f, -19f), Offset(-14f, -27.8366f), Offset(-6.8366f, -35f), Offset(2f, -35f))
        cubic(points, Offset(2f, -35f), Offset(10.8366f, -35f), Offset(18f, -27.8366f), Offset(18f, -19f))
        points += Offset(18f, 20f)
        cubic(points, Offset(18f, 20f), Offset(18f, 31.8741f), Offset(8.3741f, 41.5f), Offset(-3.5f, 41.5f))
        cubic(points, Offset(-3.5f, 41.5f), Offset(-15.3741f, 41.5f), Offset(-25f, 31.8741f), Offset(-25f, 20f))
        points += Offset(-25f, -23f)
        val angle = .52f
        sample(points.map { Offset(it.x * cos(angle) + it.y * sin(angle), -it.x * sin(angle) + it.y * cos(angle)) }, 301)
    }
    val clipParts = listOf(clip.subList(0, 101), clip.subList(100, 201), clip.subList(200, 301))

    private fun letterL(x: Float): List<Offset> {
        val points = mutableListOf(Offset(x, -31f), Offset(x, 13f))
        // Same softly rounded foot as the approved preview.
        for (i in 1..30) {
            val t = i / 30f
            val u = 1f - t
            points += Offset(x + 13f * t * t, u * u * 13f + 2f * u * t * 26f + t * t * 26f)
        }
        return sample(points, 101)
    }
    val letters = listOf(letterL(-60f), sample(listOf(Offset(-28f, -7f), Offset(-28f, 26f)), 101), letterL(-9f))
}
