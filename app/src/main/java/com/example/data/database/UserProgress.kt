package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xpPoints: Int = 0,
    val streakDays: Int = 0,
    val lastActiveTimestamp: Long = 0,
    val completedLessons: String = "" // Comma-separated list of finished lesson keys
)
