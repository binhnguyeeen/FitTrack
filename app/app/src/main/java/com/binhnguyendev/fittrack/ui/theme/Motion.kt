package com.binhnguyendev.fittrack.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Shared motion curves — values taken from the prototype (the rendered source
 * of truth), NOT the spec text.
 *
 *  - FT_EASE        : window.FT_EASE = cubic-bezier(0.25, 0.46, 0.45, 0.94)
 *  - FT_EASE_RIPPLE : FT_EASE_RIPPLE = cubic-bezier(0.4, 0, 0.2, 1)
 */
val FT_EASE: Easing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f)
val FT_EASE_RIPPLE: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

object Motion {
    const val TAB = 320
    const val OVERLAY_ENTER = 380
    const val OVERLAY_EXIT = 280
    const val RIPPLE = 380
    const val SHEET_UP = 400
    const val SHEET_DOWN = 360
    const val PRESS = 120
    const val FADE_IN = 200
    const val FADE_OUT = 360
}
