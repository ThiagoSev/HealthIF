package com.example.healthif.data.Entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ColumnInfo

@Entity(tableName = "batimentos")
data class HeartBeat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "bpm") val bpm: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)