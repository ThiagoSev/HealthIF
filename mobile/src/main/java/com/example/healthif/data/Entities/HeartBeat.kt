package com.example.healthif.data.Entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ColumnInfo

@Entity(tableName = "heartBeat")
data class HeartBeat(
    @PrimaryKey() val measuredAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "bpm") val bpm: Int
)