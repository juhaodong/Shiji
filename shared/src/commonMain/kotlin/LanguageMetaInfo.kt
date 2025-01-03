data class LanguageMetaInfo(
    val language: String,
    val region: String,
    val script: String,
    val name: String,
    val localizedName: String
)

val currentLocales = arrayOf(
    LanguageMetaInfo(
        language = "en",
        region = "US",
        script = "",
        name = "English",
        localizedName = "English",
    ),
    LanguageMetaInfo(
        language = "de",
        region = "",
        script = "",
        name = "German",
        localizedName = "Deutsch",
    ),
    LanguageMetaInfo(
        language = "zh",
        region = "",
        script = "Hans",
        name = "Simplified Chinese",
        localizedName = "简体中文",
    ),
    LanguageMetaInfo(
        language = "nl",
        region = "",
        script = "",
        name = "Dutch",
        localizedName = "Nederlands",
    ),
    LanguageMetaInfo(
        language = "ar",
        region = "",
        script = "",
        name = "Arabic",
        localizedName = "العربية",
    ),
    LanguageMetaInfo(
        language = "cs",
        region = "",
        script = "",
        name = "Czech",
        localizedName = "Čeština",
    ),
    LanguageMetaInfo(
        language = "da",
        region = "",
        script = "",
        name = "Danish",
        localizedName = "Dansk",
    ),
    LanguageMetaInfo(
        language = "es",
        region = "",
        script = "",
        name = "Spanish",
        localizedName = "Español",
    ),
    LanguageMetaInfo(
        language = "fr",
        region = "FR",
        script = "",
        name = "French (France)",
        localizedName = "Français (France)",
    ),
    LanguageMetaInfo(
        language = "it",
        region = "",
        script = "",
        name = "Italian",
        localizedName = "Italiano",
    ),
    LanguageMetaInfo(
        language = "ja",
        region = "",
        script = "",
        name = "Japanese",
        localizedName = "日本語",
    ),
    LanguageMetaInfo(
        language = "ko",
        region = "",
        script = "",
        name = "Korean",
        localizedName = "한국어",
    ),

    LanguageMetaInfo(
        language = "ro",
        region = "",
        script = "",
        name = "Romanian",
        localizedName = "Română",
    ),
    LanguageMetaInfo(
        language = "ru",
        region = "",
        script = "",
        name = "Russian",
        localizedName = "Русский",
    ),
    LanguageMetaInfo(
        language = "tr",
        region = "",
        script = "",
        name = "Turkish",
        localizedName = "Türkçe",
    ),
    LanguageMetaInfo(
        language = "uk",
        region = "",
        script = "",
        name = "Ukrainian",
        localizedName = "Українська",
    ),
    LanguageMetaInfo(
        language = "vi",
        region = "",
        script = "",
        name = "Vietnamese",
        localizedName = "Tiếng Việt",
    ),
)
