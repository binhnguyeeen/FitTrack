package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.db.PersonalBest
import com.binhnguyendev.fittrack.data.db.PersonalBestDao
import com.binhnguyendev.fittrack.data.db.WorkoutSession
import com.binhnguyendev.fittrack.data.db.WorkoutSessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class StatsRepository(
    private val sessionDao: WorkoutSessionDao,
    private val pbDao: PersonalBestDao,
) {
    val allSessions: Flow<List<WorkoutSession>> = sessionDao.getAll()
    val personalBests: Flow<List<PersonalBest>> = pbDao.getAll()

    /**
     * 8 columns (weeks, oldest→newest) × 7 rows (Mon→Sun) intensity grid.
     * Derived from real session minutes; falls back to the prototype's seed
     * pattern (StatsScreen) while there is no history yet (spec STEP / CONSTRAINT 3).
     */
    val heatmap: Flow<List<List<Int>>> = sessionDao.getAll().map { sessions ->
        if (sessions.isEmpty()) STATIC_HEAT else buildHeat(sessions)
    }

    private fun buildHeat(sessions: List<WorkoutSession>): List<List<Int>> {
        val minutesByDate = HashMap<Long, Int>()
        for (s in sessions) {
            val d = Instant.ofEpochMilli(s.date).truncatedTo(ChronoUnit.DAYS).toEpochMilli()
            minutesByDate[d] = (minutesByDate[d] ?: 0) + s.durationMinutes
        }
        val today = Instant.now().atZone(ZoneOffset.UTC).toLocalDate()
        val thisMonday = today.minusDays(((today.dayOfWeek.value + 6) % 7).toLong())
        return (0 until 8).map { col ->
            val weekStart = thisMonday.minusWeeks((7 - col).toLong())
            (0 until 7).map { row ->
                val date = weekStart.plusDays(row.toLong())
                val key = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                level(minutesByDate[key] ?: 0)
            }
        }
    }

    private fun level(minutes: Int): Int = when {
        minutes <= 0 -> 0
        minutes < 20 -> 1
        minutes < 40 -> 2
        minutes < 60 -> 3
        else -> 4
    }

    companion object {
        /** Prototype seed pattern (design/project/fittrack-screens-3.jsx). */
        val STATIC_HEAT: List<List<Int>> = listOf(
            listOf(2, 0, 3, 0, 2, 1, 0),
            listOf(3, 0, 2, 0, 3, 0, 0),
            listOf(0, 2, 0, 3, 0, 4, 1),
            listOf(3, 0, 2, 2, 0, 3, 0),
            listOf(2, 0, 3, 0, 2, 0, 0),
            listOf(4, 0, 2, 0, 3, 2, 0),
            listOf(0, 3, 0, 2, 0, 4, 0),
            listOf(3, 2, 0, 3, 1, 0, 0),
        )
    }
}
