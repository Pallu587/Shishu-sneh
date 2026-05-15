package com.example.shishu_sneh_healthcare.domain.use_case

import javax.inject.Inject

data class Insight(
    val title: String,
    val description: String,
    val icon: String = "✨"
)

/**
 * Use case to provide personalized, age-appropriate health insights for the infant.
 * 
 * **Educational Impact**: By providing proactive advice (feeding, sleep, milestones) 
 * in the user's preferred language, the app bridges the information gap for 
 * first-time mothers in rural settings.
 *
 * @constructor Creates an instance of the use case.
 */
class GetInsightsUseCase @Inject constructor() {
    operator fun invoke(babyAgeInWeeks: Int): List<Insight> {
        return when {
            babyAgeInWeeks < 4 -> listOf(
                Insight("Feeding Tip", "Your newborn needs small, frequent feeds. Exclusive breastfeeding is best for building immunity.", "🍼"),
                Insight("Sleep Support", "Newborns sleep a lot but in short bursts. Try to rest whenever your little one rests.", "😴"),
                Insight("Connection", "Skin-to-skin contact helps your baby feel safe and helps your milk supply.", "🤱")
            )
            babyAgeInWeeks < 12 -> listOf(
                Insight("Development", "Baby is starting to recognize your voice and face. Keep talking and smiling!", "😊"),
                Insight("Tummy Time", "2-3 minutes of tummy time daily helps strengthen those neck muscles for holding their head up.", "🐢"),
                Insight("Crying", "It's baby's way of talking. They might just need a cuddle or a diaper change.", "💙")
            )
            babyAgeInWeeks < 24 -> listOf(
                Insight("Active Play", "Baby is becoming more curious. Use colorful toys to encourage them to reach and grab.", "🌈"),
                Insight("Solid Prep", "Around 6 months, baby might show interest in your food. Talk to your doctor about starting simple mashes.", "🥣"),
                Insight("Rolling", "Stay watchful! Baby might start rolling over soon. Keep them safe during diaper changes.", "🔄")
            )
            else -> listOf(
                Insight("Nutrition", "Iron-rich foods like ragi or dal mash are great as you introduce variety.", "🍛"),
                Insight("Sitting Up", "Baby is getting stronger! Support them with pillows as they learn to sit and see the world.", "🧘"),
                Insight("Social", "Your baby loves 'Peek-a-boo'! It's a great way to learn about objects and people.", "🙈")
            )
        }
    }
}
