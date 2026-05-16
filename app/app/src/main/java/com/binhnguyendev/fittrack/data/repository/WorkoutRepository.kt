package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.DateUtils
import com.binhnguyendev.fittrack.data.db.PersonalBest
import com.binhnguyendev.fittrack.data.db.PersonalBestDao
import com.binhnguyendev.fittrack.data.db.Streak
import com.binhnguyendev.fittrack.data.db.StreakDao
import com.binhnguyendev.fittrack.data.db.WorkoutSession
import com.binhnguyendev.fittrack.data.db.WorkoutSessionDao
import com.binhnguyendev.fittrack.data.db.WorkoutSet
import com.binhnguyendev.fittrack.data.db.WorkoutSetDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WorkoutRepository(
    private val sessionDao: WorkoutSessionDao,
    private val setDao: WorkoutSetDao,
    private val pbDao: PersonalBestDao,
    private val streakDao: StreakDao,
) {
    val recentSessions: Flow<List<WorkoutSession>> = sessionDao.getRecent(3)
    val allSessions: Flow<List<WorkoutSession>> = sessionDao.getAll()
    val streak: Flow<Streak?> = streakDao.get()
    val personalBests: Flow<List<PersonalBest>> = pbDao.getAll()

    suspend fun getSession(id: Long): WorkoutSession? =
        withContext(Dispatchers.IO) { sessionDao.getById(id) }

    suspend fun getSets(sessionId: Long): List<WorkoutSet> =
        withContext(Dispatchers.IO) { setDao.getBySessionId(sessionId) }

    suspend fun updateNotes(sessionId: Long, notes: String) =
        withContext(Dispatchers.IO) { sessionDao.updateNotes(sessionId, notes) }

    /**
     * Persists a finished workout: inserts the session + its sets, runs PB
     * detection (flagging beating sets and upserting [PersonalBest] rows), then
     * updates the streak. Returns the new session id.
     */
    suspend fun saveCompletedWorkout(
        session: WorkoutSession,
        sets: List<WorkoutSet>,
    ): Long = withContext(Dispatchers.IO) {
        val sessionId = sessionDao.insert(session)
        val now = System.currentTimeMillis()

        val flagged = sets.map { set ->
            val isPb = detectAndStorePb(set, now)
            set.copy(sessionId = sessionId, isPersonalBest = isPb)
        }
        if (flagged.isNotEmpty()) setDao.insertAll(flagged)

        recordWorkoutComplete(session.date)
        sessionId
    }

    private suspend fun detectAndStorePb(set: WorkoutSet, achievedAt: Long): Boolean {
        val existing = pbDao.get(set.exerciseName)
        val reps = set.reps ?: 0
        val weight = set.weightKg ?: 0f
        val dur = set.durationSeconds ?: 0

        val beatsReps = reps > 0 && reps > (existing?.bestReps ?: 0)
        val beatsWeight = weight > 0f && weight > (existing?.bestWeightKg ?: 0f)
        val beatsDur = dur > 0 && dur > (existing?.bestDurationSeconds ?: 0)

        if (!beatsReps && !beatsWeight && !beatsDur) return false

        pbDao.upsert(
            PersonalBest(
                exerciseName = set.exerciseName,
                bestReps = maxOf(existing?.bestReps ?: 0, reps).takeIf { it > 0 },
                bestWeightKg = maxOf(existing?.bestWeightKg ?: 0f, weight).takeIf { it > 0f },
                bestDurationSeconds = maxOf(existing?.bestDurationSeconds ?: 0, dur).takeIf { it > 0 },
                achievedAt = achievedAt,
            ),
        )
        return true
    }

    /** Streak rules per spec STEP 4. [workoutDateUtc] is a normalized date. */
    suspend fun recordWorkoutComplete(workoutDateUtc: Long) =
        withContext(Dispatchers.IO) {
            val current = streakDao.getOnce() ?: Streak(1, 0, 0, null)
            val last = current.lastWorkoutDate

            val newCurrent = when {
                last == null -> 1
                DateUtils.daysBetween(last, workoutDateUtc) == 0L -> current.currentStreak
                DateUtils.daysBetween(last, workoutDateUtc) == 1L -> current.currentStreak + 1
                else -> 1
            }.coerceAtLeast(1)

            streakDao.upsert(
                Streak(
                    id = 1,
                    currentStreak = newCurrent,
                    longestStreak = maxOf(current.longestStreak, newCurrent),
                    lastWorkoutDate = workoutDateUtc,
                ),
            )
        }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        sessionDao.clearAll() // cascades to workout_set
        pbDao.clearAll()
        streakDao.upsert(Streak(1, 0, 0, null))
    }
}
