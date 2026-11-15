package com.example.wearos.healthif

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.wearos.healthif.ui.health.HealthScreen
import com.example.wearos.healthif.ui.health.HealthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: HealthViewModel
    private var activityResumed = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            updatePermissions()
            if (activityResumed) startSensors()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]
        updatePermissions()

        setContent {
            HealthScreen(
                readings = viewModel.readings,
                syncStatus = viewModel.syncStatus,
                onSync = viewModel::syncHealthReadings,
                onFindPhone = viewModel::findPhone,
                onRefreshSteps = ::refreshSteps,
                onRefreshHeartRate = ::refreshHeartRate,
            )
        }

        val missingPermissions = requiredPermissions().filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    override fun onResume() {
        super.onResume()
        activityResumed = true
        startSensors()
    }

    override fun onPause() {
        activityResumed = false
        viewModel.stopSensors()
        super.onPause()
    }

    private fun startSensors() {
        viewModel.startSensors(
            hasActivityPermission = hasPermission(Manifest.permission.ACTIVITY_RECOGNITION),
            hasHeartRatePermission = hasPermission(heartRatePermission()),
        )
    }

    private fun updatePermissions() {
        viewModel.updatePermissions(
            hasActivityPermission = hasPermission(Manifest.permission.ACTIVITY_RECOGNITION),
            hasHeartRatePermission = hasPermission(heartRatePermission()),
        )
    }

    private fun refreshSteps() {
        if (!hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
            return
        }
        viewModel.refreshSteps()
    }

    private fun refreshHeartRate() {
        if (!hasPermission(heartRatePermission())) {
            permissionLauncher.launch(arrayOf(heartRatePermission()))
            return
        }
        viewModel.refreshHeartRate()
    }

    private fun hasPermission(permission: String): Boolean =
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun requiredPermissions(): List<String> = listOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        heartRatePermission(),
    )

    private fun heartRatePermission(): String =
        if (Build.VERSION.SDK_INT >= 36) {
            "android.permission.health.READ_HEART_RATE"
        } else {
            Manifest.permission.BODY_SENSORS
        }
}
