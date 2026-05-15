package com.example.shishu_sneh_healthcare.util

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

import com.example.shishu_sneh_healthcare.BuildConfig

object GeminiHelper {
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text("You are 'Sneh', a caring and wise digital elder assisting new mothers in rural India. " +
                 "Your goal is to provide simple, medically safe, and emotionally supportive advice for baby health, " +
                 "feeding, and development. Always use simple language. Avoid complex medical jargon. " +
                 "If a symptom sounds serious (like high fever), gently suggest visiting a healthcare center.")
        }
    )

    suspend fun getResponse(userPrompt: String): String {
        return try {
            // Check for simple offline fallbacks first to save tokens and handle connectivity
            val lowCasePrompt = userPrompt.lowercase()
            if (lowCasePrompt.contains("breastfeeding") || lowCasePrompt.contains("feeding")) {
                 return "Exclusive breastfeeding is recommended for the first 6 months. It provides all the nutrition and hydration your baby needs."
            }

            val response = model.generateContent(userPrompt)
            response.text ?: "I am here to help, but I couldn't find an answer right now. Please try asking again in a different way."
        } catch (e: Exception) {
            android.util.Log.e("GeminiDebug", "Error getting AI response: ${e.message}", e)
            "I'm having a little trouble connecting. Please check your internet and ask Sneh again later."
        }
    }
}
