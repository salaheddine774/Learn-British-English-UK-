package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_words")
data class SavedWord(
    @PrimaryKey val word: String,
    val arabicMeaning: String,
    val pronunciation: String,
    val wordType: String,
    val synonyms: String = "",
    val antonyms: String = "",
    val britishUsage: String = "",
    val sampleSentence: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
