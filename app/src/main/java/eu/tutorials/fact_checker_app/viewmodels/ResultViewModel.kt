package eu.tutorials.fact_checker_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.fact_checker_app.data.repository.FactCheckerRepository
import eu.tutorials.fact_checker_app.domain.ClaimResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ResultScreenState {
    object Loading : ResultScreenState()
    data class Success(val result: ClaimResult) : ResultScreenState()
    data class Error(val message: String) : ResultScreenState()
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: FactCheckerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ResultScreenState>(ResultScreenState.Loading)
    val state: StateFlow<ResultScreenState> = _state.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadResult(id: Long) {
        viewModelScope.launch {
            val result = repository.getById(id)
            _state.value = if (result != null) {
                ResultScreenState.Success(result)
            } else {
                ResultScreenState.Error("Result not found.")
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            val newValue = !_isFavorite.value
            _isFavorite.value = newValue
            repository.toggleFavorite(id, newValue)
        }
    }

    fun deleteResult(result: ClaimResult) {
        viewModelScope.launch {
            repository.deleteVerification(result)
        }
    }
}