package com.example.shishu_sneh_healthcare.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import com.example.shishu_sneh_healthcare.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class VaccineReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val vaccineRepository: VaccineRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val babyId = inputData.getLong("babyId", -1L)
        if (babyId == -1L) return Result.failure()

        val overdueVaccines = vaccineRepository.getOverdueVaccines(babyId).first()
        if (overdueVaccines.isNotEmpty()) {
            val notificationHelper = NotificationHelper(appContext)
            overdueVaccines.forEach { vaccine ->
                notificationHelper.showVaccineNotification(
                    title = "Vaccination Overdue!",
                    message = "${vaccine.name} is overdue for your baby. Please visit the clinic soon."
                )
            }
        }

        return Result.success()
    }
}
