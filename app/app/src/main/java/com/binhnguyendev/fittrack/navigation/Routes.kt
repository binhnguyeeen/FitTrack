package com.binhnguyendev.fittrack.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val TEMPLATES = "templates"
    const val TEMPLATES_FLOW = "templates/flow"
    const val TEMPLATES_CREATE = "templates/create"
    const val TEMPLATES_ADD = "templates/add"
    const val WORKOUT = "workout/{kind}/{templateId}"
    const val SUMMARY = "summary/{sessionId}"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile"

    fun workout(kind: String, templateId: Long) = "workout/$kind/$templateId"
    fun summary(sessionId: Long) = "summary/$sessionId"

    /** The four bottom-tab destinations, in index order. */
    val TABS = listOf(HOME, CALENDAR, TEMPLATES, STATS)

    /** Routes that show the bottom tab bar (tab roots only). */
    val TAB_BAR_ROUTES = setOf(HOME, CALENDAR, TEMPLATES, STATS)

    /** Soft push-up overlay routes (spec STEP 5). */
    val OVERLAY_ROUTES = setOf(SETTINGS, EDIT_PROFILE, TEMPLATES_CREATE, TEMPLATES_ADD)

    fun tabIndex(route: String?): Int = TABS.indexOf(route).coerceAtLeast(0)
}
