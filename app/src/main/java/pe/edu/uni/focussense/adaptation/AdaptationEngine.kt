package pe.edu.uni.focussense.adaptation

import pe.edu.uni.focussense.model.AdaptationDecision
import pe.edu.uni.focussense.model.AdaptationMode
import pe.edu.uni.focussense.model.ContextSnapshot

/**
 * Motor de decisión sin dependencias de Android.
 *
 * Reglas:
 * - Poca luz: interfaz oscura.
 * - Luz intensa: alto contraste para exteriores.
 * - Batería <= 20% y sin cargar: modo ECO (sin animaciones y sin consejos secundarios).
 *
 * Se usa histéresis para evitar que la interfaz cambie continuamente cerca del umbral.
 */
class AdaptationEngine {

    private enum class LightingState { NORMAL, DARK, OUTDOOR }

    private var lightingState = LightingState.NORMAL

    fun decide(context: ContextSnapshot): AdaptationDecision {
        updateLightingState(context.lux)

        val eco = context.batteryLevel <= LOW_BATTERY_PERCENT && !context.isCharging

        return when {
            lightingState == LightingState.DARK && eco -> AdaptationDecision(
                mode = AdaptationMode.DARK_ECO,
                useDarkPalette = true,
                highContrast = false,
                animationsEnabled = false,
                showTips = false,
                title = "Modo noche + ECO",
                explanation = "Poca luz y batería baja: tema oscuro, sin animaciones ni elementos secundarios."
            )

            lightingState == LightingState.OUTDOOR && eco -> AdaptationDecision(
                mode = AdaptationMode.OUTDOOR_ECO,
                useDarkPalette = false,
                highContrast = true,
                animationsEnabled = false,
                showTips = false,
                title = "Modo exterior + ECO",
                explanation = "Luz intensa y batería baja: alto contraste con consumo visual reducido."
            )

            lightingState == LightingState.DARK -> AdaptationDecision(
                mode = AdaptationMode.DARK,
                useDarkPalette = true,
                highContrast = false,
                animationsEnabled = true,
                showTips = true,
                title = "Modo noche",
                explanation = "La iluminación ambiental es baja: la interfaz cambia automáticamente a una paleta oscura."
            )

            lightingState == LightingState.OUTDOOR -> AdaptationDecision(
                mode = AdaptationMode.OUTDOOR,
                useDarkPalette = false,
                highContrast = true,
                animationsEnabled = true,
                showTips = true,
                title = "Modo exterior",
                explanation = "La iluminación es intensa: la interfaz aumenta el contraste para mantener la legibilidad."
            )

            eco -> AdaptationDecision(
                mode = AdaptationMode.ECO,
                useDarkPalette = false,
                highContrast = false,
                animationsEnabled = false,
                showTips = false,
                title = "Modo ECO",
                explanation = "Batería baja y sin cargar: se desactivan animaciones y contenido secundario."
            )

            else -> AdaptationDecision(
                mode = AdaptationMode.NORMAL,
                useDarkPalette = false,
                highContrast = false,
                animationsEnabled = true,
                showTips = true,
                title = "Modo normal",
                explanation = "El contexto está dentro de rangos normales; se mantiene la experiencia completa."
            )
        }
    }

    private fun updateLightingState(lux: Float?) {
        if (lux == null) {
            lightingState = LightingState.NORMAL
            return
        }

        lightingState = when (lightingState) {
            LightingState.NORMAL -> when {
                lux < DARK_ENTER_LUX -> LightingState.DARK
                lux > OUTDOOR_ENTER_LUX -> LightingState.OUTDOOR
                else -> LightingState.NORMAL
            }

            LightingState.DARK -> when {
                lux > OUTDOOR_ENTER_LUX -> LightingState.OUTDOOR
                lux > DARK_EXIT_LUX -> LightingState.NORMAL
                else -> LightingState.DARK
            }

            LightingState.OUTDOOR -> when {
                lux < DARK_ENTER_LUX -> LightingState.DARK
                lux < OUTDOOR_EXIT_LUX -> LightingState.NORMAL
                else -> LightingState.OUTDOOR
            }
        }
    }

    companion object {
        const val LOW_BATTERY_PERCENT = 20
        const val DARK_ENTER_LUX = 40f
        const val DARK_EXIT_LUX = 70f
        const val OUTDOOR_ENTER_LUX = 1000f
        const val OUTDOOR_EXIT_LUX = 700f
    }
}
