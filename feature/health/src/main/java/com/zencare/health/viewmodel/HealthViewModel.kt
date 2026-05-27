package com.zencare.health.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zencare.common.result.AppResult
import com.zencare.data.repository.HealthRepository
import com.zencare.model.dto.HealthRecord
import com.zencare.model.dto.HealthMetricType
import com.zencare.model.dto.HealthStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthUiState(
    val records: List<HealthRecord> = emptyList(),
    val stats: HealthStats? = null,
    val selectedMetric: HealthMetricType = HealthMetricType.BLOOD_SUGAR,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HealthUiState())
    val state: StateFlow<HealthUiState> = _state.asStateFlow()

    fun loadRecords(type: HealthMetricType? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getRecords(type = type)) {
                is AppResult.Success -> _state.update {
                    it.copy(isLoading = false, records = result.data.records)
                }
                is AppResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    fun loadStats(type: HealthMetricType) {
        viewModelScope.launch {
            _state.update { it.copy(selectedMetric = type) }
            when (val result = repository.getStats(type)) {
                is AppResult.Success -> _state.update { it.copy(stats = result.data) }
                else -> {}
            }
        }
    }

    fun selectMetric(type: HealthMetricType) {
        loadStats(type)
        loadRecords(type)
    }
}
