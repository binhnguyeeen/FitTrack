package com.binhnguyendev.fittrack.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.launch

class EditProfileViewModel(private val repos: Repositories) : ViewModel() {
    var name by mutableStateOf("")
    var photo by mutableStateOf<String?>(null)
    var loaded by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repos.user.getProfileOnce()?.let {
                name = it.name
                photo = it.photoUri
            }
            loaded = true
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            repos.user.updateProfile(name, photo)
            onSaved()
        }
    }
}
