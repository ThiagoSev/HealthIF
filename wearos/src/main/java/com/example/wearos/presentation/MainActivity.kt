/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wearos.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults.cardColors
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.wearos.R
import com.example.wearos.presentation.theme.DataStoreTheme
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject
import java.time.LocalDate
import kotlin.math.roundToInt
import androidx.core.content.edit

private const val SYNC_PATH = "/healthif/sync"
private const val FIND_PHONE_PATH = "/healthif/find_phone"

data class HealthReadings(
    val steps: Long? = null,
    val calories: Int? = null,
    val distanceMeters: Double? = null,
    val heartRate: Int? = null,
    val hasStepSensor: Boolean = false,
    val hasHeartRateSensor: Boolean = false,
    val hasActivityPermission: Boolean = false,
    val hasHeartRatePermission: Boolean = false,
    val stepsRefreshing: Boolean = false,
    val stepsRefreshTimedOut: Boolean = false,
    val heartRateRefreshing: Boolean = false,
    val heartRateRefreshTimedOut: Boolean = false,
) {
    val stepsLabel: String
        get() = when {
            !hasStepSensor -> "Não disponível"
            !hasActivityPermission -> "Permissão necessária"
            stepsRefreshing -> "Atualizando..."
            stepsRefreshTimedOut -> "Caminhe e tente novamente"
            steps == null -> "Aguardando leitura"
            else -> "$steps passos"
        }

    val caloriesLabel: String
        get() = when {
            !hasStepSensor -> "Não disponível"
            !hasActivityPermission -> "Permissão necessária"
            stepsRefreshing -> "Atualizando..."
            stepsRefreshTimedOut -> "Caminhe e tente novamente"
            calories == null -> "Aguardando leitura"
            else -> "≈ $calories kcal"
        }

    val heartRateLabel: String
        get() = when {
            !hasHeartRateSensor -> "Não disponível"
            !hasHeartRatePermission -> "Permissão necessária"
            heartRateRefreshing -> "Medindo..."
            heartRateRefreshTimedOut -> "Sem leitura; tente novamente"
            heartRate == null -> "Aguardando leitura"
            else -> "$heartRate BPM"
        }

    val distanceLabel: String
        get() = when {
            !hasStepSensor -> "Não disponível"
            !hasActivityPermission -> "Permissão necessária"
            stepsRefreshing -> "Atualizando..."
            stepsRefreshTimedOut -> "Caminhe e tente novamente"
            distanceMeters == null -> "Aguardando leitura"
            distanceMeters >= 1000 -> "≈ %.2f km".format(distanceMeters / 1000)
            else -> "≈ %d m".format(distanceMeters.roundToInt())
        }
}

class MainActivity : ComponentActivity(), SensorEventListener {
    private val readingsState = mutableStateOf(HealthReadings())
    private val syncStatus = mutableStateOf("")
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var heartRateSensor: Sensor? = null
    private var stepSensorRegistered = false
    private var heartRateSensorRegistered = false
    private var activityResumed = false
    private val refreshHandler = Handler(Looper.getMainLooper())
    private val stepsRefreshTimeout = Runnable {
        readingsState.value = readingsState.value.copy(
            stepsRefreshing = false,
            stepsRefreshTimedOut = true,
        )
    }
    private val heartRateRefreshTimeout = Runnable {
        readingsState.value = readingsState.value.copy(
            heartRateRefreshing = false,
            heartRateRefreshTimedOut = true,
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            updateSensorAvailability()
            if (activityResumed) registerAvailableSensors()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        updateSensorAvailability()

        setContent {
            WearApp(
                greetingName = "HealthIF",
                readings = readingsState.value,
                syncStatus = syncStatus.value,
                onSync = { sendHealthReadings() },
                onFindPhone = { findPhone() },
                onRefreshSteps = { refreshSteps() },
                onRefreshHeartRate = { refreshHeartRate() },
            )
        }

        val missingPermissions = requiredSensorPermissions().filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    override fun onResume() {
        super.onResume()
        activityResumed = true
        readingsState.value = readingsState.value.copy(heartRate = null)
        updateSensorAvailability()
        registerAvailableSensors()
    }

    override fun onPause() {
        activityResumed = false
        if (stepSensorRegistered || heartRateSensorRegistered) {
            sensorManager.unregisterListener(this)
            stepSensorRegistered = false
            heartRateSensorRegistered = false
        }
        refreshHandler.removeCallbacks(stepsRefreshTimeout)
        refreshHandler.removeCallbacks(heartRateRefreshTimeout)
        readingsState.value = readingsState.value.copy(
            stepsRefreshing = false,
            heartRateRefreshing = false,
        )
        super.onPause()
    }

    private fun requiredSensorPermissions(): List<String> = listOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        heartRatePermission(),
    )

    private fun heartRatePermission(): String =
        if (Build.VERSION.SDK_INT >= 36) {
            "android.permission.health.READ_HEART_RATE"
        } else {
            Manifest.permission.BODY_SENSORS
        }

    private fun updateSensorAvailability() {
        readingsState.value = readingsState.value.copy(
            hasStepSensor = stepSensor != null,
            hasHeartRateSensor = heartRateSensor != null,
            hasActivityPermission =
                checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) ==
                        PackageManager.PERMISSION_GRANTED,
            hasHeartRatePermission =
                checkSelfPermission(heartRatePermission()) ==
                        PackageManager.PERMISSION_GRANTED,
        )
    }

