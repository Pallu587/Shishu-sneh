package com.example.shishu_sneh_healthcare.data.local.dao

import androidx.room.*
import com.example.shishu_sneh_healthcare.data.local.entity.DiaperLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaperDao {
    @Query("SELECT * FROM diaper_logs WHERE babyId = :babyId ORDER BY time DESC")
    fun getDiaperLogs(babyId: Long): Flow<List<DiaperLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaperLog(log: DiaperLogEntity): Long

    @Query("SELECT * FROM diaper_logs WHERE babyId = :babyId AND time >= :since")
    fun getDiaperLogsSince(babyId: Long, since: Long): Flow<List<DiaperLogEntity>>
}
