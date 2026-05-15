package com.example.shishu_sneh_healthcare.presentation.diaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.DiaperLogEntity
import com.example.shishu_sneh_healthcare.domain.repository.DiaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaperViewModel @Inject constructor(
    private val repository: DiaperRepository
) : ViewModel() {

    private val _diaperLogs = MutableStateFlow<List<DiaperLogEntity>>(emptyList())
    val diaperLogs: StateFlow<List<DiaperLogEntity>> = _diaperLogs.asStateFlow()

    fun loadDiaperLogs(babyId: Long) {
        viewModelScope.launch {
            repository.getDiaperLogs(babyId).collectLatest {
                _diaperLogs.value = it
            }
        }
    }

    fun addDiaperLog(babyId: Long, type: String, texture: String?, color: String?, notes: String?) {
        viewModelScope.launch {
            val log = DiaperLogEntity(
                babyId = babyId,
                time = System.currentTimeMillis(),
                type = type,
                texture = texture,
                color = color,
                notes = notes
            )
            repository.insertDiaperLog(log)
        }
    }
}
