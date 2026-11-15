package com.example.healthif.data.Entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "dailyActivity")
data class DailyActivity (
    @PrimaryKey() val dayEpoch: Long,
    @ColumnInfo(name = "steps") val steps: Int?,
    @ColumnInfo(name = "calories") val calories: Int?,
    //distância é em metros
    @ColumnInfo(name = "distance") val distance: Int?,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long
)