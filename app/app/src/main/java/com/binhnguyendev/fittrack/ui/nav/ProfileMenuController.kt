package com.binhnguyendev.fittrack.ui.nav

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/** Toggles the Home avatar's ProfileMenu sheet, rendered at the app root. */
class ProfileMenuController {
    var open by mutableStateOf(false)
        private set

    fun show() {
        open = true
    }

    fun dismiss() {
        open = false
    }
}

val LocalProfileMenu = staticCompositionLocalOf { ProfileMenuController() }
