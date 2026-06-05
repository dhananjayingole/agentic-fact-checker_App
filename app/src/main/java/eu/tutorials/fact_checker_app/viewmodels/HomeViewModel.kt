package eu.tutorials.fact_checker_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.fact_checker_app.data.repository.FactCheckerRepository
import eu.tutorials.fact_checker_app.domain.CheckStats
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.VerifyState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FactCheckerRepository
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────────
    private val _verifyState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val verifyState: StateFlow<VerifyState> = _verifyState.asStateFlow()

    private val _claimText = MutableStateFlow("")
    val claimText: StateFlow<String> = _claimText.asStateFlow()

    private val _maxSources = MutableStateFlow(5)

    // ── Recent checks (last 5) ────────────────────────────────
    val recentChecks: StateFlow<List<ClaimResult>> = repository
        .getAllVerificationsFlow()
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Stats ─────────────────────────────────────────────────
    val stats: StateFlow<CheckStats> = repository
        .getStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckStats())

    // ── Actions ───────────────────────────────────────────────

    fun onClaimChanged(text: String) {
        _claimText.value = text
        if (_verifyState.value is VerifyState.Error) {
            _verifyState.value = VerifyState.Idle
        }
    }

    fun verifyClaim() {
        val claim = _claimText.value.trim()
        if (claim.isBlank()) return
        if (claim.length < 5) {
            _verifyState.value = VerifyState.Error("Claim too short. Enter at least 5 characters.")
            return
        }

        viewModelScope.launch {
            _verifyState.value = VerifyState.Loading
            val result = repository.verifyClaim(claim, _maxSources.value)
            _verifyState.value = when (result) {
                is eu.tutorials.fact_checker_app.data.repository.Result.Success ->
                    VerifyState.Success(result.data)
                is eu.tutorials.fact_checker_app.data.repository.Result.Error ->
                    VerifyState.Error(result.message)
            }
        }
    }

    fun clearInput() {
        _claimText.value = ""
        _verifyState.value = VerifyState.Idle
    }

    fun resetState() {
        _verifyState.value = VerifyState.Idle
    }
}