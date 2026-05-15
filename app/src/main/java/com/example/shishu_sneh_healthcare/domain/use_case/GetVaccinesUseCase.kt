package com.example.shishu_sneh_healthcare.domain.use_case

import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the vaccination schedule for a specific infant.
 * 
 * **Social Impact**: Automated scheduling ensures that rural mothers don't miss 
 * critical immunization milestones, which is a major cause of preventable infant 
 * mortality in underserved areas.
 *
 * @property repository The [VaccineRepository] used to fetch vaccination data.
 */
class GetVaccinesUseCase @Inject constructor(
    private val repository: VaccineRepository
) {
    operator fun invoke(babyId: Long): Flow<List<VaccineEntity>> {
        return repository.getVaccinesForBaby(babyId)
    }
}
