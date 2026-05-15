package com.example.shishu_sneh_healthcare.data.repository

import com.example.shishu_sneh_healthcare.data.local.dao.SleepDao
import com.example.shishu_sneh_healthcare.data.local.entity.SleepLogEntity
import com.example.shishu_sneh_healthcare.domain.repository.SleepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SleepRepositoryImpl @Inject constructor(
    private val sleepDao: SleepDao
) : SleepRepository {
    override fun getSleepLogs(babyId: Long): Flow<List<SleepLogEntity>> =
        sleepDao.getSleepLogs(babyId)

    override suspend fun insertSleepLog(log: SleepLogEntity): Long =
        sleepDao.insertSleepLog(log)

    override fun getSleepLogsSince(babyId: Long, since: Long): Flow<List<SleepLogEntity>> =
        sleepDao.getSleepLogsSince(babyId, since)
}