    @Suppress("MissingPermission")
    private fun registerAvailableSensors() {
        updateSensorAvailability()

        if (readingsState.value.hasActivityPermission) {
            stepSensor?.let { sensor ->
                if (!stepSensorRegistered) {
                    stepSensorRegistered = sensorManager.registerListener(
                        this,
                        sensor,
                        SensorManager.SENSOR_DELAY_NORMAL,
                    )
                }
            }
        }
        if (readingsState.value.hasHeartRatePermission) {
            heartRateSensor?.let { sensor ->
                if (!heartRateSensorRegistered) {
                    heartRateSensorRegistered = sensorManager.registerListener(
                        this,
                        sensor,
                        SensorManager.SENSOR_DELAY_NORMAL,
                    )
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                refreshHandler.removeCallbacks(stepsRefreshTimeout)
                updateSteps(event.values.firstOrNull() ?: return)
            }

            Sensor.TYPE_HEART_RATE -> {
                val bpm = event.values.firstOrNull()?.roundToInt() ?: return
                if (bpm > 0) {
                    refreshHandler.removeCallbacks(heartRateRefreshTimeout)
                    readingsState.value = readingsState.value.copy(
                        heartRate = bpm,
                        heartRateRefreshing = false,
                        heartRateRefreshTimedOut = false,
                    )
                }
            }
        }
    }

    private fun refreshSteps() {
        updateSensorAvailability()
        val sensor = stepSensor ?: return
        if (!readingsState.value.hasActivityPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
            return
        }

        readingsState.value = readingsState.value.copy(
            stepsRefreshing = true,
            stepsRefreshTimedOut = false,
        )
        refreshHandler.removeCallbacks(stepsRefreshTimeout)
        refreshHandler.postDelayed(stepsRefreshTimeout, 10_000)
        sensorManager.unregisterListener(this, sensor)
        stepSensorRegistered = false
        stepSensorRegistered =
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        if (!stepSensorRegistered) {
            refreshHandler.removeCallbacks(stepsRefreshTimeout)
            readingsState.value = readingsState.value.copy(
                stepsRefreshing = false,
                stepsRefreshTimedOut = true,
            )
        }
    }

    private fun refreshHeartRate() {
        updateSensorAvailability()
        val sensor = heartRateSensor ?: return
        if (!readingsState.value.hasHeartRatePermission) {
            permissionLauncher.launch(arrayOf(heartRatePermission()))
            return
        }

        readingsState.value = readingsState.value.copy(
            heartRateRefreshing = true,
            heartRateRefreshTimedOut = false,
        )
        refreshHandler.removeCallbacks(heartRateRefreshTimeout)
        refreshHandler.postDelayed(heartRateRefreshTimeout, 10_000)
        sensorManager.unregisterListener(this, sensor)
        heartRateSensorRegistered = false
        heartRateSensorRegistered =
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        if (!heartRateSensorRegistered) {
            refreshHandler.removeCallbacks(heartRateRefreshTimeout)
            readingsState.value = readingsState.value.copy(
                heartRateRefreshing = false,
                heartRateRefreshTimedOut = true,
            )
        }
    }

    private fun updateSteps(totalSteps: Float) {
        val preferences = getSharedPreferences("health_metrics", MODE_PRIVATE)
        val today = LocalDate.now().toString()
        var baseline = preferences.getFloat("step_baseline", Float.NaN)
        val baselineDate = preferences.getString("step_baseline_date", null)
        if (baselineDate != today || !baseline.isFinite() || totalSteps < baseline) {
            baseline = totalSteps
            preferences.edit {
                putString("step_baseline_date", today)
                    .putFloat("step_baseline", baseline)
            }
        }

        val steps = (totalSteps - baseline).coerceAtLeast(0f).toLong()
        readingsState.value = readingsState.value.copy(
            steps = steps,
            calories = (steps * 0.04).roundToInt(),
            distanceMeters = steps * 0.75,
            stepsRefreshing = false,
            stepsRefreshTimedOut = false,
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun sendHealthReadings() {
        val readings = readingsState.value
        val payload = JSONObject()
            .put("steps", readings.steps ?: JSONObject.NULL)
            .put("calories", readings.calories ?: JSONObject.NULL)
            .put("distanceMeters", readings.distanceMeters ?: JSONObject.NULL)
            .put("heartRate", readings.heartRate ?: JSONObject.NULL)
            .put("collectedAt", System.currentTimeMillis())
            .toString()
            .toByteArray(Charsets.UTF_8)

        sendToConnectedPhones(SYNC_PATH, payload, "Dados sincronizados")
    }

    private fun findPhone() {
        sendToConnectedPhones(FIND_PHONE_PATH, byteArrayOf(), "Sinal enviado ao telefone")
    }

    private fun sendToConnectedPhones(path: String, payload: ByteArray, successMessage: String) {
        syncStatus.value = "Conectando ao telefone..."
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    syncStatus.value = "Nenhum telefone conectado"
                    return@addOnSuccessListener
                }

                var remaining = nodes.size
                var anySuccess = false
                nodes.forEach { node ->
                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, path, payload)
                        .addOnCompleteListener { task ->
                            anySuccess = anySuccess || task.isSuccessful
                            remaining--
                            if (remaining == 0) {
                                syncStatus.value = if (anySuccess) {
                                    successMessage
                                } else {
                                    "Falha ao enviar para o telefone"
                                }
                            }
                        }
                }
            }
            .addOnFailureListener {
                syncStatus.value = "Não foi possível localizar o telefone"
            }
    }
}

