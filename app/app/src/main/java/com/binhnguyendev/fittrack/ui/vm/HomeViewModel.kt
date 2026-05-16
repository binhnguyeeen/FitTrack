package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.DateUtils
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.db.WorkoutSession
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class RecentItem(
    val kind: ActivityKind,
    val title: String,
    val detail: String,
    val whenLabel: String,
    val duration: String,
)

data class TodayPlan(
    val kind: ActivityKind,
    val label: String,
    val templateId: Long?,
    val exerciseCount: Int?,
)

data class HomeUiState(
    val name: String = "",
    val photoUri: String? = null,
    val streak: Int = 0,
    val today: TodayPlan? = null,
    val recent: List<RecentItem> = emptyList(),
    val loading: Boolean = true,
)

class HomeViewModel(private val repos: Repositories) : ViewModel() {

    private val loading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            delay(900) // brief skeleton, mirrors prototype useLoading(1200)
            loading.value = false
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repos.user.profile,
        repos.workout.streak,
        repos.workout.recentSessions,
        repos.calendar.plannedForDate(DateUtils.todayUtc()),
        loading,
    ) { profile, streak, recent, plannedToday, isLoading ->
        val plan = plannedToday.firstOrNull()
        HomeUiState(
            name = profile?.name ?: "Nguyen",
            photoUri = profile?.photoUri,
            streak = streak?.currentStreak ?: 0,
            today = plan?.let {
                TodayPlan(it.activityKind, it.label, it.templateId, null)
            },
            recent = recent.map { it.toItem() },
            loading = isLoading,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private fun WorkoutSession.toItem(): RecentItem {
        val detail = when {
            distanceMeters != null && activityKind == ActivityKind.SWIM ->
                "${"%,d".format(distanceMeters.toInt())}m"
            distanceMeters != null ->
                "${"%.1f".format(distanceMeters / 1000f)} km"
            volumeKg != null -> "${"%.1f".format(volumeKg / 1000f)}k kg"
            else -> "Workout"
        }
        return RecentItem(
            kind = activityKind,
            title = kindTitle(activityKind),
            detail = detail,
            whenLabel = relativeDay(startTime),
            duration = "$durationMinutes min",
        )
    }

    private fun kindTitle(k: ActivityKind) = when (k) {
        ActivityKind.TREADMILL -> "Treadmill"
        ActivityKind.SWIM -> "Swimming"
        ActivityKind.BASKETBALL -> "Basketball"
        ActivityKind.ROUTINE -> "Routine"
    }

    private fun relativeDay(startMillis: Long): String {
        val days = DateUtils.daysBetween(
            DateUtils.startOfDayUtc(startMillis),
            DateUtils.todayUtc(),
        )
        return when (days) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> "$days days ago"
        }
    }
}
