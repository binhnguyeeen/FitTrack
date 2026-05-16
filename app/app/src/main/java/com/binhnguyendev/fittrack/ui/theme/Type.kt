package com.binhnguyendev.fittrack.ui.theme

import android.content.res.AssetManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Tiempos families loaded from `assets/fonts/`. The licensed `.otf` files ship
 * Regular/Medium/Semibold/Bold (+ Black for Headline) — there are no italic
 * faces, so italic text uses Compose synthetic slanting (matches the
 * prototype's visual intent for the Fine-italic display text).
 *
 * Initialised once from [FitTrackApplication.onCreate]; FontFamily instances do
 * not need to be created inside composition.
 */
object AppFonts {
    lateinit var text: FontFamily
        private set
    lateinit var headline: FontFamily
        private set
    lateinit var fine: FontFamily
        private set

    fun init(assets: AssetManager) {
        if (::text.isInitialized) return

        text = FontFamily(
            Font("fonts/TiemposText-Regular.otf", assets, FontWeight.Normal),
            Font("fonts/TiemposText-Medium.otf", assets, FontWeight.Medium),
            Font("fonts/TiemposText-Semibold.otf", assets, FontWeight.SemiBold),
            Font("fonts/TiemposText-Bold.otf", assets, FontWeight.Bold),
        )

        headline = FontFamily(
            Font("fonts/TiemposHeadline-Regular.otf", assets, FontWeight.Normal),
            Font("fonts/TiemposHeadline-Medium.otf", assets, FontWeight.Medium),
            Font("fonts/TiemposHeadline-Semibold.otf", assets, FontWeight.SemiBold),
            Font("fonts/TiemposHeadline-Bold.otf", assets, FontWeight.Bold),
            Font("fonts/TiemposHeadline-Black.otf", assets, FontWeight.Black),
        )

        fine = FontFamily(
            Font("fonts/TiemposFine-Regular.otf", assets, FontWeight.Normal),
            Font("fonts/TiemposFine-Medium.otf", assets, FontWeight.Medium),
            Font("fonts/TiemposFine-Bold.otf", assets, FontWeight.Bold),
        )
    }
}

/** Convenience aliases mirroring the prototype's FT.font / fontDisplay / fontFine. */
val FontText: FontFamily get() = AppFonts.text
val FontHeadline: FontFamily get() = AppFonts.headline
val FontFine: FontFamily get() = AppFonts.fine

/** Marker for synthetic-italic display text (Tiempos has no italic face). */
val Italic = FontStyle.Italic
