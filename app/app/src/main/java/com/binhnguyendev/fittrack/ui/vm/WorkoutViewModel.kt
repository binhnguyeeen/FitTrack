package com.binhnguyendev.fittrack.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.DateUtils
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.db.WorkoutSession
import com.binhnguyendev.fittrack.data.db.WorkoutSet
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CardioCfg(
    val unitMeters: Boolean, // true = meters (swim), false = km (treadmill/basket)
    val unitLabel: String,
    val kcalPerUnit: Float,
    val presets: List<String>,
    val paceUnit: String,
    val paceDivisor: Float,
    val decimal: Boolean,
)

class SetRow(
    val number: Int,
    reps: String = "",
    weight: String = "",
    checked: Boolean = false,
    isPb: Boolean = false,
) {
    var reps by mutableStateOf(reps)
    var weight by mutableStateOf(weight)
    var checked by mutableStateOf(checked)
    var isPb by mutableStateOf(isPb)
}

data class PbInfo(val exercise: String, val value: String, val delta: String?)

class WorkoutViewModel(
    private val repos: Repositories,
    kindKey: String,
    private val templateId: Long,
) : ViewModel() {

    val kind: ActivityKind = ActivityKind.fromKey(kindKey)
    val isCardio: Boolean = kind != ActivityKind.ROUTINE

    val cfg: CardioCfg? = when (kind) {
        ActivityKind.TREADMILL -> CardioCfg(false, "km", 62f, listOf("1.0", "2.5", "5.0", "10.0"), "/km", 1f, true)
        ActivityKind.SWIM -> CardioCfg(true, "m", 0.28f, listOf("500", "1000", "1500", "1800", "2000"), "/100m", 100f, false)
        ActivityKind.BASKETBALL -> CardioCfg(false, "km", 80f, listOf("1.0", "2.0", "3.0", "5.0"), "/km", 1f, true)
        ActivityKind.ROUTINE -> null
    }

    private val defaultRoutine = listOf(
        "Pull-ups", "Push-ups", "Dumbbell rows", "Plank", "Jumping jacks", "Burpees",
    )

    val exercises = mutableStateListOf<String>().apply {
        if (isCardio) add(kindLabel()) else addAll(defaultRoutine)
    }

    var currentIndex by mutableStateOf(0)
        private set

    private val setsByExercise = mutableStateMapOf<Int, SnapshotStateList<SetRow>>()

    fun setsFor(index: Int): SnapshotStateList<SetRow> =
        setsByExercise.getOrPut(index) { mutableStateListOf(SetRow(1)) }

    // Timer ------------------------------------------------------------------
    var elapsed by mutableStateOf(0)
        private set
    private var running by mutableStateOf(true)
    private var timerJob: Job? = null

    // Cardio -----------------------------------------------------------------
    var cardioDone by mutableStateOf(false)
        private set
    var distance by mutableStateOf("")

    // Rest -------------------------------------------------------------------
    var restActive by mutableStateOf(false)
        private set
    var restRemaining by mutableStateOf(60)
        private set
    val restTotal = 60
    private var restJob: Job? = null

    var pbBanner by mutableStateOf<PbInfo?>(null)
        private set

    private var finished = false

    init {
        if (templateId > 0 && !isCardio) {
            viewModelScope.launch {
                val ex = repos.template.exercisesOnce(templateId)
                if (ex.isNotEmpty()) {
                    exercises.clear()
                    exercises.addAll(ex.map { it.name })
                }
            }
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (running) elapsed += 1
            }
        }
    }

    fun addSet() {
        val list = setsFor(currentIndex)
        list.add(SetRow(list.size + 1))
    }

    fun checkSet(row: SetRow) {
        if (row.checked) {
            row.checked = false
            return
        }
        row.checked = true
        viewModelScope.launch { detectPb(exercises[currentIndex], row) }
        startRest()
    }

    private suspend fun detectPb(exerciseName: String, row: SetRow) {
        val reps = row.reps.toIntOrNull() ?: 0
        val weight = row.weight.toFloatOrNull() ?: 0f
        val best = repos.workout.bestFor(exerciseName)
        val beatsReps = reps > 0 && reps > (best?.bestReps ?: 0)
        val beatsWeight = weight > 0f && weight > (best?.bestWeightKg ?: 0f)
        if (beatsReps || beatsWeight) {
            row.isPb = true
            val value = when {
                weight > 0f && reps > 0 -> "$exerciseName · ${trim(weight)}kg × $reps"
                reps > 0 -> "$exerciseName · $reps reps"
                else -> exerciseName
            }
            val delta = best?.bestReps?.let { if (beatsReps) "+${reps - it} reps" else null }
            pbBanner = PbInfo(exerciseName, value, delta)
        }
    }

    private fun startRest() {
        restJob?.cancel()
        restActive = true
        restRemaining = restTotal
        restJob = viewModelScope.launch {
            while (restRemaining > 0) {
                delay(1000)
                restRemaining -= 1
            }
            restActive = false
        }
    }

    fun skipRest() {
        restJob?.cancel()
        restActive = false
    }

    fun addRest30() {
        restRemaining += 30
    }

    fun stopCardioTimer() {
        cardioDone = true
        running = false
    }

    fun updateDistance(v: String) {
        val cleaned = if (cfg?.decimal == true) {
            v.filter { it.isDigit() || it == '.' }
        } else {
            v.filter { it.isDigit() }
        }
        distance = cleaned
    }

    val isLastExercise: Boolean get() = currentIndex >= exercises.size - 1

    fun next() {
        if (!isLastExercise) {
            currentIndex += 1
            setsFor(currentIndex)
            pbBanner = null
            skipRest()
        }
    }

    // Derived display --------------------------------------------------------
    fun paceString(): String {
        val c = cfg ?: return "—"
        val d = distance.toFloatOrNull() ?: 0f
        if (d <= 0f) return "—"
        val paceSec = elapsed / (d / c.paceDivisor)
        if (paceSec <= 0f) return "—"
        val m = (paceSec / 60).toInt()
        val s = (paceSec % 60).toInt()
        return "%d:%02d".format(m, s)
    }

    fun calories(): Int {
        val c = cfg
        return if (c != null) {
            val d = distance.toFloatOrNull() ?: 0f
            (d * c.kcalPerUnit).roundToInt()
        } else {
            (elapsed / 60).coerceAtLeast(1) * 6
        }
    }

    private fun volumeKg(): Float {
        var total = 0f
        setsByExercise.values.forEach { list ->
            list.forEach { r ->
                if (r.checked) {
                    total += (r.reps.toIntOrNull() ?: 0) * (r.weight.toFloatOrNull() ?: 0f)
                }
            }
        }
        return total
    }

    fun finish(onSaved: (Long) -> Unit) {
        if (finished) return
        finished = true
        running = false
        timerJob?.cancel()
        restJob?.cancel()

        val now = System.currentTimeMillis()
        val durationMin = (elapsed / 60).coerceAtLeast(1)
        val distMeters: Float? = cfg?.let {
            val d = distance.toFloatOrNull() ?: 0f
            if (d <= 0f) null else if (it.unitMeters) d else d * 1000f
        }
        val vol = if (isCardio) null else volumeKg().takeIf { it > 0f }

        val sets = if (isCardio) emptyList() else buildList {
            setsByExercise.forEach { (idx, list) ->
                val name = exercises.getOrElse(idx) { kindLabel() }
                list.filter { it.checked }.forEach { r ->
                    add(
                        WorkoutSet(
                            sessionId = 0,
                            exerciseName = name,
                            setNumber = r.number,
                            reps = r.reps.toIntOrNull(),
                            weightKg = r.weight.toFloatOrNull(),
                            durationSeconds = null,
                            isPersonalBest = r.isPb,
                        ),
                    )
                }
            }
        }

        val session = WorkoutSession(
            templateId = templateId.takeIf { it > 0 },
            activityKind = kind,
            startTime = now - elapsed * 1000L,
            endTime = now,
            durationMinutes = durationMin,
            caloriesBurned = calories(),
            distanceMeters = distMeters,
            volumeKg = vol,
            notes = "",
            date = DateUtils.todayUtc(),
        )

        viewModelScope.launch {
            val id = repos.workout.saveCompletedWorkout(session, sets)
            onSaved(id)
        }
    }

    private fun kindLabel() = when (kind) {
        ActivityKind.TREADMILL -> "Treadmill"
        ActivityKind.SWIM -> "Swimming"
        ActivityKind.BASKETBALL -> "Basketball"
        ActivityKind.ROUTINE -> "Routine"
    }

    private fun trim(f: Float): String =
        if (f % 1f == 0f) f.toInt().toString() else f.toString()

    override fun onCleared() {
        timerJob?.cancel()
        restJob?.cancel()
    }
}
