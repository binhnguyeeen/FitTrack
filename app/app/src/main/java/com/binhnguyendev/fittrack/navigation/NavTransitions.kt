package com.binhnguyendev.fittrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry
import com.binhnguyendev.fittrack.ui.theme.FT_EASE
import com.binhnguyendev.fittrack.ui.theme.Motion

/**
 * Per-route transitions matching the prototype's animation system:
 *  - tab ↔ tab : directional slide + scale + fade, 320ms
 *  - → overlay : soft push-up (0.7α + translateY 24dp → settled), 380ms
 *  - overlay → : reverse push-up, 280ms
 *  - ripple routes (onboarding/workout/summary and returns) : no NavHost
 *    animation — the RippleHost overlay covers the swap instead.
 */

private fun base(route: String?): String? = route?.substringBefore('/')

private fun isRipple(route: String?): Boolean {
    val b = base(route)
    return b == "onboarding" || b == "workout" || b == "summary" || b == "home"
}

private fun bothTabs(from: String?, to: String?): Boolean =
    Routes.TAB_BAR_ROUTES.contains(from) && Routes.TAB_BAR_ROUTES.contains(to)

private fun tabDir(from: String?, to: String?): Int {
    val d = Routes.tabIndex(to) - Routes.tabIndex(from)
    return if (d == 0) 1 else (d / kotlin.math.abs(d))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.ftEnter(): EnterTransition {
    val from = initialState.destination.route
    val to = targetState.destination.route
    return when {
        Routes.OVERLAY_ROUTES.contains(to) ->
            fadeIn(tween(Motion.OVERLAY_ENTER, easing = FT_EASE), initialAlpha = 0.7f) +
                slideInVertically(tween(Motion.OVERLAY_ENTER, easing = FT_EASE)) { (it * 0.06f).toInt() }

        bothTabs(from, to) -> {
            val dir = tabDir(from, to)
            fadeIn(tween(Motion.TAB, easing = FT_EASE)) +
                scaleIn(tween(Motion.TAB, easing = FT_EASE), initialScale = 0.96f) +
                slideInHorizontally(tween(Motion.TAB, easing = FT_EASE)) { (dir * it * 0.08f).toInt() }
        }

        isRipple(to) || isRipple(from) -> EnterTransition.None

        else -> fadeIn(tween(Motion.TAB, easing = FT_EASE))
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.ftExit(): ExitTransition {
    val from = initialState.destination.route
    val to = targetState.destination.route
    return when {
        Routes.OVERLAY_ROUTES.contains(to) -> fadeOut(tween(Motion.OVERLAY_ENTER, easing = FT_EASE), targetAlpha = 0.85f)

        bothTabs(from, to) -> {
            val dir = tabDir(from, to)
            fadeOut(tween(Motion.TAB, easing = FT_EASE)) +
                scaleOut(tween(Motion.TAB, easing = FT_EASE), targetScale = 0.96f) +
                slideOutHorizontally(tween(Motion.TAB, easing = FT_EASE)) { (-dir * it * 0.08f).toInt() }
        }

        isRipple(to) || isRipple(from) -> ExitTransition.None

        else -> fadeOut(tween(Motion.TAB, easing = FT_EASE))
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.ftPopEnter(): EnterTransition {
    val to = targetState.destination.route
    return when {
        isRipple(to) -> EnterTransition.None
        Routes.TAB_BAR_ROUTES.contains(to) -> fadeIn(tween(Motion.OVERLAY_EXIT, easing = FT_EASE))
        else -> fadeIn(tween(Motion.OVERLAY_EXIT, easing = FT_EASE))
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.ftPopExit(): ExitTransition {
    val from = initialState.destination.route
    return when {
        Routes.OVERLAY_ROUTES.contains(from) ->
            fadeOut(tween(Motion.OVERLAY_EXIT, easing = FT_EASE)) +
                slideOutVertically(tween(Motion.OVERLAY_EXIT, easing = FT_EASE)) { (it * 0.06f).toInt() }

        isRipple(from) -> ExitTransition.None
        else -> fadeOut(tween(Motion.OVERLAY_EXIT, easing = FT_EASE))
    }
}
