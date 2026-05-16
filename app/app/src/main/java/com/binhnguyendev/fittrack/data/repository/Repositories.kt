package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.db.FitTrackDatabase

/** Manually-wired repository graph (no DI framework, per spec STEP 8). */
class Repositories(db: FitTrackDatabase) {
    val user = UserRepository(db.userProfileDao())
    val workout = WorkoutRepository(
        db.workoutSessionDao(),
        db.workoutSetDao(),
        db.personalBestDao(),
        db.streakDao(),
    )
    val template = TemplateRepository(
        db.workoutTemplateDao(),
        db.templateExerciseDao(),
    )
    val calendar = CalendarRepository(
        db.plannedWorkoutDao(),
        db.workoutSessionDao(),
    )
    val stats = StatsRepository(
        db.workoutSessionDao(),
        db.personalBestDao(),
    )
}
