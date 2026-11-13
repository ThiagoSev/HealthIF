package com.example.healthif.ui.feature.heartbeats

import com.example.healthif.data.Entities.HeartBeat
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediasBatimentosTest {
    private val zoneId = ZoneId.of("America/Sao_Paulo")

    private fun leitura(horario: String, bpm: Int) = HeartBeat(
        bpm = bpm,
        timestamp = LocalDateTime.parse(horario).atZone(zoneId).toInstant().toEpochMilli()
    )

    @Test
    fun calculaMediaSemArredondarEOrdenaFaixas() {
        val medias = calcularMediasPorQuatroHoras(
            listOf(
                leitura("2026-11-08T06:00:00", 90),
                leitura("2026-11-08T03:59:59", 81),
                leitura("2026-11-08T00:00:00", 70),
                leitura("2026-11-08T04:00:00", 80)
            ), zoneId
        )

        assertEquals(listOf(0, 4), medias.map { it.inicio.hour })
        assertEquals(75.5f, medias[0].mediaBpm, 0.001f)
        assertEquals(85f, medias[1].mediaBpm, 0.001f)
    }

    @Test
    fun separaDiasEAnosSemCriarZerosParaFaixasVazias() {
        val medias = calcularMediasPorQuatroHoras(
            listOf(
                leitura("2027-01-01T00:00:00", 80),
                leitura("2026-12-31T23:59:59", 70),
                leitura("2026-01-01T00:00:00", 60)
            ), zoneId
        )

        assertEquals(
            listOf("2026-01-01T00:00", "2026-12-31T20:00", "2027-01-01T00:00"),
            medias.map { it.inicio.toString() }
        )
        assertEquals(listOf(60f, 70f, 80f), medias.map { it.mediaBpm })
    }

    @Test
    fun respeitaFusoLocalAoDefinirFaixas() {
        val historico = listOf(leitura("2026-11-08T23:30:00", 72))

        val local = calcularMediasPorQuatroHoras(historico, zoneId).single()
        val utc = calcularMediasPorQuatroHoras(historico, ZoneId.of("UTC")).single()

        assertEquals(LocalDateTime.parse("2026-11-08T20:00"), local.inicio)
        assertEquals(LocalDateTime.parse("2026-11-09T00:00"), utc.inicio)
    }

    @Test
    fun historicoVazioNaoProduzMedias() {
        assertTrue(calcularMediasPorQuatroHoras(emptyList(), zoneId).isEmpty())
    }
}
