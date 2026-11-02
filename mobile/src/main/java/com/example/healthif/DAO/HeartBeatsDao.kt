package com.example.healthif.DAO

import androidx.room3.Dao
import androidx.room3.Query
import com.example.healthif.Entities.HeartBeat
import kotlinx.coroutines.flow.Flow
import androidx.room3.Insert

@Dao
interface HeartBeatsDao{
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<HeartBeat>

    @Insert
    suspend fun inserir(dao: HeartBeat): List<HeartBeat>

    @Query("""
        SELECT * FROM batimentos 
        ORDER BY data 
        DESC LIMIT 1
    """)
    fun observarUltimo(): Flow<HeartBeat?>

    @Query("""
        SELECT * FROM batimentos
        ORDER BY timestamp ASC
    """)
    fun observarHistorico(): Flow<List<HeartBeat>>
}