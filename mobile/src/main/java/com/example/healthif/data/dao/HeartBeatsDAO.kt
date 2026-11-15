package com.example.healthif.data.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.example.healthif.data.Entities.HeartBeat
import kotlinx.coroutines.flow.Flow
import androidx.room3.Insert

@Dao
interface HeartBeatsDao{
    @Query("SELECT * FROM heartBeat")
    suspend fun getAll(): List<HeartBeat>

    @Insert
    suspend fun inserir(dao: HeartBeat)

    @Query("""
        SELECT * FROM heartBeat 
        ORDER BY measuredAt
        DESC LIMIT 1
    """)
    fun observarUltimo(): Flow<HeartBeat?>

    @Query("""
        SELECT * FROM heartBeat
        ORDER BY measuredAt ASC
    """)
    fun observarHistorico(): Flow<List<HeartBeat>>
}
