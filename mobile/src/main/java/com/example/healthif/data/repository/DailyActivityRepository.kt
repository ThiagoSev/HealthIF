package com.example.healthif.data.repository

import com.example.healthif.data.dao.DailyActivityDAO
import com.example.healthif.data.Entities.DailyActivity
import java.time.LocalDate

class DailyActivityRepository(
    private val dao: DailyActivityDAO
) {
    val today = LocalDate.now().toEpochDay()
    val ultimaAtividade = dao.observarAtividadeHoje(today)
    val historico = dao.observarHistorico()

    suspend fun adicionarAtividadeDiaria(dailyActivity: DailyActivity) {
        dao.inserir(DailyActivity(
            dayEpoch = dailyActivity.dayEpoch,
            steps = dailyActivity.steps,
            calories = dailyActivity.calories,
            distance = dailyActivity.distance,
            updatedAt = dailyActivity.updatedAt
        ))
    }
}