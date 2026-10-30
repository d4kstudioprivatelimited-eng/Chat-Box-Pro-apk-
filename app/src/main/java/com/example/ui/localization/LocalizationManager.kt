package com.example.ui.localization

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalAppStrings = compositionLocalOf { Translations.english }
val LocalCurrentLanguage = compositionLocalOf { AppLanguage.ENGLISH }

/**
 * LocalizationManager
 * 
 * Manages dynamic in-app language switching without requiring an app restart.
 * Persists user preference in SharedPreferences and provides reactive StateFlows.
 */
object LocalizationManager {

    private const val PREFS_NAME = "chatbox_locale_prefs"
    private const val KEY_LANGUAGE = "selected_language_code"

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentStrings = MutableStateFlow(Translations.english)
    val currentStrings: StateFlow<AppStrings> = _currentStrings.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCode = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
        val lang = AppLanguage.fromCode(savedCode)
        _currentLanguage.value = lang
        _currentStrings.value = Translations.forLanguage(lang)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        _currentLanguage.value = language
        _currentStrings.value = Translations.forLanguage(language)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}

/**
 * Wraps content with LocalAppStrings and LocalCurrentLanguage for instant reactivity.
 */
@Composable
fun ProvideLocalizedStrings(
    strings: AppStrings,
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalCurrentLanguage provides language
    ) {
        content()
    }
}
