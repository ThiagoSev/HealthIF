package com.example.wearos.healthif.ui.health

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.example.wearos.healthif.data.model.HealthReadings
import com.example.wearos.healthif.data.sensors.HealthSensorDataSource
import com.example.wearos.healthif.data.sync.PhoneSyncDataSource
import java.time.LocalDate
import kotlin.math.roundToInt

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    var readings by mutableStateOf(HealthReadings())
        private set

    var syncStatus by mutableStateOf("")
        private set

    private val refreshHandler = Handler(Looper.getMainLooper())
    private val sensorDataSource = HealthSensorDataSource(
        context = application,
        onSteps = ::updateSteps,
        onHeartRate = ::updateHeartRate,
    )
    private val phoneSyncDataSource = PhoneSyncDataSource(application)
    private val stepsRefreshTimeout = Runnable {
        readings = readings.copy(
            stepsRefreshing = false,
            stepsRefreshTimedOut = true,
        )
    }
    private val heartRateRefreshTimeout = Runnable {
        readings = readings.copy(
            heartRateRefreshing = false,
            heartRateRefreshTimedOut = true,
        )
    }

    fun updatePermissions(hasActivityPermission: Boolean, hasHeartRatePermission: Boolean) {
        readings = readings.copy(
            hasStepSensor = sensorDataSource.hasStepSensor,
            hasHeartRateSensor = sensorDataSource.hasHeartRateSensor,
            hasActivityPermission = hasActivityPermission,
            hasHeartRatePermission = hasHeartRatePermission,
        )
    }

    fun startSensors(hasActivityPermission: Boolean, hasHeartRatePermission: Boolean) {
        updatePermissions(hasActivityPermission, hasHeartRatePermission)
        readings = readings.copy(heartRate = null)
        sensorDataSource.start(hasActivityPermission, hasHeartRatePermission)
    }

    fun stopSensors() {
        sensorDataSource.stop()
        refreshHandler.removeCallbacks(stepsRefreshTimeout)
        refreshHandler.removeCallbacks(heartRateRefreshTimeout)
        readings = readings.copy(
            stepsRefreshing = false,
            heartRateRefreshing = false,
        )
    }

    fun refreshSteps() {
        readings = readings.copy(
            stepsRefreshing = true,
            stepsRefreshTimedOut = false,
        )
        refreshHandler.removeCallbacks(stepsRefreshTimeout)
        refreshHandler.postDelayed(stepsRefreshTimeout, REFRESH_TIMEOUT_MS)
        if (!sensorDataSource.refreshSteps()) {
            refreshHandler.removeCallbacks(stepsRefreshTimeout)
            stepsRefreshTimeout.run()
        }
    }

    fun refreshHeartRate() {
        readings = readings.copy(
            heartRateRefreshing = true,
            heartRateRefreshTimedOut = false,
        )
        refreshHandler.removeCallbacks(heartRateRefreshTimeout)
        refreshHandler.postDelayed(heartRateRefreshTimeout, REFRESH_TIMEOUT_MS)
        if (!sensorDataSource.refreshHeartRate()) {
            refreshHandler.removeCallbacks(heartRateRefreshTimeout)
            heartRateRefreshTimeout.run()
        }
    }

    fun syncHealthReadings() {
        phoneSyncDataSource.syncHealthReadings(readings) { syncStatus = it }
    }

    fun findPhone() {
        phoneSyncDataSource.findPhone { syncStatus = it }
    }

    private fun updateSteps(totalSteps: Float) {
        refreshHandler.removeCallbacks(stepsRefreshTimeout)
        val preferences = getApplication<Application>()
            .getSharedPreferences(HEALTH_METRICS_PREFERENCES, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        var baseline = preferences.getFloat(STEP_BASELINE_KEY, Float.NaN)
        val baselineDate = preferences.getString(STEP_BASELINE_DATE_KEY, null)
        if (baselineDate != today || !baseline.isFinite() || totalSteps < baseline) {
            baseline = totalSteps
            preferences.edit {
                putString(STEP_BASELINE_DATE_KEY, today)
                putFloat(STEP_BASELINE_KEY, baseline)
            }
        }

        val steps = (totalSteps - baseline).coerceAtLeast(0f).toLong()
        readings = readings.copy(
            steps = steps,
            calories = (steps * CALORIES_PER_STEP).roundToInt(),
            distanceMeters = steps * METERS_PER_STEP,
            stepsRefreshing = false,
            stepsRefreshTimedOut = false,
        )
    }

    private fun updateHeartRate(bpm: Int) {
        refreshHandler.removeCallbacks(heartRateRefreshTimeout)
        readings = readings.copy(
            heartRate = bpm,
            heartRateRefreshing = false,
            heartRateRefreshTimedOut = false,
        )
    }

    override fun onCleared() {
        stopSensors()
        super.onCleared()
    }

    private companion object {
        const val REFRESH_TIMEOUT_MS = 10_000L
        const val HEALTH_METRICS_PREFERENCES = "health_metrics"
        const val STEP_BASELINE_KEY = "step_baseline"
        const val STEP_BASELINE_DATE_KEY = "step_baseline_date"
        const val CALORIES_PER_STEP = 0.04
        const val METERS_PER_STEP = 0.75
    }
}
