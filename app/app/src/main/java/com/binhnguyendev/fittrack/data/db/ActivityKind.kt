package com.binhnguyendev.fittrack.data.db

/**
 * The four activity kinds. Stored in Room as the enum name (see [Converters]).
 * The prototype keys these as treadmill/swim/basket/routine — [fromKey]/[key]
 * bridge that naming.
 */
enum class ActivityKind {
    TREADMILL,
    SWIM,
    BASKETBALL,
    ROUTINE;

    val key: String
        get() = when (this) {
            TREADMILL -> "treadmill"
            SWIM -> "swim"
            BASKETBALL -> "basket"
            ROUTINE -> "routine"
        }

    companion object {
        fun fromKey(key: String): ActivityKind = when (key) {
            "treadmill" -> TREADMILL
            "swim" -> SWIM
            "basket", "basketball" -> BASKETBALL
            else -> ROUTINE
        }
    }
}
