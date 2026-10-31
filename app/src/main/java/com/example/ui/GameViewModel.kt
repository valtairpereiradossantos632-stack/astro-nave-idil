package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.anticheat.AntiCheatEngine
import com.example.data.db.AppDatabase
import com.example.data.db.GameSaveEntity
import com.example.data.model.*
import com.example.data.repository.GameRepository
import com.example.util.FormatUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

data class TapParticle(
    val id: Long = System.nanoTime(),
    val text: String,
    val isCrit: Boolean,
    val xOffset: Float,
    val yOffset: Float
)

sealed class LootBoxState {
    object Closed : LootBoxState()
    object Opening : LootBoxState()
    data class Opened(val item: CosmeticItem, val isDuplicate: Boolean, val refundGems: Int) : LootBoxState()
}

sealed class AdRewardType {
    object DoubleBoost : AdRewardType()
    object CometSurprise : AdRewardType()
    object LootBoxKey : AdRewardType()
}

data class DuelActiveState(
    val opponentName: String,
    val opponentTitle: String,
    val opponentMmr: Int,
    val durationSeconds: Int = 15,
    val remainingSeconds: Int = 15,
    val playerTaps: Int = 0,
    val opponentTaps: Int = 0,
    val playerPower: Double = 50.0 // 0 to 100 tug of war
)

