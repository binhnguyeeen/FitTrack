package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.db.PlannedWorkout
import com.binhnguyendev.fittrack.data.db.PlannedWorkoutDao
import com.binhnguyendev.fittrack.data.db.WorkoutSession
import com.binhnguyendev.fittrack.data.db.WorkoutSessionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CalendarRepository(
    private val plannedDao: PlannedWorkoutDao,
    private val sessionDao: WorkoutSessionDao,
) {
    fun plannedForDate(dateUtc: Long): Flow<List<PlannedWorkout>> =
        plannedDao.getByDate(dateUtc)

    fun plannedForRange(startUtc: Long, endUtc: Long): Flow<List<PlannedWorkout>> =
        plannedDao.getByDateRange(startUtc, endUtc)

    fun sessionsForDate(dateUtc: Long): Flow<List<WorkoutSession>> =
        sessionDao.getByDate(dateUtc)

    suspend fun addPlanned(
        dateUtc: Long,
        kind: ActivityKind,
        label: String,
        templateId: Long?,
    ) = withContext(Dispatchers.IO) {
        plannedDao.insert(
            PlannedWorkout(
                date = dateUtc,
                templateId = templateId,
                activityKind = kind,
                label = label,
            ),
        )
    }

    suspend fun removePlanned(id: Long) =
        withContext(Dispatchers.IO) { plannedDao.deleteById(id) }
}
