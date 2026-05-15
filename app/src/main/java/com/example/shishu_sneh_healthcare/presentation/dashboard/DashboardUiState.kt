package com.example.shishu_sneh_healthcare.presentation.dashboard

import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.local.entity.FeedingLogEntity
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import com.example.shishu_sneh_healthcare.domain.use_case.Insight

data class DashboardUiState(
    val isLoading: Boolean = false,
    val babies: List<BabyEntity> = emptyList(),
    val selectedBaby: BabyEntity? = null,
    val lastFeeding: FeedingLogEntity? = null,
    val upcomingVaccine: VaccineEntity? = null,
    val growthStatus: String = "Healthy",
    val insights: List<Insight> = emptyList(),
    val error: String? = null
)
