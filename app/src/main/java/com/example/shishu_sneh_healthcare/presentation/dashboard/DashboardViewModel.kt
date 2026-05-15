package com.example.shishu_sneh_healthcare.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.preferences.PreferenceManager
import com.example.shishu_sneh_healthcare.domain.use_case.GetBabiesUseCase
import com.example.shishu_sneh_healthcare.domain.use_case.GetDashboardSummaryUseCase
import com.example.shishu_sneh_healthcare.domain.use_case.GetInsightsUseCase
import com.example.shishu_sneh_healthcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getBabiesUseCase: GetBabiesUseCase,
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val getInsightsUseCase: GetInsightsUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _appLanguage = MutableStateFlow("en")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    init {
        loadInitialData()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            preferenceManager.appLanguage.collect { 
                _appLanguage.value = it
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val userId = "dummy_user_id"
            
            getBabiesUseCase(userId).collectLatest { babies ->
                val lastBabyId = preferenceManager.lastSelectedBabyId.first()
                val selectedBaby = babies.find { it.id == lastBabyId } ?: babies.firstOrNull()
                
                _state.update { it.copy(babies = babies, selectedBaby = selectedBaby) }
                
                selectedBaby?.let { 
                    loadDashboardSummary(it.id)
                    loadInsights(it.dob)
                }
            }
        }
    }

    private fun loadInsights(dob: Long) {
        val ageInWeeks = ((System.currentTimeMillis() - dob) / (1000 * 60 * 60 * 24 * 7)).toInt()
        val insights = getInsightsUseCase(ageInWeeks)
        _state.update { it.copy(insights = insights) }
    }

    private fun loadDashboardSummary(babyId: Long) {
        viewModelScope.launch {
            getDashboardSummaryUseCase(babyId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, error = null) }
                        // Here we could update more specific summary fields in state if DashboardSummary had them
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun setCurrentTab(index: Int) {
        _currentTab.value = index
    }

    fun selectBaby(baby: BabyEntity) {
        viewModelScope.launch {
            preferenceManager.saveSelectedBabyId(baby.id)
            _state.update { it.copy(selectedBaby = baby) }
            loadDashboardSummary(baby.id)
        }
    }
}
