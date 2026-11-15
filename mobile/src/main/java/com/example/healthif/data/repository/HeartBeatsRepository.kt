package com.example.healthif.data.repository

import com.example.healthif.data.dao.HeartBeatsDao
import com.example.healthif.data.Entities.HeartBeat

class HeartBeatsRepository(
    private val dao: HeartBeatsDao
) {
    val ultimoBatimento = dao.observarUltimo()
    val historico = dao.observarHistorico()

    suspend fun adicionarBatimentos(bpm: Int, measuredAt: Long) {
        dao.inserir(HeartBeat(bpm = bpm, measuredAt = measuredAt))
    }
}