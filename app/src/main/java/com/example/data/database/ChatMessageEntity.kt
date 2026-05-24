package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String, // "tutor_chat", "friends_group", "job_interview", etc.
    val role: String, // "user" or "model"
    val content: String,
    val arabicExplanation: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
