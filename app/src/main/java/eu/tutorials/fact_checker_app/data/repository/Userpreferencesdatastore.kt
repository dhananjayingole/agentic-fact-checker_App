package eu.tutorials.fact_checker_app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fact_checker_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val MAX_SOURCES = intPreferencesKey("max_sources")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val DEFAULT_API_URL = "https://agentic-fact-checker.onrender.com/"
    }

    // ── Reads ─────────────────────────────────────────────────

    val apiBaseUrl: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[API_BASE_URL] ?: DEFAULT_API_URL }

    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[IS_DARK_THEME] ?: false }

    val maxSources: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[MAX_SOURCES] ?: 5 }

    val hasSeenOnboarding: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[HAS_SEEN_ONBOARDING] ?: false }

    // ── Writes ────────────────────────────────────────────────

    suspend fun setApiBaseUrl(url: String) {
        dataStore.edit { it[API_BASE_URL] = url }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { it[IS_DARK_THEME] = isDark }
    }

    suspend fun setMaxSources(count: Int) {
        dataStore.edit { it[MAX_SOURCES] = count }
    }

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        dataStore.edit { it[HAS_SEEN_ONBOARDING] = seen }
    }
}