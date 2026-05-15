package com.example.shishu_sneh_healthcare.domain.util

import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import java.util.Calendar

/**
 * Utility responsible for generating a standardized Indian immunization schedule.
 * Logic is based on day offsets from the baby's Date of Birth (DOB).
 */
object VaccineScheduler {

    /**
     * Represents a vaccination rule.
     * @param name Name of the vaccine (e.g., BCG)
     * @param disease Targeted disease(s)
     * @param dayOffset Days from birth when vaccine should be administered
     */
    data class VaccineTemplate(
        val name: String,
        val disease: String,
        val dayOffset: Int
    )

    // Standard list of vaccinations for the first 12 months
    private val standardVaccines = listOf(
        VaccineTemplate("BCG", "Tuberculosis", 0),
        VaccineTemplate("Hepatitis B (Birth Dose)", "Hepatitis B", 0),
        VaccineTemplate("OPV 0", "Polio", 0),
        VaccineTemplate("OPV 1", "Polio", 42), // 6 weeks
        VaccineTemplate("Pentavalent 1", "DPT, Hep B, HiB", 42),
        VaccineTemplate("IPV 1", "Polio", 42),
        VaccineTemplate("Rotavirus 1", "Diarrhea", 42),
        VaccineTemplate("PCV 1", "Pneumonia", 42),
        VaccineTemplate("OPV 2", "Polio", 70), // 10 weeks
        VaccineTemplate("Pentavalent 2", "DPT, Hep B, HiB", 70),
        VaccineTemplate("Rotavirus 2", "Diarrhea", 70),
        VaccineTemplate("OPV 3", "Polio", 98), // 14 weeks
        VaccineTemplate("Pentavalent 3", "DPT, Hep B, HiB", 98),
        VaccineTemplate("IPV 2", "Polio", 98),
        VaccineTemplate("Rotavirus 3", "Diarrhea", 98),
        VaccineTemplate("PCV 2", "Pneumonia", 98),
        VaccineTemplate("MR 1", "Measles, Rubella", 270), // 9 months
        VaccineTemplate("JE 1", "Japanese Encephalitis", 270),
        VaccineTemplate("Vitamin A 1", "Blindness prevention", 270)
    )

    /**
     * Generates a list of [VaccineEntity] objects mapped to specific dates.
     * @param babyId The ID of the baby profile this schedule belongs to
     * @param dob The birth timestamp of the baby
     */
    fun generateSchedule(babyId: Long, dob: Long): List<VaccineEntity> {
        val calendar = Calendar.getInstance()
        
        return standardVaccines.map { template ->
            calendar.timeInMillis = dob
            calendar.add(Calendar.DAY_OF_YEAR, template.dayOffset)
            
            VaccineEntity(
                babyId = babyId,
                name = template.name,
                disease = template.disease,
                scheduledDate = calendar.timeInMillis,
                givenDate = null,
                hospital = null,
                doctor = null,
                // Automatically mark as overdue if the date has already passed
                status = if (calendar.timeInMillis < System.currentTimeMillis()) "Overdue" else "Upcoming"
            )
        }
    }
}