data class GameUiState(
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
    val generators: List<GeneratorDef> = GameDefinitions.DEFAULT_GENERATORS,
    val prestigeUpgrades: List<PrestigeUpgradeDef> = GameDefinitions.DEFAULT_PRESTIGE_UPGRADES,
    val cosmetics: List<CosmeticItem> = GameDefinitions.ALL_COSMETICS,
    val dailyQuests: List<DailyQuest> = GameDefinitions.DEFAULT_DAILY_QUESTS,
    val seasonPoints: Int = 150,
    val isSeasonPassPremium: Boolean = false,
    val boostActiveUntil: Long = 0L,
    val selectedTab: Int = 0,
    val tapParticles: List<TapParticle> = emptyList(),
    val offlineRewardEarned: Double = 0.0,
    val offlineSecondsAway: Long = 0L,
    val showOfflineDialog: Boolean = false,
    val isCometActive: Boolean = false,
    val cometBonusMultiplier: Double = 1.0,
    val lootBoxState: LootBoxState = LootBoxState.Closed,
    val activeAdReward: AdRewardType? = null,
    val adCountdownSeconds: Int = 0,
    val isAdModalOpen: Boolean = false,
    val isMatchmaking: Boolean = false,
    val activeDuel: DuelActiveState? = null,
    val lastDuelReport: DuelResult? = null,
    val antiCheatNotice: String = "",
    val cloudSyncMessage: String = "",
    val isVoiceMuted: Boolean = false,
    val isVoiceConnected: Boolean = true
) {
    val totalCps: Double
        get() {
            var resonanceMultiplier = 1.0
            val resonanceLevel = prestigeUpgrades.find { it.id == "p_resonance" }?.level ?: 0
            resonanceMultiplier += resonanceLevel * 0.25

            val isBoosted = boostActiveUntil > System.currentTimeMillis()
            val boostMult = if (isBoosted) 2.0 else 1.0

            val baseSum = generators.sumOf { it.currentCps(1.0) }
            return baseSum * resonanceMultiplier * boostMult
        }

    val costReductionFactor: Double
        get() {
            val effLevel = prestigeUpgrades.find { it.id == "p_efficiency" }?.level ?: 0
            return max(0.4, 1.0 - (effLevel * 0.05))
        }

    val critDamageMultiplier: Double
        get() {
            val critLevel = prestigeUpgrades.find { it.id == "p_crit_power" }?.level ?: 0
            return 2.0 + (critLevel * 0.5)
        }

    val maxOfflineHours: Long
        get() {
            val vaultLevel = prestigeUpgrades.find { it.id == "p_offline_vault" }?.level ?: 0
            return 8L + (vaultLevel * 2L)
        }

    val prestigeGainOnReset: Long
        get() {
            if (lifetimeEnergy < 100000.0) return 0L
            return floor(sqrt(lifetimeEnergy / 100000.0)).toLong()
        }

    val isBoostActive: Boolean
        get() = boostActiveUntil > System.currentTimeMillis()
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val recentDuels = repository.recentDuelsFlow
    val chatMessages = repository.chatMessagesFlow

    private var duelJob: Job? = null
    private var tickerJob: Job? = null
    private var cometTimerJob: Job? = null

    init {
        loadInitialState()
        startIdleTicker()
        startCometScheduler()
        seedInitialChatIfEmpty()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val save = repository.getSaveSync()
            if (save != null) {
                // Parse saved generators
                val genMap = save.generatorLevelsMap.split(",")
                    .filter { it.contains(":") }
                    .associate {
                        val parts = it.split(":")
                        parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                    }
                val loadedGenerators = GameDefinitions.DEFAULT_GENERATORS.map {
                    it.copy(level = genMap[it.id] ?: 0)
                }

                // Parse prestige upgrades
                val pMap = save.prestigeUpgradesMap.split(",")
                    .filter { it.contains(":") }
                    .associate {
                        val parts = it.split(":")
                        parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                    }
                val loadedPrestige = GameDefinitions.DEFAULT_PRESTIGE_UPGRADES.map {
                    it.copy(level = pMap[it.id] ?: 0)
                }

                // Parse unlocked cosmetics
                val unlockedIds = save.unlockedCosmeticsList.split(",").toSet()
                val loadedCosmetics = GameDefinitions.ALL_COSMETICS.map {
                    it.copy(
                        isUnlocked = unlockedIds.contains(it.id) || it.id in listOf("skin_neon", "trail_stars", "title_cadet"),
                        isEquipped = it.id in listOf(save.equippedSkinId, save.equippedTrailId, save.equippedTitleId)
                    )
                }

                // Offline progression calculation
                val now = System.currentTimeMillis()
                val (isClockValid, clockMsg) = AntiCheatEngine.validateTimestamp(save.lastSavedTimestamp, now)
                var offlineEarned = 0.0
                var secondsAway = 0L

                val vaultLevel = loadedPrestige.find { it.id == "p_offline_vault" }?.level ?: 0
                val maxOfflineSec = (8L + vaultLevel * 2L) * 3600L

                if (isClockValid && save.lastSavedTimestamp > 0) {
                    val rawElapsed = (now - save.lastSavedTimestamp) / 1000L
                    secondsAway = rawElapsed.coerceIn(0L, maxOfflineSec)
                    if (secondsAway > 60L) { // at least 1 minute away to trigger bonus
                        val resonanceLevel = loadedPrestige.find { it.id == "p_resonance" }?.level ?: 0
                        val resMult = 1.0 + (resonanceLevel * 0.25)
                        val baseCps = loadedGenerators.sumOf { it.currentCps(1.0) } * resMult
                        offlineEarned = secondsAway * baseCps * 0.75 // 75% offline efficiency base
                    }
                }

                _uiState.update { current ->
                    current.copy(
                        energy = save.energy + offlineEarned,
                        lifetimeEnergy = save.lifetimeEnergy + offlineEarned,
                        darkMatter = save.darkMatter,
                        gems = save.gems,
                        prestigeCount = save.prestigeCount,
                        playerMmr = save.playerMmr,
                        playerName = save.playerName,
                        equippedSkinId = save.equippedSkinId,
                        equippedTrailId = save.equippedTrailId,
                        equippedTitleId = save.equippedTitleId,
                        generators = loadedGenerators,
                        prestigeUpgrades = loadedPrestige,
                        cosmetics = loadedCosmetics,
                        seasonPoints = save.seasonPoints,
                        isSeasonPassPremium = save.isSeasonPassPremium,
                        boostActiveUntil = save.boostActiveUntil,
                        offlineRewardEarned = offlineEarned,
                        offlineSecondsAway = secondsAway,
                        showOfflineDialog = offlineEarned > 1.0,
                        antiCheatNotice = clockMsg
                    )
                }
            } else {
                // First launch save
                persistCurrentState()
            }
        }
    }

    private fun startIdleTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val state = _uiState.value
                val cps = state.totalCps
                if (cps > 0.0) {
                    _uiState.update {
                        it.copy(
                            energy = it.energy + cps,
                            lifetimeEnergy = it.lifetimeEnergy + cps
                        )
                    }
                }
                // Periodic auto-save every 10 seconds
                if (System.currentTimeMillis() % 10000 < 1100) {
                    persistCurrentState()
                }
            }
        }
    }

    private fun startCometScheduler() {
        cometTimerJob?.cancel()
        cometTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(Random.nextLong(30000L, 60000L))
                _uiState.update { it.copy(isCometActive = true) }
                delay(12000L) // Comet stays for 12 seconds
                _uiState.update { it.copy(isCometActive = false) }
            }
        }
    }

    fun onCoreClick(): TapParticle {
        val (isLegit, status) = AntiCheatEngine.validateTap()
        if (!isLegit) {
            _uiState.update { it.copy(antiCheatNotice = status.anomalyMessage) }
        } else if (_uiState.value.antiCheatNotice.isNotEmpty()) {
            _uiState.update { it.copy(antiCheatNotice = "") }
        }

        val state = _uiState.value
        val isCrit = Random.nextDouble() < 0.15 // 15% base crit
        val baseDamage = 1.0 + (state.totalCps * 0.04)
        val boost = if (state.isBoostActive) 2.0 else 1.0
        val finalDamage = if (isCrit) {
            baseDamage * state.critDamageMultiplier * boost
        } else {
            baseDamage * boost
        }

        val particle = TapParticle(
            text = "+${FormatUtil.formatNumber(finalDamage)}${if (isCrit) " CRÍT!" else ""}",
            isCrit = isCrit,
            xOffset = Random.nextFloat() * 120f - 60f,
            yOffset = Random.nextFloat() * -80f - 40f
        )

        _uiState.update { current ->
            val updatedQuests = current.dailyQuests.map { q ->
                if (q.id == "q_clicks" && q.current < q.target) {
                    q.copy(current = q.current + 1)
                } else q
            }
            val recentParticles = (current.tapParticles + particle).takeLast(6)
            current.copy(
                energy = current.energy + finalDamage,
                lifetimeEnergy = current.lifetimeEnergy + finalDamage,
                tapParticles = recentParticles,
                dailyQuests = updatedQuests
            )
        }
        return particle
    }

    fun catchComet() {
        val state = _uiState.value
        val bonus = max(100.0, state.totalCps * 60.0) // 1 minute of CPS
        val gemsBonus = Random.nextInt(5, 15)

        _uiState.update { current ->
            val updatedQuests = current.dailyQuests.map { q ->
                if (q.id == "q_comet") q.copy(current = minOf(q.target, q.current + 1)) else q
            }
            current.copy(
                isCometActive = false,
                energy = current.energy + bonus,
                lifetimeEnergy = current.lifetimeEnergy + bonus,
                gems = current.gems + gemsBonus,
                dailyQuests = updatedQuests
            )
        }
    }

    fun buyGenerator(generatorId: String, multiplierType: Int = 1) { // 1 = 1x, 10 = 10x
        _uiState.update { current ->
            val gen = current.generators.find { it.id == generatorId } ?: return@update current
            var cost = 0.0
            var boughtCount = 0

            var tempLevel = gen.level
            val countToBuy = if (multiplierType == 10) 10 else 1

            for (i in 0 until countToBuy) {
                val nextCost = gen.baseCost * Math.pow(1.15, tempLevel.toDouble()) * current.costReductionFactor
                if (current.energy >= cost + nextCost) {
                    cost += nextCost
                    tempLevel++
                    boughtCount++
                } else break
            }

            if (boughtCount == 0) return@update current

            val updatedGenerators = current.generators.map {
                if (it.id == generatorId) it.copy(level = tempLevel) else it
            }

            val updatedQuests = current.dailyQuests.map { q ->
                if (q.id == "q_upgrades") q.copy(current = minOf(q.target, q.current + boughtCount)) else q
            }

            current.copy(
                energy = current.energy - cost,
                generators = updatedGenerators,
                dailyQuests = updatedQuests
            )
        }
        persistCurrentState()
    }

    fun buyPrestigeUpgrade(upgradeId: String) {
        _uiState.update { current ->
            val p = current.prestigeUpgrades.find { it.id == upgradeId } ?: return@update current
            if (p.level >= p.maxLevel) return@update current
            val cost = p.currentCost()
            if (current.darkMatter < cost) return@update current

            val updatedP = current.prestigeUpgrades.map {
                if (it.id == upgradeId) it.copy(level = it.level + 1) else it
            }
            current.copy(
                darkMatter = current.darkMatter - cost,
                prestigeUpgrades = updatedP
            )
        }
        persistCurrentState()
    }

    fun performPrestige() {
        val state = _uiState.value
        val gain = state.prestigeGainOnReset
        if (gain <= 0L) return

        _uiState.update { current ->
            val resetGenerators = GameDefinitions.DEFAULT_GENERATORS
            current.copy(
                energy = 0.0,
                darkMatter = current.darkMatter + gain,
                prestigeCount = current.prestigeCount + 1,
                generators = resetGenerators
            )
        }
        persistCurrentState()
    }

    fun openLootBox(useGems: Boolean) {
        val state = _uiState.value
        val cost = 25
        if (useGems && state.gems < cost) return

        _uiState.update { current ->
            current.copy(
                gems = if (useGems) current.gems - cost else current.gems,
                lootBoxState = LootBoxState.Opening
            )
        }

        viewModelScope.launch {
            delay(1200L) // dramatic opening suspense
            val roll = Random.nextDouble()
            val targetRarity = when {
                roll < 0.05 -> Rarity.LEGENDARY // 5%
                roll < 0.20 -> Rarity.EPIC // 15%
                roll < 0.50 -> Rarity.RARE // 30%
                else -> Rarity.COMMON // 50%
            }

            val matchingItems = GameDefinitions.ALL_COSMETICS.filter { it.rarity == targetRarity }
            val chosen = matchingItems.random()

            val alreadyUnlocked = _uiState.value.cosmetics.find { it.id == chosen.id }?.isUnlocked == true
            val refund = if (alreadyUnlocked) 15 else 0

            _uiState.update { current ->
                val updatedCosmetics = current.cosmetics.map {
                    if (it.id == chosen.id) it.copy(isUnlocked = true) else it
                }
                current.copy(
                    gems = current.gems + refund,
                    cosmetics = updatedCosmetics,
                    lootBoxState = LootBoxState.Opened(chosen, alreadyUnlocked, refund)
                )
            }
            persistCurrentState()
        }
    }

    fun closeLootBoxModal() {
        _uiState.update { it.copy(lootBoxState = LootBoxState.Closed) }
    }

    fun equipCosmetic(cosmeticId: String) {
        _uiState.update { current ->
            val item = current.cosmetics.find { it.id == cosmeticId } ?: return@update current
            if (!item.isUnlocked) return@update current

            val updatedList = current.cosmetics.map {
                if (it.type == item.type) {
                    it.copy(isEquipped = it.id == item.id)
                } else it
            }

            current.copy(
                cosmetics = updatedList,
                equippedSkinId = if (item.type == CosmeticType.CORE_SKIN) item.id else current.equippedSkinId,
                equippedTrailId = if (item.type == CosmeticType.TAP_TRAIL) item.id else current.equippedTrailId,
                equippedTitleId = if (item.type == CosmeticType.PLAYER_TITLE) item.id else current.equippedTitleId
            )
        }
        persistCurrentState()
    }

    fun startRewardedAd(rewardType: AdRewardType) {
        _uiState.update {
            it.copy(
                activeAdReward = rewardType,
                isAdModalOpen = true,
                adCountdownSeconds = 5 // 5 second simulated interactive ad
            )
        }

        viewModelScope.launch {
            for (i in 5 downTo 1) {
                delay(1000L)
                _uiState.update { it.copy(adCountdownSeconds = i - 1) }
            }
        }
    }

    fun claimRewardedAdReward() {
        val state = _uiState.value
        val reward = state.activeAdReward ?: return

        when (reward) {
            AdRewardType.DoubleBoost -> {
                val now = System.currentTimeMillis()
                val currentBoostEnd = max(now, state.boostActiveUntil)
                val newBoostEnd = currentBoostEnd + (2 * 3600 * 1000L) // +2 hours
                _uiState.update {
                    it.copy(
                        boostActiveUntil = newBoostEnd,
                        isAdModalOpen = false,
                        activeAdReward = null
                    )
                }
            }
            AdRewardType.CometSurprise -> {
                _uiState.update {
                    it.copy(
                        isCometActive = true,
                        gems = it.gems + 20,
                        isAdModalOpen = false,
                        activeAdReward = null
                    )
                }
            }
            AdRewardType.LootBoxKey -> {
                _uiState.update {
                    it.copy(
                        gems = it.gems + 30,
                        isAdModalOpen = false,
                        activeAdReward = null
                    )
                }
            }
        }
        persistCurrentState()
    }

    fun dismissAdModal() {
        _uiState.update { it.copy(isAdModalOpen = false, activeAdReward = null) }
    }

    fun startDuelMatchmaking() {
        _uiState.update { it.copy(isMatchmaking = true, lastDuelReport = null) }

        viewModelScope.launch {
            delay(1800L) // searching opponent by MMR
            val currentMmr = _uiState.value.playerMmr
            val opponentMmr = (currentMmr + Random.nextInt(-40, 45)).coerceAtLeast(800)
            val opponentNames = listOf("NovaHunter", "VortexRider", "SolarTitan", "StarlightX", "CosmoAce", "SingularPlayer", "QuantumWolf")
            val opponentTitles = listOf("Gladiador Astral", "Lorde da Fenda", "Comandante Estelar", "Viajante do Vazio")

            val active = DuelActiveState(
                opponentName = opponentNames.random(),
                opponentTitle = opponentTitles.random(),
                opponentMmr = opponentMmr,
                durationSeconds = 12,
                remainingSeconds = 12,
                playerTaps = 0,
                opponentTaps = 0,
                playerPower = 50.0
            )

            _uiState.update { it.copy(isMatchmaking = false, activeDuel = active) }
            runDuelBattleLoop()
        }
    }

    fun onDuelTap() {
        _uiState.update { current ->
            val duel = current.activeDuel ?: return@update current
            val newPlayerTaps = duel.playerTaps + 1
            val newPower = (duel.playerPower + 2.5).coerceIn(0.0, 100.0)
            current.copy(activeDuel = duel.copy(playerTaps = newPlayerTaps, playerPower = newPower))
        }
    }

    private fun runDuelBattleLoop() {
        duelJob?.cancel()
        duelJob = viewModelScope.launch {
            for (sec in 12 downTo 1) {
                delay(1000L)
                _uiState.update { current ->
                    val duel = current.activeDuel ?: return@update current
                    // Simulated opponent taps according to MMR
                    val opponentRate = Random.nextInt(5, 9)
                    val newOppTaps = duel.opponentTaps + opponentRate
                    val powerShift = (duel.playerPower - (opponentRate * 0.4)).coerceIn(0.0, 100.0)
                    current.copy(
                        activeDuel = duel.copy(
                            remainingSeconds = sec - 1,
                            opponentTaps = newOppTaps,
                            playerPower = powerShift
                        )
                    )
                }
            }

            // Duel finished!
            val duel = _uiState.value.activeDuel ?: return@launch
            val isWin = duel.playerPower >= 50.0
            val mmrChange = if (isWin) 25 else -15
            val gemsReward = if (isWin) 30 else 5
            val cps = duel.playerTaps / 12.0
            val totalDamage = (duel.playerTaps * 450L)
            val critAccuracy = Random.nextInt(15, 35)

            val result = DuelResult(
                opponentName = duel.opponentName,
                opponentMmr = duel.opponentMmr,
                isWin = isWin,
                playerTaps = duel.playerTaps,
                playerCps = cps,
                playerDamage = totalDamage,
                critAccuracyPercent = critAccuracy,
                mmrChange = mmrChange,
                rewardGems = gemsReward
            )

            repository.recordDuel(result)

            _uiState.update { current ->
                val updatedQuests = current.dailyQuests.map { q ->
                    if (q.id == "q_duel") q.copy(current = minOf(q.target, q.current + 1)) else q
                }
                current.copy(
                    activeDuel = null,
                    lastDuelReport = result,
                    playerMmr = max(0, current.playerMmr + mmrChange),
                    gems = current.gems + gemsReward,
                    dailyQuests = updatedQuests
                )
            }
            persistCurrentState()
        }
    }

    fun dismissDuelReport() {
        _uiState.update { it.copy(lastDuelReport = null) }
    }

    fun claimDailyQuest(questId: String) {
        _uiState.update { current ->
            val q = current.dailyQuests.find { it.id == questId } ?: return@update current
            if (q.current < q.target || q.isClaimed) return@update current

            val updated = current.dailyQuests.map {
                if (it.id == questId) it.copy(isClaimed = true) else it
            }
            current.copy(
                gems = current.gems + q.rewardGems,
                dailyQuests = updated,
                seasonPoints = current.seasonPoints + 25
            )
        }
        persistCurrentState()
    }

    fun closeOfflineDialog(doubleWithAd: Boolean = false) {
        val earned = _uiState.value.offlineRewardEarned
        if (doubleWithAd) {
            _uiState.update {
                it.copy(
                    energy = it.energy + earned, // award 1x more
                    lifetimeEnergy = it.lifetimeEnergy + earned,
                    showOfflineDialog = false,
                    offlineRewardEarned = 0.0
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    showOfflineDialog = false,
                    offlineRewardEarned = 0.0
                )
            }
        }
        persistCurrentState()
    }

    fun sendChatMessage(text: String, channel: String = "#Global") {
        if (text.isBlank()) return
        val (cleanText, wasFiltered) = FormatUtil.filterToxicity(text)
        val state = _uiState.value

        val msg = ChatMessage(
            senderName = state.playerName,
            senderTitle = state.cosmetics.find { it.id == state.equippedTitleId }?.name ?: "Cadete Espacial",
            channel = channel,
            text = cleanText,
            isFiltered = wasFiltered
        )

        viewModelScope.launch {
            repository.insertChatMessage(msg)
            // Simulated player reply after 2-4 seconds
            delay(Random.nextLong(2000L, 4000L))
            val peerNames = listOf("AstroNave", "StarQueen", "ZeroKelvin", "Orion99", "Eclipse")
            val peerReplies = listOf(
                "Boa jogada! Como está seu CPS?",
                "Alguém afim de um duelo na arena?",
                "Acabei de fazer meu 3º prestígio, a produção disparou!",
                "Fica esperto com os cometas surpresa, eles dão muita gema!",
                "Esse jogo tá viciante demais!"
            )
            val peerMsg = ChatMessage(
                senderName = peerNames.random(),
                senderTitle = "Explorador",
                channel = channel,
                text = peerReplies.random(),
                isFiltered = false
            )
            repository.insertChatMessage(peerMsg)
        }
    }

    fun toggleVoiceMute() {
        _uiState.update { it.copy(isVoiceMuted = !it.isVoiceMuted) }
    }

    fun saveToCloud() {
        viewModelScope.launch {
            persistCurrentState()
            delay(600L)
            _uiState.update {
                it.copy(cloudSyncMessage = "✅ Progresso salvo na AstroCloud com sucesso! ID: ASTRO-${Random.nextInt(1000, 9999)}")
            }
        }
    }

    fun exportSaveCode(): String {
        val s = _uiState.value
        val raw = "${s.energy.toLong()}:${s.darkMatter}:${s.prestigeCount}:${s.playerMmr}:${s.gems}"
        val checksum = AntiCheatEngine.generateChecksum(raw)
        return "ASTRO_SAVE_V1:$raw:$checksum"
    }

    fun importSaveCode(code: String): Boolean {
        try {
            if (!code.startsWith("ASTRO_SAVE_V1:")) return false
            val parts = code.removePrefix("ASTRO_SAVE_V1:").split(":")
            if (parts.size < 6) return false
            val raw = "${parts[0]}:${parts[1]}:${parts[2]}:${parts[3]}:${parts[4]}"
            val expectedHash = parts[5]
            if (!AntiCheatEngine.verifyChecksum(raw, expectedHash)) {
                _uiState.update { it.copy(antiCheatNotice = "⚠️ Código de save inválido ou adulterado!") }
                return false
            }

            val e = parts[0].toDoubleOrNull() ?: 0.0
            val dm = parts[1].toLongOrNull() ?: 0L
            val p = parts[2].toIntOrNull() ?: 0
            val mmr = parts[3].toIntOrNull() ?: 1000
            val g = parts[4].toIntOrNull() ?: 50

            _uiState.update {
                it.copy(
                    energy = e,
                    lifetimeEnergy = max(it.lifetimeEnergy, e),
                    darkMatter = dm,
                    prestigeCount = p,
                    playerMmr = mmr,
                    gems = g,
                    cloudSyncMessage = "✅ Save multiplataforma importado com sucesso!"
                )
            }
            persistCurrentState()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    private fun persistCurrentState() {
        val state = _uiState.value
        viewModelScope.launch {
            val genLevels = state.generators.joinToString(",") { "${it.id}:${it.level}" }
            val pLevels = state.prestigeUpgrades.joinToString(",") { "${it.id}:${it.level}" }
            val unlockedCosmetics = state.cosmetics.filter { it.isUnlocked }.joinToString(",") { it.id }

            val entity = GameSaveEntity(
                id = 1,
                energy = state.energy,
                lifetimeEnergy = state.lifetimeEnergy,
                darkMatter = state.darkMatter,
                gems = state.gems,
                prestigeCount = state.prestigeCount,
                playerMmr = state.playerMmr,
                playerName = state.playerName,
                equippedSkinId = state.equippedSkinId,
                equippedTrailId = state.equippedTrailId,
                equippedTitleId = state.equippedTitleId,
                generatorLevelsMap = genLevels,
                prestigeUpgradesMap = pLevels,
                unlockedCosmeticsList = unlockedCosmetics,
                dailyQuestsData = "",
                seasonPoints = state.seasonPoints,
                isSeasonPassPremium = state.isSeasonPassPremium,
                lastSavedTimestamp = System.currentTimeMillis(),
                cloudSyncTimestamp = System.currentTimeMillis(),
                boostActiveUntil = state.boostActiveUntil
            )
            repository.saveGame(entity)
        }
    }

    private fun seedInitialChatIfEmpty() {
        viewModelScope.launch {
            delay(500L)
            repository.insertChatMessage(
                ChatMessage(
                    senderName = "AstroBot",
                    senderTitle = "Sistema",
                    channel = "#Global",
                    text = "Bem-vindo ao Chat Galáctico! Ambiente moderado com filtro de toxicidade ativo.",
                    isSystem = true
                )
            )
            repository.insertChatMessage(
                ChatMessage(
                    senderName = "Comandante Rex",
                    senderTitle = "Lorde Cósmico",
                    channel = "#Global",
                    text = "Dica: Economizem Matéria Escura para 'Ressonância Cósmica', dá muito boost!",
                    isSystem = false
                )
            )
        }
    }
}
