package com.binhnguyendev.fittrack.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Upsert
    suspend fun upsert(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun get(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getOnce(): UserProfile?
}

@Dao
interface WorkoutTemplateDao {
    @Insert
    suspend fun insert(template: WorkoutTemplate): Long

    @Delete
    suspend fun delete(template: WorkoutTemplate)

    @Query("DELETE FROM workout_template WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM workout_template ORDER BY createdAt DESC")
    fun getAll(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_template WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkoutTemplate?
}

@Dao
interface TemplateExerciseDao {
    @Insert
    suspend fun insert(exercise: TemplateExercise): Long

    @Insert
    suspend fun insertAll(exercises: List<TemplateExercise>)

    @Query("DELETE FROM template_exercise WHERE templateId = :templateId")
    suspend fun deleteByTemplateId(templateId: Long)

    @Query("SELECT * FROM template_exercise WHERE templateId = :templateId ORDER BY sortOrder ASC")
    fun getByTemplateId(templateId: Long): Flow<List<TemplateExercise>>

    @Query("SELECT * FROM template_exercise WHERE templateId = :templateId ORDER BY sortOrder ASC")
    suspend fun getByTemplateIdOnce(templateId: Long): List<TemplateExercise>

    @Query("SELECT COUNT(*) FROM template_exercise WHERE templateId = :templateId")
    fun countForTemplate(templateId: Long): Flow<Int>
}

@Dao
interface WorkoutSessionDao {
    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Query("SELECT * FROM workout_session ORDER BY startTime DESC")
    fun getAll(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_session WHERE date = :date ORDER BY startTime DESC")
    fun getByDate(date: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_session ORDER BY startTime DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_session WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkoutSession?

    @Query("UPDATE workout_session SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("DELETE FROM workout_session")
    suspend fun clearAll()
}

@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(set: WorkoutSet): Long

    @Insert
    suspend fun insertAll(sets: List<WorkoutSet>)

    @Query("SELECT * FROM workout_set WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getBySessionId(sessionId: Long): List<WorkoutSet>
}

@Dao
interface PersonalBestDao {
    @Upsert
    suspend fun upsert(pb: PersonalBest)

    @Query("SELECT * FROM personal_best WHERE exerciseName = :exerciseName LIMIT 1")
    suspend fun get(exerciseName: String): PersonalBest?

    @Query("SELECT * FROM personal_best ORDER BY achievedAt DESC")
    fun getAll(): Flow<List<PersonalBest>>

    @Query("DELETE FROM personal_best")
    suspend fun clearAll()
}

@Dao
interface PlannedWorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(planned: PlannedWorkout): Long

    @Delete
    suspend fun delete(planned: PlannedWorkout)

    @Query("DELETE FROM planned_workout WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM planned_workout WHERE date = :date ORDER BY id ASC")
    fun getByDate(date: Long): Flow<List<PlannedWorkout>>

    @Query("SELECT * FROM planned_workout WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getByDateRange(start: Long, end: Long): Flow<List<PlannedWorkout>>

    @Query("DELETE FROM planned_workout")
    suspend fun clearAll()
}

@Dao
interface StreakDao {
    @Upsert
    suspend fun upsert(streak: Streak)

    @Query("SELECT * FROM streak WHERE id = 1 LIMIT 1")
    fun get(): Flow<Streak?>

    @Query("SELECT * FROM streak WHERE id = 1 LIMIT 1")
    suspend fun getOnce(): Streak?
}
