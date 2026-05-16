package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PbRow(val kind: ActivityKind, val label: String, val detail: String, val date: String)
data class SessionRow(val kind: ActivityKind, val name: String, val date: String, val time: String)

data class StatsUiState(
    val heat: List<List<Int>> = emptyList(),
    val pbs: List<PbRow> = emptyList(),
    val sessions: List<SessionRow> = emptyList(),
    val loading: Boolean = true,
)

class StatsViewModel(repos: Repositories) : ViewModel() {
    private val loading = MutableStateFlow(true)
    private val df = SimpleDateFormat("MMM d", Locale.ENGLISH)

    // Prototype seed shown until real history exists (spec CONSTRAINT 3).
    private val seedPbs = listOf(
        PbRow(ActivityKind.SWIM, "Swimming", "2.1 km · 44 min", "May 10"),
        PbRow(ActivityKind.TREADMILL, "Treadmill", "6.0 km · 35 min", "May 8"),
        PbRow(ActivityKind.BASKETBALL, "Basketball", "5.4 km · 62 min", "May 3"),
    )

    init {
        viewModelScope.launch {
            delay(900)
            loading.value = false
        }
    }

    val uiState: StateFlow<StatsUiState> = combine(
        repos.stats.heatmap,
        repos.stats.personalBests,
        repos.stats.allSessions,
        loading,
    ) { heat, pbs, sessions, isLoading ->
        val pbRows = if (pbs.isEmpty()) {
            seedPbs
        } else {
            pbs.take(3).map { pb ->
                val detail = when {
                    pb.bestWeightKg != null && pb.bestReps != null ->
                        "${trim(pb.bestWeightKg)}kg × ${pb.bestReps}"
                    pb.bestReps != null -> "${pb.bestReps} reps"
                    pb.bestDurationSeconds != null -> "${pb.bestDurationSeconds}s"
                    else -> "—"
                }
                PbRow(ActivityKind.ROUTINE, pb.exerciseName, detail, df.format(Date(pb.achievedAt)))
            }
        }
        val sessionRows = sessions.take(8).map {
            SessionRow(it.activityKind, kindLabel(it.activityKind), df.format(Date(it.startTime)), "${it.durationMinutes} min")
        }
        StatsUiState(heat, pbRows, sessionRows, isLoading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    private fun trim(f: Float) = if (f % 1f == 0f) f.toInt().toString() else f.toString()

    private fun kindLabel(k: ActivityKind) = when (k) {
        ActivityKind.TREADMILL -> "Treadmill"
        ActivityKind.SWIM -> "Swimming"
        ActivityKind.BASKETBALL -> "Basketball"
        ActivityKind.ROUTINE -> "Routine"
    }
}
