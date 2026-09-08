package pe.edu.uni.focussense.context

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightSensorService(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private var listener: ((Float?) -> Unit)? = null

    val isAvailable: Boolean
        get() = lightSensor != null

    fun start(onLuxChanged: (Float?) -> Unit) {
        listener = onLuxChanged
        val sensor = lightSensor
        if (sensor == null) {
            onLuxChanged(null)
            return
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        listener = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_LIGHT) return
        listener?.invoke(event.values.firstOrNull())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
