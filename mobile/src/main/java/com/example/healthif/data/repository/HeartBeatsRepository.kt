package com.example.healthif.data.repository

import com.example.healthif.data.DAO.HeartBeatsDao
import com.example.healthif.data.Entities.HeartBeat
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