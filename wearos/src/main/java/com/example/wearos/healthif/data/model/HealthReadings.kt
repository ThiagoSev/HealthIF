package com.example.wearos.healthif.data.model

import kotlin.math.roundToInt

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
