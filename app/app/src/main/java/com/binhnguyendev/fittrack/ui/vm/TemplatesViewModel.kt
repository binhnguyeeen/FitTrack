package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.db.TemplateListItem
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TemplatesUiState(
    val templates: List<TemplateListItem> = emptyList(),
    val loading: Boolean = true,
)

class TemplatesViewModel(repos: Repositories) : ViewModel() {
    private val loading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            delay(900)
            loading.value = false
        }
    }

    val uiState: StateFlow<TemplatesUiState> = combine(
        repos.template.templatesWithCount,
        loading,
    ) { list, isLoading ->
        TemplatesUiState(list, isLoading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemplatesUiState())
}
