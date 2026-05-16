package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.DateUtils
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

data class DayCell(
    val date: LocalDate,
    val letter: String,
    val number: Int,
    val kind: ActivityKind?,
    val isToday: Boolean,
)

data class DayEntry(
    val kind: ActivityKind,
    val label: String,
    val done: Boolean,
)

data class CalendarUiState(
    val monthLabel: String = "",
    val year: String = "",
    val week: List<DayCell> = emptyList(),
    val selected: LocalDate = LocalDate.now(),
    val entries: List<DayEntry> = emptyList(),
)

class CalendarViewModel(private val repos: Repositories) : ViewModel() {

    private val today = LocalDate.now()
    // Monday of the current week (dayOfWeek: Mon=1 … Sun=7).
    private val monday = today.minusDays(((today.dayOfWeek.value + 6) % 7).toLong())
    private val weekDates = (0..6).map { monday.plusDays(it.toLong()) }

    private val selectedDate = MutableStateFlow(today)

    val uiState: StateFlow<CalendarUiState> = combine(
        repos.calendar.plannedForRange(
            DateUtils.utcMillis(weekDates.first()),
            DateUtils.utcMillis(weekDates.last()),
        ),
        repos.workout.allSessions,
        selectedDate,
    ) { planned, sessions, selected ->
        val plannedByDate = planned.groupBy { DateUtils.toLocalDate(it.date) }
        val sessionsByDate = sessions.groupBy { DateUtils.startOfDayUtc(it.startTime) }

        val week = weekDates.map { d ->
            val key = DateUtils.startOfDayUtc(DateUtils.utcMillis(d))
            val kind = sessionsByDate[key]?.firstOrNull()?.activityKind
                ?: plannedByDate[d]?.firstOrNull()?.activityKind
            DayCell(
                date = d,
                letter = d.dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT, Locale.ENGLISH,
                ).take(3),
                number = d.dayOfMonth,
                kind = kind,
                isToday = d == today,
            )
        }

        val selKey = DateUtils.startOfDayUtc(DateUtils.utcMillis(selected))
        val doneEntries = (sessionsByDate[selKey] ?: emptyList()).map {
            DayEntry(it.activityKind, kindLabel(it.activityKind), done = true)
        }
        val plannedEntries = (plannedByDate[selected] ?: emptyList()).map {
            DayEntry(it.activityKind, it.label, done = false)
        }

        CalendarUiState(
            monthLabel = today.month.getDisplayName(
                java.time.format.TextStyle.FULL, Locale.ENGLISH,
            ),
            year = today.year.toString(),
            week = week,
            selected = selected,
            entries = doneEntries + plannedEntries,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun select(date: LocalDate) {
        selectedDate.value = date
    }

    fun addPlanned(kind: ActivityKind, label: String) {
        viewModelScope.launch {
            repos.calendar.addPlanned(
                DateUtils.utcMillis(selectedDate.value),
                kind,
                label.ifBlank { kindLabel(kind) },
                null,
            )
        }
    }

    private fun kindLabel(k: ActivityKind) = when (k) {
        ActivityKind.TREADMILL -> "Treadmill"
        ActivityKind.SWIM -> "Swimming"
        ActivityKind.BASKETBALL -> "Basketball"
        ActivityKind.ROUTINE -> "Routine"
    }
}
