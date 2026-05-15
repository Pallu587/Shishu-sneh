package com.example.shishu_sneh_healthcare.domain.repository

import com.example.shishu_sneh_healthcare.data.local.entity.SleepLogEntity
import kotlinx.coroutines.flow.Flow

interface SleepRepository {
    fun getSleepLogs(babyId: Long): Flow<List<SleepLogEntity>>
    suspend fun insertSleepLog(log: SleepLogEntity): Long
    fun getSleepLogsSince(babyId: Long, since: Long): Flow<List<SleepLogEntity>>
}
