package com.binhnguyendev.fittrack.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fittrack_prefs")

/** App preferences not part of the Room schema (units / reminders /
 *  notifications). use24h lives on UserProfile instead. */
class PreferencesRepository(private val context: Context) {
    private val UNITS = stringPreferencesKey("units")
    private val REMINDERS = booleanPreferencesKey("reminders")
    private val NOTIFICATIONS = booleanPreferencesKey("notifications")

    val units: Flow<String> = context.dataStore.data.map { it[UNITS] ?: "metric" }
    val reminders: Flow<Boolean> = context.dataStore.data.map { it[REMINDERS] ?: true }
    val notifications: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS] ?: true }

    suspend fun setUnits(value: String) {
        context.dataStore.edit { it[UNITS] = value }
    }

    suspend fun setReminders(value: Boolean) {
        context.dataStore.edit { it[REMINDERS] = value }
    }

    suspend fun setNotifications(value: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS] = value }
    }
}
