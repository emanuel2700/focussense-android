package pe.edu.uni.focussense.model

enum class AdaptationMode {
    NORMAL,
    DARK,
    OUTDOOR,
    ECO,
    DARK_ECO,
    OUTDOOR_ECO
}

data class AdaptationDecision(
    val mode: AdaptationMode,
    val useDarkPalette: Boolean,
    val highContrast: Boolean,
    val animationsEnabled: Boolean,
    val showTips: Boolean,
    val title: String,
    val explanation: String
)
