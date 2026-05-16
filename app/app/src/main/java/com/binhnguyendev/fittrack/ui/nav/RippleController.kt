package com.binhnguyendev.fittrack.ui.nav

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FT_EASE_RIPPLE
import kotlin.math.hypot

/**
 * Orange ripple that expands from a tapped point to fill the screen, then cuts
 * to the new screen and fades — the prototype's non-tab navigation transition
 * (FitTrackApp.goRoute ripple branch).
 */
class RippleController {
    var request by mutableStateOf<RippleRequest?>(null)
        private set

    /** [center] is in root pixel coordinates. [onCovered] runs once the
     *  ripple fully covers the screen (perform the navigation there). */
    fun launch(center: Offset, color: Color = FT.orange, onCovered: () -> Unit) {
        if (request != null) return
        request = RippleRequest(center, color, onCovered)
    }

    fun finish() {
        request = null
    }
}

data class RippleRequest(
    val center: Offset,
    val color: Color,
    val onCovered: () -> Unit,
)

val LocalRipple = staticCompositionLocalOf { RippleController() }

@Composable
fun RippleHost(controller: RippleController, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val req = controller.request ?: return@BoxWithConstraints
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val hPx = with(density) { maxHeight.toPx() }

        val radius = remember(req) { Animatable(10f) }
        val alpha = remember(req) { Animatable(0.92f) }

        LaunchedEffect(req) {
            val maxR = hypot(
                maxOf(req.center.x, wPx - req.center.x).toDouble(),
                maxOf(req.center.y, hPx - req.center.y).toDouble(),
            ).toFloat() + 24f
            radius.animateTo(maxR, tween(380, easing = FT_EASE_RIPPLE))
            // Fully covered → swap screens behind the ripple, then fade out.
            req.onCovered()
            alpha.animateTo(0f, tween(180))
            controller.finish()
        }

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = req.color,
                radius = radius.value,
                center = req.center,
                alpha = alpha.value,
            )
        }
    }
}
