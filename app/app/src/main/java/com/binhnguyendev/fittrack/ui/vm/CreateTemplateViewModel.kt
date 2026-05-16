package com.binhnguyendev.fittrack.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.data.repository.EditableExercise
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.launch

/** Shared across the CreateTemplate ⇄ AddExercise flow (scoped to the
 *  templates/flow nav graph entry). */
class CreateTemplateViewModel(private val repos: Repositories) : ViewModel() {
    var name by mutableStateOf("")
    var kind by mutableStateOf(ActivityKind.ROUTINE)
    val exercises = mutableStateListOf<EditableExercise>()

    fun addExercise(name: String, mode: String) {
        exercises.add(EditableExercise(name, mode))
    }

    fun removeAt(index: Int) {
        if (index in exercises.indices) exercises.removeAt(index)
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            repos.template.saveTemplate(name, kind, exercises.toList())
            onSaved()
        }
    }
}
