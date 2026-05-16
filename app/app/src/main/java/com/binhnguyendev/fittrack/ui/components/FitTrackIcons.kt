package com.binhnguyendev.fittrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.ui.theme.FT

/**
 * Single stroke-icon set, transcribed verbatim from the prototype's `Icon`
 * switch (design/project/fittrack-frame.jsx) plus the `StreakFlame` glyph.
 * All glyphs use a 0 0 24 24 viewBox; rendered via Compose Canvas + PathParser.
 */
private sealed interface Seg {
    data class P(val d: String, val fill: Boolean = false) : Seg
    data class C(val cx: Float, val cy: Float, val r: Float) : Seg
    data class R(val x: Float, val y: Float, val w: Float, val h: Float, val rx: Float) : Seg
}

private fun segsFor(name: String): List<Seg> = when (name) {
    "home" -> listOf(Seg.P("M3 11l9-7 9 7v9a2 2 0 0 1-2 2h-4v-7h-6v7H5a2 2 0 0 1-2-2z"))
    "calendar" -> listOf(
        Seg.P("M3 6a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"),
        Seg.P("M3 10h18M8 2v4M16 2v4"),
    )
    "clip" -> listOf(
        Seg.P("M9 4h6a1 1 0 0 1 1 1v1h2a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h2V5a1 1 0 0 1 1-1z"),
        Seg.P("M9 13h6M9 17h4"),
    )
    "chart" -> listOf(Seg.P("M3 21h18M6 17V9M11 17V5M16 17v-6M21 17v-3"))
    "plus" -> listOf(Seg.P("M12 5v14M5 12h14"))
    "play" -> listOf(Seg.P("M8 5l12 7-12 7z", fill = true))
    "arrow" -> listOf(Seg.P("M5 12h14M13 5l7 7-7 7"))
    "arrowL" -> listOf(Seg.P("M19 12H5M11 5l-7 7 7 7"))
    "check" -> listOf(Seg.P("M5 13l4 4 10-10"))
    "flame" -> listOf(Seg.P("M12 2s4 4 4 8a4 4 0 0 1-8 0c0-1 .3-2 .8-2.7-.4 1-1.8 2.5-1.8 4.7a5 5 0 0 0 10 0c0-5-5-10-5-10z", fill = true))
    "streakFlame" -> listOf(Seg.P("M13.4 1.2c.6 2.6-.8 4.4-2.4 6.1-1.2 1.3-2.3 2.6-2.3 4.3 0 1.2.7 2 1.7 2.5-2.5-.3-4.2-2.1-4.2-4.7-1.7 2.3-2.7 4.7-2.7 7.3 0 4.2 3.6 7.6 8.2 7.6S20 20.9 20 16.6c0-7.2-6.6-9.3-6.6-15.4z", fill = true))
    "trophy" -> listOf(
        Seg.P("M8 4h8v4a4 4 0 0 1-8 0z"),
        Seg.P("M5 4h3v3a3 3 0 0 1-3-3zM19 4h-3v3a3 3 0 0 0 3-3zM10 12v3M14 12v3M8 18h8M9 15h6v3H9z"),
    )
    "gear" -> listOf(
        Seg.P("M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z"),
        Seg.P("M19 12a7 7 0 0 0-.1-1.2l2-1.5-2-3.5-2.4.8a7 7 0 0 0-2.1-1.2L14 3h-4l-.4 2.4a7 7 0 0 0-2.1 1.2L5.1 5.8l-2 3.5 2 1.5A7 7 0 0 0 5 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.5 2.4-.8c.6.5 1.3.9 2.1 1.2L10 21h4l.4-2.4a7 7 0 0 0 2.1-1.2l2.4.8 2-3.5-2-1.5c.1-.4.1-.8.1-1.2z"),
    )
    "bell" -> listOf(Seg.P("M6 8a6 6 0 0 1 12 0c0 7 3 8 3 8H3s3-1 3-8M10 21a2 2 0 0 0 4 0"))
    "download" -> listOf(Seg.P("M12 4v12M6 12l6 6 6-6M4 20h16"))
    "close" -> listOf(Seg.P("M6 6l12 12M6 18L18 6"))
    "timer" -> listOf(
        Seg.C(12f, 13f, 8f),
        Seg.P("M12 7v6l4 2"),
        Seg.P("M9 2h6M12 5v2"),
    )
    "drag" -> listOf(Seg.P("M9 6h.01M9 12h.01M9 18h.01M15 6h.01M15 12h.01M15 18h.01"))
    "search" -> listOf(Seg.C(11f, 11f, 7f), Seg.P("M21 21l-4.3-4.3"))
    "edit" -> listOf(Seg.P("M12 20h9M16.5 3.5a2.1 2.1 0 1 1 3 3L7 19l-4 1 1-4z"))
    "star" -> listOf(Seg.P("M12 2l3 6.9 7.5.7-5.6 5 1.7 7.4L12 18l-6.6 4 1.7-7.4L1.5 9.6 9 8.9z", fill = true))
    "twitter" -> listOf(Seg.P("M17.53 3H20.5l-6.49 7.42L21.65 21h-5.96l-4.67-6.11L5.7 21H2.73l6.95-7.94L1.97 3h6.11l4.22 5.58L17.53 3zm-1.04 16.17h1.66L7.6 4.73H5.83l10.66 14.44z", fill = true))
    "github" -> listOf(Seg.P("M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.22.66-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.46-1.16-1.11-1.47-1.11-1.47-.9-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.94 0-1.1.39-2 1.03-2.7-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.6 9.6 0 0 1 5 0c1.91-1.3 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.6 1.03 2.7 0 3.84-2.34 4.68-4.57 4.93.36.31.68.92.68 1.85V21c0 .27.16.57.67.48A10 10 0 0 0 12 2z", fill = true))
    "discord" -> listOf(Seg.P("M20.32 4.45a18.4 18.4 0 0 0-4.55-1.4.07.07 0 0 0-.07.03c-.2.35-.41.81-.55 1.16a16.9 16.9 0 0 0-5.16 0c-.15-.36-.37-.81-.56-1.16a.07.07 0 0 0-.07-.03c-1.63.28-3.18.77-4.55 1.4a.06.06 0 0 0-.03.03C1.9 8.65 1.13 12.74 1.5 16.78a.08.08 0 0 0 .03.05 18.5 18.5 0 0 0 5.6 2.85.07.07 0 0 0 .08-.03c.43-.59.81-1.21 1.14-1.86a.07.07 0 0 0-.04-.1c-.6-.23-1.18-.51-1.74-.83a.07.07 0 0 1 0-.12l.35-.27a.07.07 0 0 1 .07 0 13.2 13.2 0 0 0 11.21 0 .07.07 0 0 1 .07 0l.35.27a.07.07 0 0 1 0 .12c-.56.32-1.14.6-1.74.83a.07.07 0 0 0-.04.1c.33.65.71 1.27 1.14 1.86a.07.07 0 0 0 .08.03 18.46 18.46 0 0 0 5.6-2.85.07.07 0 0 0 .03-.05c.44-4.7-.74-8.75-3.13-12.3a.05.05 0 0 0-.03-.03zM8.52 14.32c-1.1 0-2-1.02-2-2.26 0-1.25.88-2.26 2-2.26 1.13 0 2.03 1.02 2 2.26 0 1.24-.88 2.26-2 2.26zm7.42 0c-1.1 0-2-1.02-2-2.26 0-1.25.88-2.26 2-2.26 1.13 0 2.03 1.02 2 2.26 0 1.24-.87 2.26-2 2.26z", fill = true))
    "telegram" -> listOf(Seg.P("M21.94 3.13a.9.9 0 0 0-.95-.16L2.37 10.16a.9.9 0 0 0 .08 1.7l4.55 1.39 2.05 6.43a.9.9 0 0 0 1.42.39l2.6-2.13 4.3 3.18a.9.9 0 0 0 1.42-.55l3-15.5a.9.9 0 0 0-.85-.94zM9.4 14.23l-.5 4.62-1.8-5.66 9.62-7.31-7.32 8.35z", fill = true))
    "youtube" -> listOf(Seg.P("M21.58 7.2a2.5 2.5 0 0 0-1.76-1.77C18.25 5 12 5 12 5s-6.25 0-7.82.43A2.5 2.5 0 0 0 2.42 7.2C2 8.78 2 12 2 12s0 3.22.42 4.8a2.5 2.5 0 0 0 1.76 1.77C5.75 19 12 19 12 19s6.25 0 7.82-.43a2.5 2.5 0 0 0 1.76-1.77C22 15.22 22 12 22 12s0-3.22-.42-4.8zM10 15V9l5.2 3L10 15z", fill = true))
    "gmail" -> listOf(Seg.P("M22 7.3v9.4a2 2 0 0 1-2 2h-2.5v-9L12 13.7 6.5 9.7v9H4a2 2 0 0 1-2-2V7.3a2 2 0 0 1 .9-1.7L4 5l8 5.8L20 5l1.1.6A2 2 0 0 1 22 7.3z", fill = true))
    "camera" -> listOf(
        Seg.P("M3 8a2 2 0 0 1 2-2h2.5l1.5-2h6l1.5 2H19a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"),
        Seg.C(12f, 13f, 4f),
    )
    "user" -> listOf(Seg.C(12f, 8f, 4f), Seg.P("M4 21a8 8 0 0 1 16 0"))
    "image" -> listOf(
        Seg.R(3f, 3f, 18f, 18f, 2f),
        Seg.C(9f, 9f, 2f),
        Seg.P("M21 15l-5-5L5 21"),
    )
    "logout" -> listOf(Seg.P("M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"))
    "treadmill" -> listOf(Seg.P("M3 19l3-9 4 1 6-2 5 1v3l-5 1-3 6"), Seg.C(14f, 4f, 2f))
    "swim" -> listOf(
        Seg.P("M2 17c2 0 2-1.5 4-1.5s2 1.5 4 1.5 2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5"),
        Seg.P("M2 21c2 0 2-1.5 4-1.5s2 1.5 4 1.5 2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5"),
        Seg.C(17f, 6f, 2f),
        Seg.P("M7 14l4-7 4 3"),
    )
    "basket" -> listOf(
        Seg.C(12f, 12f, 9f),
        Seg.P("M3 12h18M12 3v18M5.6 5.6l12.8 12.8M18.4 5.6L5.6 18.4"),
    )
    "routine" -> listOf(Seg.P("M6 5v14M18 5v14M3 9h3M3 15h3M18 9h3M18 15h3M6 12h12"))
    else -> emptyList()
}

