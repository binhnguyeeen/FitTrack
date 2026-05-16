package com.binhnguyendev.fittrack.ui.theme

import androidx.compose.ui.graphics.Color
import com.binhnguyendev.fittrack.data.db.ActivityKind

/** Per-kind visual metadata (prototype's `ACT` map / spec STEP 3). */
data class ActivityMeta(
    val color: Color,
    val soft: Color,
    val label: String,
    val icon: String,
)

val ActivityKind.meta: ActivityMeta
    get() = when (this) {
        ActivityKind.TREADMILL -> ActivityMeta(FT.green, FT.greenSoft, "Treadmill", "treadmill")
        ActivityKind.SWIM -> ActivityMeta(FT.blue, FT.blueSoft, "Swimming", "swim")
        ActivityKind.BASKETBALL -> ActivityMeta(FT.orange, FT.orangeSoft, "Basketball", "basket")
        ActivityKind.ROUTINE -> ActivityMeta(FT.purple, FT.purpleSoft, "Routine", "routine")
    }
