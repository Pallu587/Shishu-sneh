package com.example.shishu_sneh_healthcare.presentation.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.SleepLogEntity
import com.example.shishu_sneh_healthcare.domain.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel() {

    private val _sleepLogs = MutableStateFlow<List<SleepLogEntity>>(emptyList())
    val sleepLogs: StateFlow<List<SleepLogEntity>> = _sleepLogs.asStateFlow()

    fun loadSleepLogs(babyId: Long) {
        viewModelScope.launch {
            repository.getSleepLogs(babyId).collectLatest {
                _sleepLogs.value = it
            }
        }
    }

    fun addSleepLog(babyId: Long, startTime: Long, endTime: Long?, quality: String?, notes: String?) {
        viewModelScope.launch {
            val log = SleepLogEntity(
                babyId = babyId,
                startTime = startTime,
                endTime = endTime,
                quality = quality,
                notes = notes
            )
            repository.insertSleepLog(log)
        }
    }
}