@Composable
fun WearApp(
    greetingName: String,
    readings: HealthReadings,
    syncStatus: String,
    onSync: () -> Unit,
    onFindPhone: () -> Unit,
    onRefreshSteps: () -> Unit,
    onRefreshHeartRate: () -> Unit,
) {
    DataStoreTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            val corCard = Color(0xFF1C1B1F)
            ScreenScaffold(
                scrollState = listState,
                edgeButton = {
                    EdgeButton(
                        onClick = onFindPhone,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                    ) {
                        Text("Encontrar\nTelefone")
                    }
                },
            ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed

                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.app_name, greetingName))
                        }
                    }
                    item {

                        Card(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            colors = cardColors(
                                containerColor = corCard,
                            ),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),

                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                                    8.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = onRefreshSteps)
                                            .clip(RoundedCornerShape(10.dp))
                                            .defaultMinSize(minHeight = 25.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.Center,

                                        ) {
                                        Text(
                                            text = "Passos:\n${readings.stepsLabel}",
                                            style = MaterialTheme.typography.bodyExtraSmall,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                    VerticalDivider(
                                        modifier = Modifier
                                            .padding(vertical = 1.dp)
                                            .fillMaxHeight(),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = onRefreshSteps)
                                            .clip(RoundedCornerShape(10.dp))
                                            .defaultMinSize(minHeight = 25.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Calorias:\n${readings.caloriesLabel}",
                                            style = MaterialTheme.typography.bodyExtraSmall,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                }

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 2.dp
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = onRefreshHeartRate)
                                            .clip(RoundedCornerShape(10.dp))
                                            .defaultMinSize(minHeight = 25.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Batimentos:\n${readings.heartRateLabel}",
                                            style = MaterialTheme.typography.bodyExtraSmall,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                    VerticalDivider(
                                        modifier = Modifier
                                            .padding(vertical = 1.dp)
                                            .fillMaxHeight(),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = onRefreshSteps)
                                            .clip(RoundedCornerShape(10.dp))
                                            .defaultMinSize(minHeight = 25.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Distância:\n${readings.distanceLabel}",
                                            style = MaterialTheme.typography.bodyExtraSmall,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }

                        }
                    }

                    item { Spacer(modifier = Modifier.height(5.dp)) }

                    item {
                        Button(
                            onClick = onSync,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),

                            ) {
                            Text(
                                text = "Sincronizar",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    if (syncStatus.isNotBlank()) {
                        item {
                            Text(
                                text = syncStatus,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyExtraSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp(
        greetingName = "Preview Android",
        readings = HealthReadings(),
        syncStatus = "",
        onSync = {},
        onFindPhone = {},
        onRefreshSteps = {},
        onRefreshHeartRate = {},
    )
}