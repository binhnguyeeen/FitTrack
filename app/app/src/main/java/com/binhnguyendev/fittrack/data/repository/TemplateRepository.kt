package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.db.TemplateExercise
import com.binhnguyendev.fittrack.data.db.TemplateExerciseDao
import com.binhnguyendev.fittrack.data.db.TemplateListItem
import com.binhnguyendev.fittrack.data.db.WorkoutTemplate
import com.binhnguyendev.fittrack.data.db.WorkoutTemplateDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class EditableExercise(val name: String, val mode: String)

class TemplateRepository(
    private val templateDao: WorkoutTemplateDao,
    private val exerciseDao: TemplateExerciseDao,
) {
    val allTemplates: Flow<List<WorkoutTemplate>> = templateDao.getAll()
    val templatesWithCount: Flow<List<TemplateListItem>> = templateDao.getAllWithCount()

    fun exerciseCount(templateId: Long): Flow<Int> = exerciseDao.countForTemplate(templateId)

    fun exercises(templateId: Long): Flow<List<TemplateExercise>> =
        exerciseDao.getByTemplateId(templateId)

    suspend fun getTemplate(id: Long): WorkoutTemplate? =
        withContext(Dispatchers.IO) { templateDao.getById(id) }

    suspend fun exercisesOnce(templateId: Long): List<TemplateExercise> =
        withContext(Dispatchers.IO) { exerciseDao.getByTemplateIdOnce(templateId) }

    suspend fun saveTemplate(
        name: String,
        kind: ActivityKind,
        exercises: List<EditableExercise>,
    ): Long = withContext(Dispatchers.IO) {
        val id = templateDao.insert(
            WorkoutTemplate(name = name.ifBlank { "Untitled" }, activityKind = kind),
        )
        if (exercises.isNotEmpty()) {
            exerciseDao.insertAll(
                exercises.mapIndexed { i, e ->
                    TemplateExercise(
                        templateId = id,
                        name = e.name,
                        mode = e.mode,
                        sortOrder = i,
                    )
                },
            )
        }
        id
    }

    suspend fun deleteTemplate(id: Long) =
        withContext(Dispatchers.IO) { templateDao.deleteById(id) }
}
