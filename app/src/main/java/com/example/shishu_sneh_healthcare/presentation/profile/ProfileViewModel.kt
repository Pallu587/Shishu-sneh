package com.example.shishu_sneh_healthcare.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.local.entity.MilestoneEntity
import com.example.shishu_sneh_healthcare.domain.repository.BabyRepository
import com.example.shishu_sneh_healthcare.domain.repository.MilestoneRepository
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import com.example.shishu_sneh_healthcare.domain.util.VaccineScheduler
import com.example.shishu_sneh_healthcare.worker.VaccineReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ProfileStats(
    val vaccinesDone: Int = 0,
    val milestonesDone: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val babyRepository: BabyRepository,
    private val vaccineRepository: VaccineRepository,
    private val milestoneRepository: MilestoneRepository,
) : AndroidViewModel(application) {

    private val _babyProfile = MutableStateFlow<BabyEntity?>(null)
    val babyProfile: StateFlow<BabyEntity?> = _babyProfile.asStateFlow()

    private val _stats = MutableStateFlow<ProfileStats>(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            // Fetch first baby for the dummy user
            babyRepository.getBabiesForUser("dummy_user_id").collectLatest { babies ->
                val baby = babies.firstOrNull()
                _babyProfile.value = baby
                
                baby?.let {
                    loadStats(it.id)
                }
            }
        }
    }

    private fun loadStats(babyId: Long) {
        viewModelScope.launch {
            vaccineRepository.getVaccinesForBaby(babyId).collectLatest { vaccines ->
                val doneCount = vaccines.count { it.status == "Done" }
                _stats.value = _stats.value.copy(vaccinesDone = doneCount)
            }
        }
        viewModelScope.launch {
            milestoneRepository.getMilestonesForBaby(babyId).collectLatest { milestones ->
                val doneCount = milestones.count { it.status == "Yes" }
                _stats.value = _stats.value.copy(milestonesDone = doneCount)
            }
        }
    }

    fun saveBabyDetails(baby: BabyEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val babyId = babyRepository.insertBaby(baby)
            
            // Generate and save vaccination schedule
            val vaccines = VaccineScheduler.generateSchedule(babyId, baby.dob)
            vaccineRepository.insertVaccines(vaccines)
            
            // Generate and save initial milestones
            val initialMilestones = listOf(
                MilestoneEntity(0, babyId, 1, "Reacts to loud sounds", "No", null, null),
                MilestoneEntity(0, babyId, 1, "Watches faces", "No", null, null),
                MilestoneEntity(0, babyId, 2, "Smiles at people", "No", null, null),
                MilestoneEntity(0, babyId, 2, "Holds head up during tummy time", "No", null, null),
                MilestoneEntity(0, babyId, 4, "Babbles with expression", "No", null, null),
                MilestoneEntity(0, babyId, 4, "Reaches for toy with one hand", "No", null, null),
                MilestoneEntity(0, babyId, 6, "Responds to own name", "No", null, null),
                MilestoneEntity(0, babyId, 6, "Rolls over in both directions", "No", null, null),
                MilestoneEntity(0, babyId, 9, "Makes a lot of different sounds like \"mamama\" and \"bababa\"", "No", null, null),
                MilestoneEntity(0, babyId, 9, "Sits without support", "No", null, null),
                MilestoneEntity(0, babyId, 12, "Responds to simple spoken requests", "No", null, null),
                MilestoneEntity(0, babyId, 12, "Gets to a sitting position without help", "No", null, null)
            )
            milestoneRepository.insertMilestones(initialMilestones)

            // Schedule WorkManager for vaccination reminders
            scheduleVaccineReminders(babyId)

            onSuccess()
        }
    }

    private fun scheduleVaccineReminders(babyId: Long) {
        val workRequest = PeriodicWorkRequestBuilder<VaccineReminderWorker>(24, TimeUnit.HOURS)
            .setInputData(workDataOf("babyId" to babyId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            "VaccineReminder_$babyId",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