private class IconParts(
    val strokePaths: List<Path>,
    val fillPaths: List<Path>,
    val circles: List<Seg.C>,
    val rects: List<Seg.R>,
)

private fun buildParts(name: String): IconParts {
    val segs = segsFor(name)
    val stroke = ArrayList<Path>()
    val fill = ArrayList<Path>()
    val circles = ArrayList<Seg.C>()
    val rects = ArrayList<Seg.R>()
    for (s in segs) when (s) {
        is Seg.P -> {
            val p = PathParser().parsePathString(s.d).toPath()
            if (s.fill) fill.add(p) else stroke.add(p)
        }
        is Seg.C -> circles.add(s)
        is Seg.R -> rects.add(s)
    }
    return IconParts(stroke, fill, circles, rects)
}

@Composable
fun FtIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = FT.text,
    strokeWidth: Float = 1.8f,
) {
    val parts = remember(name) { buildParts(name) }
    Canvas(modifier = modifier.size(size)) {
        val sf = this.size.minDimension / 24f
        scale(sf, sf, pivot = Offset.Zero) {
            val st = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            parts.fillPaths.forEach { drawPath(it, color) }
            parts.strokePaths.forEach { drawPath(it, color, style = st) }
            parts.circles.forEach {
                drawCircle(color, radius = it.r, center = Offset(it.cx, it.cy), style = st)
            }
            parts.rects.forEach {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(it.x, it.y),
                    size = androidx.compose.ui.geometry.Size(it.w, it.h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(it.rx, it.rx),
                    style = st,
                )
            }
        }
    }
}
