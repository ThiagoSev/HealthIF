package com.example.healthif

import android.app.Application
import com.example.healthif.repository.HeartBeatsRepository
import kotlin.getValue

class MeuApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val batimentosRepository: HeartBeatsRepository by lazy {
        HeartBeatsRepository(database.heartBeatDao())
    }
}