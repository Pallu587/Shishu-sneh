package com.example.shishu_sneh_healthcare.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.preferences.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            preferenceManager.saveAppLanguage(languageCode)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferenceManager.clearAll()
            onSuccess()
        }
    }
}
