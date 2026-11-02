package com.example.healthif.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthif.repository.HeartBeatsRepository
import com.example.healthif.repository.PreferenciasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer

class PreferenciasViewModel(
    private val repo: PreferenciasRepository,
    repository: HeartBeatsRepository
) : ViewModel() {

    //Conversão de Flow para StateFlow com stateIn
    val escuro  = repo.modoEscuro.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    fun toggleEscuro(ativo: Boolean)   {
        viewModelScope.launch {
            repo.setModoEscuro(ativo)
        }
    }

    companion object {
        fun factory(
            repo: PreferenciasRepository,
            repository: HeartBeatsRepository
        ) = viewModelFactory {
            initializer {
                PreferenciasViewModel(repo, repository)
            }
        }
    }
}


