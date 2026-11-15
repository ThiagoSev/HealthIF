package com.example.healthif.data.dao

import androidx.room3.Dao
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import androidx.room3.Insert
import com.example.healthif.data.Entities.DailyActivity
import androidx.room3.OnConflictStrategy

@Dao
interface DailyActivityDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(dao: DailyActivity)

    @Query("""
        SELECT * FROM dailyActivity 
        WHERE date = @dateToday
    """)
    fun observarAtividadeHoje(dateToday: Long): Flow<DailyActivity?>

    @Query("""
        SELECT * FROM dailyActivity
        ORDER BY date DESC
    """)
    fun observarHistorico(): Flow<List<DailyActivity>>
}
