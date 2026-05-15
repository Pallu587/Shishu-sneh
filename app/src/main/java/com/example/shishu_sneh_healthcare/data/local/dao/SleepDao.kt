package com.example.shishu_sneh_healthcare.data.local.dao

import androidx.room.*
import com.example.shishu_sneh_healthcare.data.local.entity.SleepLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs WHERE babyId = :babyId ORDER BY startTime DESC")
    fun getSleepLogs(babyId: Long): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLogEntity): Long

    @Query("SELECT * FROM sleep_logs WHERE babyId = :babyId AND startTime >= :since")
    fun getSleepLogsSince(babyId: Long, since: Long): Flow<List<SleepLogEntity>>
}
