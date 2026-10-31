package com.example.data.repository

import com.example.data.anticheat.AntiCheatEngine
import com.example.data.db.AppDatabase
import com.example.data.db.ChatMessageEntity
import com.example.data.db.DuelRecordEntity
import com.example.data.db.GameSaveEntity
import com.example.data.model.ChatMessage
import com.example.data.model.DuelResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val db: AppDatabase) {

    val gameSaveFlow: Flow<GameSaveEntity?> = db.gameDao().getGameSaveFlow()
    val recentDuelsFlow: Flow<List<DuelResult>> = db.duelDao().getRecentDuels().map { entities ->
        entities.map {
            DuelResult(
                id = it.id,
                opponentName = it.opponentName,
                opponentMmr = it.opponentMmr,
                isWin = it.isWin,
                playerTaps = it.playerTaps,
                playerCps = it.playerCps,
                playerDamage = it.playerDamage,
                critAccuracyPercent = it.critAccuracyPercent,
                mmrChange = it.mmrChange,
                rewardGems = it.rewardGems,
                timestamp = it.timestamp
            )
        }
    }

    val chatMessagesFlow: Flow<List<ChatMessage>> = db.chatDao().getAllMessages().map { entities ->
        entities.map {
            ChatMessage(
                id = it.id,
                senderName = it.senderName,
                senderTitle = it.senderTitle,
                channel = it.channel,
                text = it.text,
                timestamp = it.timestamp,
                isSystem = it.isSystem,
                isFiltered = it.isFiltered
            )
        }
    }

    suspend fun getSaveSync(): GameSaveEntity? = withContext(Dispatchers.IO) {
        db.gameDao().getGameSaveSync()
    }

    suspend fun saveGame(entity: GameSaveEntity) = withContext(Dispatchers.IO) {
        val rawData = "${entity.energy}:${entity.darkMatter}:${entity.prestigeCount}:${entity.lastSavedTimestamp}"
        val checksum = AntiCheatEngine.generateChecksum(rawData)
        db.gameDao().saveGame(entity.copy(saveChecksum = checksum))
    }

    suspend fun recordDuel(result: DuelResult) = withContext(Dispatchers.IO) {
        db.duelDao().insertDuel(
            DuelRecordEntity(
                id = result.id,
                opponentName = result.opponentName,
                opponentMmr = result.opponentMmr,
                isWin = result.isWin,
                playerTaps = result.playerTaps,
                playerCps = result.playerCps,
                playerDamage = result.playerDamage,
                critAccuracyPercent = result.critAccuracyPercent,
                mmrChange = result.mmrChange,
                rewardGems = result.rewardGems,
                timestamp = result.timestamp
            )
        )
    }

    suspend fun insertChatMessage(msg: ChatMessage) = withContext(Dispatchers.IO) {
        db.chatDao().insertMessage(
            ChatMessageEntity(
                id = msg.id,
                senderName = msg.senderName,
                senderTitle = msg.senderTitle,
                channel = msg.channel,
                text = msg.text,
                timestamp = msg.timestamp,
                isSystem = msg.isSystem,
                isFiltered = msg.isFiltered
            )
        )
        db.chatDao().trimOldMessages()
    }
}
