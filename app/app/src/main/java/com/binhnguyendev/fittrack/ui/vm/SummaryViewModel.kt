package com.binhnguyendev.fittrack.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StatCell(val value: String, val label: String, val unit: String)

data class SummaryUiState(
    val loaded: Boolean = false,
    val kind: ActivityKind = ActivityKind.ROUTINE,
    val title: String = "",
    val dateLabel: String = "",
    val stats: List<StatCell> = emptyList(),
    val pb: String? = null,
    val notes: String = "",
)

class SummaryViewModel(
    private val repos: Repositories,
    private val sessionId: Long,
) : ViewModel() {

    var state by mutableStateOf(SummaryUiState())
        private set

    init {
        viewModelScope.launch {
            val session = repos.workout.getSession(sessionId) ?: return@launch
            val sets = repos.workout.getSets(sessionId)
            val exercisesDone = sets.map { it.exerciseName }.distinct().size
            val pbSet = sets.firstOrNull { it.isPersonalBest }
            val pbText = pbSet?.let {
                val v = when {
                    it.weightKg != null && it.reps != null ->
                        "${trim(it.weightKg)}kg × ${it.reps}"
                    it.reps != null -> "${it.reps} reps"
                    it.durationSeconds != null -> "${it.durationSeconds}s"
                    else -> ""
                }
                "${it.exerciseName} · $v"
            }
            val df = SimpleDateFormat("MMM d, h:mm a", Locale.ENGLISH)

            val stats = buildList {
                add(StatCell("${session.durationMinutes}", "Time", "min"))
                if (session.activityKind == ActivityKind.ROUTINE) {
                    add(StatCell("$exercisesDone", "Exercises", "done"))
                } else if (session.distanceMeters != null) {
                    val d = session.distanceMeters
                    val label = if (session.activityKind == ActivityKind.SWIM) {
                        "${d.toInt()}" to "m"
                    } else {
                        "%.1f".format(d / 1000f) to "km"
                    }
                    add(StatCell(label.first, "Distance", label.second))
                } else {
                    add(StatCell("$exercisesDone", "Exercises", "done"))
                }
                add(StatCell("${session.caloriesBurned}", "Calories", "kcal"))
                add(StatCell(formatVolume(session.volumeKg), "Volume", "kg"))
            }

            state = SummaryUiState(
                loaded = true,
                kind = session.activityKind,
                title = kindLabel(session.activityKind),
                dateLabel = df.format(Date(session.endTime)),
                stats = stats,
                pb = pbText,
                notes = session.notes,
            )
        }
    }

    fun onNotesChange(text: String) {
        state = state.copy(notes = text)
    }

    fun persistNotes() {
        viewModelScope.launch { repos.workout.updateNotes(sessionId, state.notes) }
    }

    private fun formatVolume(v: Float?): String {
        if (v == null || v <= 0f) return "—"
        return if (v >= 1000f) "%.1fk".format(v / 1000f) else v.toInt().toString()
    }

    private fun trim(f: Float): String =
        if (f % 1f == 0f) f.toInt().toString() else f.toString()

    private fun kindLabel(k: ActivityKind) = when (k) {
        ActivityKind.TREADMILL -> "Treadmill"
        ActivityKind.SWIM -> "Swimming"
        ActivityKind.BASKETBALL -> "Basketball"
        ActivityKind.ROUTINE -> "Routine"
    }
}
