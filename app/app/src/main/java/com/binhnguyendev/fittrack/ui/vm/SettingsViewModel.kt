package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val name: String = "",
    val photoUri: String? = null,
    val units: String = "metric",
    val reminders: Boolean = true,
    val notifications: Boolean = true,
    val use24h: Boolean = false,
    val streak: Int = 0,
)

class SettingsViewModel(private val repos: Repositories) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repos.user.profile,
        repos.prefs.units,
        repos.prefs.reminders,
        repos.prefs.notifications,
        repos.workout.streak,
    ) { profile, units, reminders, notifications, streak ->
        SettingsUiState(
            name = profile?.name ?: "Nguyen",
            photoUri = profile?.photoUri,
            units = units,
            reminders = reminders,
            notifications = notifications,
            use24h = profile?.use24h ?: false,
            streak = streak?.currentStreak ?: 0,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setUnits(v: String) = viewModelScope.launch { repos.prefs.setUnits(v) }
    fun toggleReminders(v: Boolean) = viewModelScope.launch { repos.prefs.setReminders(v) }
    fun toggleNotifications(v: Boolean) = viewModelScope.launch { repos.prefs.setNotifications(v) }
    fun setUse24h(v: Boolean) = viewModelScope.launch { repos.user.setUse24h(v) }
    fun clearHistory() = viewModelScope.launch { repos.workout.clearHistory() }
}
