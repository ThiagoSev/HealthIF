package com.example.healthif.ui.feature.initial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthif.data.repository.DailyActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class InitialViewModel (
    private val repository: DailyActivityRepository
) : ViewModel() {
    val lastActivity = repository.ultimaAtividade
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

}