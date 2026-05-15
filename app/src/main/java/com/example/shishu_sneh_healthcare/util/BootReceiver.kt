package com.example.shishu_sneh_healthcare.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.example.shishu_sneh_healthcare.domain.repository.BabyRepository
import com.example.shishu_sneh_healthcare.worker.VaccineReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var babyRepository: BabyRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                // Fetch all babies and reschedule reminders
                val babies = babyRepository.getBabiesForUser("dummy_user_id").first()
                babies.forEach { baby ->
                    scheduleVaccineReminders(context, baby.id)
                }
            }
        }
    }

    private fun scheduleVaccineReminders(context: Context, babyId: Long) {
        val workRequest = PeriodicWorkRequestBuilder<VaccineReminderWorker>(24, TimeUnit.HOURS)
            .setInputData(workDataOf("babyId" to babyId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "VaccineReminder_$babyId",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
