package com.binhnguyendev.fittrack.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binhnguyendev.fittrack.data.repository.Repositories
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repos: Repositories) : ViewModel() {
    fun finish(name: String, photoPath: String?, onSaved: () -> Unit) {
        viewModelScope.launch {
            repos.user.completeOnboarding(name.trim(), photoPath)
            onSaved()
        }
    }
}
