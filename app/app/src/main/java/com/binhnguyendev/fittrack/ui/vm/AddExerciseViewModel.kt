package com.binhnguyendev.fittrack.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.binhnguyendev.fittrack.data.db.ActivityKind

data class PresetExercise(val name: String, val kind: ActivityKind, val mode: String)

class AddExerciseViewModel : ViewModel() {
    var query by mutableStateOf("")

    // Preset library — exact list from the prototype AddExerciseScreen.
    private val presets = listOf(
        PresetExercise("Pull-ups", ActivityKind.ROUTINE, "Sets · reps"),
        PresetExercise("Push-ups", ActivityKind.ROUTINE, "Sets · reps"),
        PresetExercise("Dumbbell rows", ActivityKind.ROUTINE, "Sets · reps"),
        PresetExercise("Plank", ActivityKind.ROUTINE, "Duration · 60s"),
        PresetExercise("Jumping jacks", ActivityKind.ROUTINE, "Duration"),
        PresetExercise("Burpees", ActivityKind.ROUTINE, "Reps"),
        PresetExercise("Treadmill intervals", ActivityKind.TREADMILL, "Cardio metrics"),
        PresetExercise("Freestyle swim sets", ActivityKind.SWIM, "Sets · duration"),
        PresetExercise("Free throws", ActivityKind.BASKETBALL, "Reps"),
        PresetExercise("Mountain climbers", ActivityKind.ROUTINE, "Duration"),
    )

    val total: Int get() = presets.size

    val filtered: List<PresetExercise>
        get() = presets.filter { it.name.contains(query, ignoreCase = true) }
}
