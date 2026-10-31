package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val energy: Double = 0.0,
    val lifetimeEnergy: Double = 0.0,
    val darkMatter: Long = 0L,
    val gems: Int = 50,
    val prestigeCount: Int = 0,
    val playerMmr: Int = 1000,
    val playerName: String = "Comandante Astro",
    val equippedSkinId: String = "skin_neon",
    val equippedTrailId: String = "trail_stars",
    val equippedTitleId: String = "title_cadet",
    val generatorLevelsMap: String = "", // e.g. "gen_probe:0,gen_harvester:0"
    val prestigeUpgradesMap: String = "",
    val unlockedCosmeticsList: String = "skin_neon,trail_stars,title_cadet",
    val dailyQuestsData: String = "",
    val seasonPoints: Int = 120,
    val isSeasonPassPremium: Boolean = false,
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val cloudSyncTimestamp: Long = 0L,
    val boostActiveUntil: Long = 0L,
    val saveChecksum: String = ""
)

@Entity(tableName = "duel_records")
data class DuelRecordEntity(
    @PrimaryKey val id: String,
    val opponentName: String,
    val opponentMmr: Int,
    val isWin: Boolean,
    val playerTaps: Int,
    val playerCps: Double,
    val playerDamage: Long,
    val critAccuracyPercent: Int,
    val mmrChange: Int,
    val rewardGems: Int,
    val timestamp: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderName: String,
    val senderTitle: String,
    val channel: String,
    val text: String,
    val timestamp: Long,
    val isSystem: Boolean,
    val isFiltered: Boolean
)
