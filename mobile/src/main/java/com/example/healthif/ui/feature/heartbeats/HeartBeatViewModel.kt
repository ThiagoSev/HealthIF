package com.example.healthif.ui.feature.heartbeats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthif.data.repository.HeartBeatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HeartBeatViewModel(
    private val repository: HeartBeatsRepository
) : ViewModel() {
    val batimentoAtual = repository.ultimoBatimento
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val historico = repository.historico
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}