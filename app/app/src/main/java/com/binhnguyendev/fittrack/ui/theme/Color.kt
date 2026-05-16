package com.binhnguyendev.fittrack.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * FitTrack design tokens — taken verbatim from the prototype's `FT` object
 * (design/project/fittrack-frame.jsx). Dark only.
 */
object FT {
    val bg = Color(0xFF0F0F11)
    val surface = Color(0xFF1A1A1C)
    val raised = Color(0xFF242427)
    val border = Color(0xFF2E2E32)
    val divider = Color(0xFF1F1F22)
    val text = Color(0xFFFFFFFF)
    val text2 = Color(0xFFA3A3A8)
    val text3 = Color(0xFF6E6E73)

    val orange = Color(0xFFFF6B35)
    val orangeSoft = Color(0x26FF6B35) // rgba(255,107,53,0.15)
    val blue = Color(0xFF00B4D8)
    val blueSoft = Color(0x2600B4D8)
    val green = Color(0xFF4CAF50)
    val greenSoft = Color(0x264CAF50)
    val purple = Color(0xFF9C27B0)
    val purpleSoft = Color(0x269C27B0)

    // White overlays used pervasively for borders / hairlines / tracks.
    val whiteA04 = Color(0x0AFFFFFF)
    val whiteA05 = Color(0x0DFFFFFF)
    val whiteA06 = Color(0x0FFFFFFF)
    val whiteA08 = Color(0x14FFFFFF)
    val whiteA10 = Color(0x1AFFFFFF)
    val whiteA50 = Color(0x80FFFFFF)

    // Orange overlays (badges, ripple wash, PB callouts, heatmap).
    val orangeA06 = Color(0x0FFF6B35)
    val orangeA10 = Color(0x1AFF6B35)
    val orangeA12 = Color(0x1FFF6B35)
    val orangeA30 = Color(0x4DFF6B35)
    val orangeA35 = Color(0x59FF6B35)
    val orangeA40 = Color(0x66FF6B35)
    val orangeA60 = Color(0x99FF6B35)

    // Misc surfaces.
    val tabBarBg = Color(0xEB1A1A1C) // rgba(26,26,28,0.92)
    val scrim = Color(0x8C000000) // rgba(0,0,0,0.55)
    val destructive = Color(0xFFC0392B)
    val black = Color(0xFF000000)

    /** Heatmap intensity 0..4 (StatsScreen). */
    fun heat(level: Int): Color = when (level) {
        0 -> surface
        1 -> orangeSoft // 0.15
        2 -> orangeA35 // 0.35
        3 -> orangeA60 // 0.60
        else -> orange // 1.0
    }
}
