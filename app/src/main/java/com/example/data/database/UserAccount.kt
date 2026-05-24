package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val username: String,
    val email: String,
    val passwordHash: String,
    val xpPoints: Int = 0,
    val streakDays: Int = 0,
    val lastActiveTimestamp: Long = 0,
    val completedLessons: String = "" // Comma-separated lessons list
)

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getAccountByUsername(username: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccount)

    @Query("SELECT * FROM user_accounts ORDER BY xpPoints DESC")
    fun getAllAccountsFlow(): Flow<List<UserAccount>>
}
