package com.sambinelli.datastore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sambinelli.datastore.repository.PreferenciasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenciasViewModel(
    private val repo: PreferenciasRepository
) : ViewModel() {

    //Conversão de Flow para StateFlow com stateIn
    val escuro  = repo.modoEscuro.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleEscuro(ativo: Boolean)   {
        viewModelScope.launch {
            repo.setModoEscuro(ativo)
        }
    }
}


