package com.sambinelli.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "healthIF")

class PreferenciasRepository(private val dataStore: DataStore<Preferences>) {

    //Chaves é um objeto que agrupa todas as keys usadas pelo DataStore
    object Chaves {
        val MODO_ESCURO   = booleanPreferencesKey("modo_escuro")
    }

    //sempre que o dataStore sofrer modificacoes, o flow notifica
    val modoEscuro: Flow<Boolean>  = dataStore.data.map { it[Chaves.MODO_ESCURO]  ?: false }

    suspend fun setModoEscuro(ativo: Boolean) = dataStore.edit {
        it[Chaves.MODO_ESCURO] = ativo
    }
}


