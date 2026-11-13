package com.example.healthif.ui.feature.heartbeats

import com.example.healthif.data.Entities.HeartBeat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal data class MediaBatimentos(
    val inicio: LocalDateTime,
    val mediaBpm: Float
)

internal fun calcularMediasPorQuatroHoras(
    historico: List<HeartBeat>,
    zoneId: ZoneId = ZoneId.systemDefault()
): List<MediaBatimentos> = historico
    .groupBy { batimento ->
        val horario = Instant.ofEpochMilli(batimento.timestamp).atZone(zoneId)
        horario.toLocalDate().atTime((horario.hour / 4) * 4, 0)
    }
    .toSortedMap()
    .map { (inicio, leituras) ->
        MediaBatimentos(inicio, leituras.map { it.bpm }.average().toFloat())
    }
