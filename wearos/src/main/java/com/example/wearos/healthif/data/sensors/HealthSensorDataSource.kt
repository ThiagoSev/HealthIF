package com.example.wearos.healthif.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.roundToInt

class HealthSensorDataSource(
    context: Context,
    private val onSteps: (Float) -> Unit,
    private val onHeartRate: (Int) -> Unit,
) : SensorEventListener {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private var stepSensorRegistered = false
    private var heartRateSensorRegistered = false

    val hasStepSensor: Boolean
        get() = stepSensor != null

    val hasHeartRateSensor: Boolean
        get() = heartRateSensor != null

    @Suppress("MissingPermission")
    fun start(hasActivityPermission: Boolean, hasHeartRatePermission: Boolean) {
        if (hasActivityPermission && !stepSensorRegistered) {
            stepSensor?.let { sensor ->
                stepSensorRegistered = sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL,
                )
            }
        }
        if (hasHeartRatePermission && !heartRateSensorRegistered) {
            heartRateSensor?.let { sensor ->
                heartRateSensorRegistered = sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL,
                )
            }
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        stepSensorRegistered = false
        heartRateSensorRegistered = false
    }

    @Suppress("MissingPermission")
    fun refreshSteps(): Boolean {
        val sensor = stepSensor ?: return false
        sensorManager.unregisterListener(this, sensor)
        stepSensorRegistered = sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
        return stepSensorRegistered
    }

    @Suppress("MissingPermission")
    fun refreshHeartRate(): Boolean {
        val sensor = heartRateSensor ?: return false
        sensorManager.unregisterListener(this, sensor)
        heartRateSensorRegistered = sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
        return heartRateSensorRegistered
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> event.values.firstOrNull()?.let(onSteps)
            Sensor.TYPE_HEART_RATE -> {
                val bpm = event.values.firstOrNull()?.roundToInt()
                if (bpm != null && bpm > 0) onHeartRate(bpm)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
