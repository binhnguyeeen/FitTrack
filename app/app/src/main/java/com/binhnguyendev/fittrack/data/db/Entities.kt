package com.binhnguyendev.fittrack.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // always 1, single user
    val name: String,
    val photoUri: String?,
    val onboardingComplete: Boolean = false,
    val use24h: Boolean = false,
)

@Entity(tableName = "workout_template")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val activityKind: ActivityKind,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "template_exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("templateId")],
)
data class TemplateExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val name: String,
    val mode: String, // e.g. "Sets · 4 × 8", "Duration · 60s"
    val sortOrder: Int = 0,
)

@Entity(tableName = "workout_session")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long?,
    val activityKind: ActivityKind,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val distanceMeters: Float?, // null for non-cardio
    val volumeKg: Float?, // null for non-strength
    val notes: String = "",
    val date: Long, // normalized date (midnight UTC) for calendar queries
)

@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Float?,
    val durationSeconds: Int?, // for duration-mode exercises
    val isPersonalBest: Boolean = false,
)

@Entity(tableName = "personal_best")
data class PersonalBest(
    @PrimaryKey val exerciseName: String,
    val bestReps: Int?,
    val bestWeightKg: Float?,
    val bestDurationSeconds: Int?,
    val achievedAt: Long,
)

@Entity(tableName = "planned_workout")
data class PlannedWorkout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // normalized date (midnight UTC)
    val templateId: Long?,
    val activityKind: ActivityKind,
    val label: String, // display label e.g. "Upper Body"
)

@Entity(tableName = "streak")
data class Streak(
    @PrimaryKey val id: Int = 1, // always 1
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastWorkoutDate: Long?, // normalized date (midnight UTC)
)
