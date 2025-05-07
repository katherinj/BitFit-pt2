package com.example.bitfit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val hours: Float,
    val mood: Int,
    val notes: String
)
