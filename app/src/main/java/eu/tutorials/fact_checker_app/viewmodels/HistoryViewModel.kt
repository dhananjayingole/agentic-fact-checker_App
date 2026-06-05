package eu.tutorials.fact_checker_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.fact_checker_app.data.repository.FactCheckerRepository
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.Verdict
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryFilter { ALL, TRUE, FALSE, INCONCLUSIVE, FAVORITES }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FactCheckerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow(HistoryFilter.ALL)
    val activeFilter: StateFlow<HistoryFilter> = _activeFilter.asStateFlow()

    val results: StateFlow<List<ClaimResult>> = combine(
        _searchQuery,
        _activeFilter
    ) { query, filter -> Pair(query, filter) }
        .flatMapLatest { (query, filter) ->
            when {
                query.isNotBlank() -> repository.searchVerificationsFlow(query)
                filter == HistoryFilter.FAVORITES -> repository.getFavoritesFlow()
                filter == HistoryFilter.TRUE -> repository.getByVerdictFlow(Verdict.TRUE)
                filter == HistoryFilter.FALSE -> repository.getByVerdictFlow(Verdict.FALSE)
                filter == HistoryFilter.INCONCLUSIVE -> repository.getByVerdictFlow(Verdict.INCONCLUSIVE)
                else -> repository.getAllVerificationsFlow()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchChanged(query: String) { _searchQuery.value = query }
    fun onFilterChanged(filter: HistoryFilter) { _activeFilter.value = filter }
    fun clearSearch() { _searchQuery.value = "" }

    fun deleteResult(result: ClaimResult) {
        viewModelScope.launch { repository.deleteVerification(result) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}