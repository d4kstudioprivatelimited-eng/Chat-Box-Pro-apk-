package com.example.ui.localization

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flagEmoji: String
) {
    ENGLISH("en", "English", "English", "🇺🇸"),
    SPANISH("es", "Español", "Spanish", "🇪🇸"),
    HINDI("hi", "हिन्दी", "Hindi", "🇮🇳"),
    ARABIC("ar", "العربية", "Arabic", "🇸🇦"),
    FRENCH("fr", "Français", "French", "🇫🇷"),
    GERMAN("de", "Deutsch", "German", "🇩🇪"),
    PORTUGUESE("pt", "Português", "Portuguese", "🇧🇷"),
    RUSSIAN("ru", "Русский", "Russian", "🇷🇺"),
    INDONESIAN("id", "Bahasa Indonesia", "Indonesian", "🇮🇩"),
    BENGALI("bn", "বাংলা", "Bengali", "🇧🇩"),
    URDU("ur", "اردو", "Urdu", "🇵🇰");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
