package pe.edu.uni.focussense.model

data class ContextSnapshot(
    val lux: Float?,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val timestampMillis: Long = System.currentTimeMillis()
)
