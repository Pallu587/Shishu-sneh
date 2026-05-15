package com.example.shishu_sneh_healthcare.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shishu_sneh_healthcare.data.local.dao.*
import com.example.shishu_sneh_healthcare.data.local.entity.*

@Database(
    entities = [
        BabyEntity::class,
        GrowthEntryEntity::class,
        VaccineEntity::class,
        MilestoneEntity::class,
        FeedingLogEntity::class,
        HealthRecordEntity::class,
        MedicationEntity::class,
        NotificationEntity::class,
        UserEntity::class,
        SleepLogEntity::class,
        DiaperLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun growthDao(): GrowthDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun feedingDao(): FeedingDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun medicationDao(): MedicationDao
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao
    abstract fun sleepDao(): SleepDao
    abstract fun diaperDao(): DiaperDao
}
