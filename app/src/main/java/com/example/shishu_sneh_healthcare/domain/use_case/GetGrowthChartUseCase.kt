package com.example.shishu_sneh_healthcare.domain.use_case

import com.example.shishu_sneh_healthcare.data.local.entity.GrowthEntryEntity
import com.example.shishu_sneh_healthcare.domain.repository.GrowthRepository
import com.example.shishu_sneh_healthcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Use case to generate growth analytics for an infant.
 * 
 * **Scientific Impact**: Tracking height, weight, and head circumference helps identify 
 * early signs of stunting or malnutrition. This tool provides rural parents with 
 * professional-grade monitoring without requiring constant hospital visits.
 *
 * @property repository The [GrowthRepository] used to fetch growth data entries.
 */
class GetGrowthChartUseCase @Inject constructor(
    private val repository: GrowthRepository
) {
    operator fun invoke(babyId: Long): Flow<Resource<List<GrowthEntryEntity>>> {
        return repository.getGrowthEntries(babyId).map<List<GrowthEntryEntity>, Resource<List<GrowthEntryEntity>>> { entries ->
            Resource.Success(entries)
        }.onStart {
            emit(Resource.Loading())
        }.catch { e ->
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}
