package com.binhnguyendev.fittrack.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.binhnguyendev.fittrack.data.repository.Repositories

/** App-wide repository graph, provided once from MainActivity. */
val LocalRepositories = staticCompositionLocalOf<Repositories> {
    error("LocalRepositories not provided")
}
