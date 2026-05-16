package com.binhnguyendev.fittrack.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserProfile::class,
        WorkoutTemplate::class,
        TemplateExercise::class,
        WorkoutSession::class,
        WorkoutSet::class,
        PersonalBest::class,
        PlannedWorkout::class,
        Streak::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun personalBestDao(): PersonalBestDao
    abstract fun plannedWorkoutDao(): PlannedWorkoutDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun get(context: Context): FitTrackDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack.db",
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
