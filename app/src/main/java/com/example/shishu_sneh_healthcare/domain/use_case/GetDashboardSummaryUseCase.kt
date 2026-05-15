package com.example.shishu_sneh_healthcare.domain.use_case

import com.example.shishu_sneh_healthcare.domain.repository.FeedingRepository
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import com.example.shishu_sneh_healthcare.domain.util.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardSummary(
    val lastFeeding: Long? = null,
    val nextVaccineDays: Int? = null,
    val isVaccineOverdue: Boolean = false
)

class GetDashboardSummaryUseCase @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val vaccineRepository: VaccineRepository
) {
    operator fun invoke(babyId: Long): Flow<Resource<DashboardSummary>> = flow {
        emit(Resource.Loading())
        
        val feedingFlow = feedingRepository.getFeedingLogs(babyId).map { it.firstOrNull()?.startTime }
        val vaccineFlow = vaccineRepository.getVaccinesForBaby(babyId).map { vaccines ->
            val upcoming = vaccines.filter { it.status == "Upcoming" }.minByOrNull { it.scheduledDate }
            val overdue = vaccines.any { it.status == "Overdue" }
            
            val days = upcoming?.let {
                ((it.scheduledDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
            }
            
            Pair(days, overdue)
        }

        combine(feedingFlow, vaccineFlow) { lastFeeding, vaccineInfo ->
            DashboardSummary(
                lastFeeding = lastFeeding,
                nextVaccineDays = vaccineInfo.first,
                isVaccineOverdue = vaccineInfo.second
            )
        }.collect { summary ->
            emit(Resource.Success(summary))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Failed to load dashboard summary"))
    }
}
