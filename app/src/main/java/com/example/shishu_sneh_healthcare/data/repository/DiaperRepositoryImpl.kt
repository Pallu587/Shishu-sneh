package com.example.shishu_sneh_healthcare.data.repository

import com.example.shishu_sneh_healthcare.data.local.dao.DiaperDao
import com.example.shishu_sneh_healthcare.data.local.entity.DiaperLogEntity
import com.example.shishu_sneh_healthcare.domain.repository.DiaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiaperRepositoryImpl @Inject constructor(
    private val diaperDao: DiaperDao
) : DiaperRepository {
    override fun getDiaperLogs(babyId: Long): Flow<List<DiaperLogEntity>> =
        diaperDao.getDiaperLogs(babyId)

    override suspend fun insertDiaperLog(log: DiaperLogEntity): Long =
        diaperDao.insertDiaperLog(log)

    override fun getDiaperLogsSince(babyId: Long, since: Long): Flow<List<DiaperLogEntity>> =
        diaperDao.getDiaperLogsSince(babyId, since)
}
