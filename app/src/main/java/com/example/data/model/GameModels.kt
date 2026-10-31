package com.example.data.model

import java.util.UUID

enum class Rarity(val label: String, val colorHex: Long) {
    COMMON("Comum", 0xFF9EACCC),
    RARE("Raro", 0xFF2979FF),
    EPIC("Épico", 0xFFD500F9),
    LEGENDARY("Lendário", 0xFFFFD700)
}

enum class CosmeticType {
    CORE_SKIN,
    TAP_TRAIL,
    PLAYER_TITLE
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val type: CosmeticType,
    val rarity: Rarity,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false
)

data class GeneratorDef(
    val id: String,
    val name: String,
    val baseCost: Double,
    val baseCps: Double,
    val level: Int = 0,
    val iconEmoji: String,
    val description: String
) {
    fun currentCost(costReductionFactor: Double = 1.0): Double {
        val cost = baseCost * Math.pow(1.15, level.toDouble()) * costReductionFactor
        return Math.max(1.0, cost)
    }

    fun currentCps(multiplier: Double = 1.0): Double {
        if (level == 0) return 0.0
        // Tier milestones (25, 50, 100, 200 give 2x, 3x, 4x)
        var milestoneBonus = 1.0
        if (level >= 25) milestoneBonus *= 2.0
        if (level >= 50) milestoneBonus *= 2.0
        if (level >= 100) milestoneBonus *= 3.0
        if (level >= 200) milestoneBonus *= 4.0
        return baseCps * level * milestoneBonus * multiplier
    }
}

data class PrestigeUpgradeDef(
    val id: String,
    val name: String,
    val description: String,
    val baseDarkMatterCost: Long,
    val costMultiplier: Double,
    val level: Int = 0,
    val maxLevel: Int = 20,
    val iconEmoji: String
) {
    fun currentCost(): Long {
        return (baseDarkMatterCost * Math.pow(costMultiplier, level.toDouble())).toLong()
    }
}

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int = 0,
    val rewardGems: Int,
    val isClaimed: Boolean = false,
    val iconEmoji: String
)

data class DuelResult(
    val id: String = UUID.randomUUID().toString(),
    val opponentName: String,
    val opponentMmr: Int,
    val isWin: Boolean,
    val playerTaps: Int,
    val playerCps: Double,
    val playerDamage: Long,
    val critAccuracyPercent: Int,
    val mmrChange: Int,
    val rewardGems: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val senderTitle: String,
    val channel: String, // #Global, #Duelos, #Clã
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystem: Boolean = false,
    val isFiltered: Boolean = false
)

data class LeaderboardPlayer(
    val rank: Int,
    val name: String,
    val title: String,
    val energyTotalFormatted: String,
    val prestigeCount: Int,
    val mmr: Int,
    val isCurrentPlayer: Boolean = false
)
