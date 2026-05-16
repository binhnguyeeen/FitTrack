package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.Composable
import com.binhnguyendev.fittrack.data.repository.Repositories
import com.binhnguyendev.fittrack.ui.LocalRepositories

/**
 * Tiny manual VM injection: builds the ViewModel from the app's repository
 * graph (no DI framework, per spec STEP 8). [key] scopes a distinct instance
 * per route argument (e.g. workout templateId / summary sessionId).
 */
@Composable
inline fun <reified VM : ViewModel> ftViewModel(
    key: String? = null,
    crossinline create: (Repositories) -> VM,
): VM {
    val repos = LocalRepositories.current
    return viewModel(
        key = key,
        factory = viewModelFactory { initializer { create(repos) } },
    )
}

/** Like [ftViewModel] but scoped to [owner] (e.g. a parent nav-graph entry)
 *  so multiple destinations share one instance. */
@Composable
inline fun <reified VM : ViewModel> ftScopedViewModel(
    owner: ViewModelStoreOwner,
    crossinline create: (Repositories) -> VM,
): VM {
    val repos = LocalRepositories.current
    return viewModel(
        viewModelStoreOwner = owner,
        factory = viewModelFactory { initializer { create(repos) } },
    )
}
