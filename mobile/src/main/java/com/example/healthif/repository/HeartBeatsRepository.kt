package com.example.healthif.repository

import com.example.healthif.DAO.HeartBeatsDao
import com.example.healthif.Entities.HeartBeat
import kotlinx.coroutines.flow.Flow

class HeartBeatsRepository(
    private val dao: HeartBeatsDao
) {
    val ultimoBatimento = dao.observarUltimo()
    val historico = dao.observarHistorico()

    suspend fun adicionarBatimentos(bpm: Int) {
        dao.inserir(HeartBeat(bpm = bpm))
    }
}