package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_save WHERE id = 1 LIMIT 1")
    fun getGameSaveFlow(): Flow<GameSaveEntity?>

    @Query("SELECT * FROM game_save WHERE id = 1 LIMIT 1")
    suspend fun getGameSaveSync(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(entity: GameSaveEntity)
}

@Dao
interface DuelDao {
    @Query("SELECT * FROM duel_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentDuels(): Flow<List<DuelRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuel(record: DuelRecordEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC LIMIT 100")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id NOT IN (SELECT id FROM chat_messages ORDER BY timestamp DESC LIMIT 100)")
    suspend fun trimOldMessages()
}
