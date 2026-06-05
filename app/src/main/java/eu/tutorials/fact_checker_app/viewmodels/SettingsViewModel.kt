package eu.tutorials.fact_checker_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.fact_checker_app.data.repository.FactCheckerRepository
import eu.tutorials.fact_checker_app.data.repository.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore,
    private val repository: FactCheckerRepository
) : ViewModel() {

    val apiBaseUrl: StateFlow<String> = prefs.apiBaseUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesDataStore.DEFAULT_API_URL)

    val isDarkTheme: StateFlow<Boolean> = prefs.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val maxSources: StateFlow<Int> = prefs.maxSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    // FIX: Map the stats flow directly to the total integer before calling stateIn
    val totalChecks: StateFlow<Int> = repository.getStatsFlow()
        .map { it.total }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun setApiBaseUrl(url: String) {
        viewModelScope.launch { prefs.setApiBaseUrl(url.trimEnd('/') + "/") }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(isDark) }
    }

    fun setMaxSources(count: Int) {
        viewModelScope.launch { prefs.setMaxSources(count) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}