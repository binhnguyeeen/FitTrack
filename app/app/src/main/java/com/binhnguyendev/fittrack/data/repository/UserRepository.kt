package com.binhnguyendev.fittrack.data.repository

import com.binhnguyendev.fittrack.data.db.UserProfile
import com.binhnguyendev.fittrack.data.db.UserProfileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(private val dao: UserProfileDao) {

    val profile: Flow<UserProfile?> = dao.get()

    suspend fun getProfileOnce(): UserProfile? = withContext(Dispatchers.IO) { dao.getOnce() }

    suspend fun isOnboardingComplete(): Boolean =
        withContext(Dispatchers.IO) { dao.getOnce()?.onboardingComplete == true }

    suspend fun completeOnboarding(name: String, photoUri: String?) =
        withContext(Dispatchers.IO) {
            val current = dao.getOnce()
            dao.upsert(
                UserProfile(
                    id = 1,
                    name = name.ifBlank { "Nguyen" },
                    photoUri = photoUri,
                    onboardingComplete = true,
                    use24h = current?.use24h ?: false,
                ),
            )
        }

    suspend fun updateProfile(name: String, photoUri: String?) =
        withContext(Dispatchers.IO) {
            val current = dao.getOnce()
            dao.upsert(
                UserProfile(
                    id = 1,
                    name = name.ifBlank { "Nguyen" },
                    photoUri = photoUri,
                    onboardingComplete = current?.onboardingComplete ?: true,
                    use24h = current?.use24h ?: false,
                ),
            )
        }

    suspend fun setUse24h(use24h: Boolean) =
        withContext(Dispatchers.IO) {
            val current = dao.getOnce() ?: UserProfile(1, "Nguyen", null, false, false)
            dao.upsert(current.copy(use24h = use24h))
        }
}
