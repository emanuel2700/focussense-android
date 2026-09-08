package pe.edu.uni.focussense.context

import android.content.Context
import pe.edu.uni.focussense.adaptation.AdaptationEngine
import pe.edu.uni.focussense.model.AdaptationDecision
import pe.edu.uni.focussense.model.ContextSnapshot

/**
 * Orquesta el pipeline adaptativo:
 * CONTEXTO -> PROCESAMIENTO -> DECISIÓN -> ADAPTACIÓN.
 */
class ContextManager(context: Context) {

    private val lightSensorService = LightSensorService(context)
    private val batteryService = BatteryService(context)
    private val adaptationEngine = AdaptationEngine()

    private var latestLux: Float? = null
    private var latestBattery = 100
    private var latestCharging = false
    private var observer: ((ContextSnapshot, AdaptationDecision) -> Unit)? = null

    val hasLightSensor: Boolean
        get() = lightSensorService.isAvailable

    fun start(onContextChanged: (ContextSnapshot, AdaptationDecision) -> Unit) {
        observer = onContextChanged

        lightSensorService.start { lux ->
            latestLux = lux
            publish()
        }

        batteryService.start { state ->
            latestBattery = state.level
            latestCharging = state.isCharging
            publish()
        }
    }

    fun stop() {
        lightSensorService.stop()
        batteryService.stop()
        observer = null
    }

    private fun publish() {
        val snapshot = ContextSnapshot(
            lux = latestLux,
            batteryLevel = latestBattery,
            isCharging = latestCharging
        )
        val decision = adaptationEngine.decide(snapshot)
        observer?.invoke(snapshot, decision)
    }
}
