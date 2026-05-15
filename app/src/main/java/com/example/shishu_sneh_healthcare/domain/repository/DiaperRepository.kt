package com.example.shishu_sneh_healthcare.domain.repository

import com.example.shishu_sneh_healthcare.data.local.entity.DiaperLogEntity
import kotlinx.coroutines.flow.Flow

interface DiaperRepository {
    fun getDiaperLogs(babyId: Long): Flow<List<DiaperLogEntity>>
    suspend fun insertDiaperLog(log: DiaperLogEntity): Long
    fun getDiaperLogsSince(babyId: Long, since: Long): Flow<List<DiaperLogEntity>>
}
