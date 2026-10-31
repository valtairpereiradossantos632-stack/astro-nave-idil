package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AdRewardType
import com.example.ui.GameViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button on sub-tabs
    BackHandler(enabled = state.selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBgDark),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopNavBar(
                state = state,
                onWatchBoostAd = { viewModel.startRewardedAd(AdRewardType.DoubleBoost) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CosmicSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.TouchApp, contentDescription = "Núcleo") },
                    label = { Text("Núcleo", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.RocketLaunch, contentDescription = "Melhorias") },
                    label = { Text("Upgrades", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = state.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.SportsMartialArts, contentDescription = "Duelos") },
                    label = { Text("Duelos", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = state.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.AllInclusive, contentDescription = "Prestígio") },
                    label = { Text("Prestígio", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonPurple,
                        indicatorColor = NeonPurple,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = state.selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Diamond, contentDescription = "Loja") },
                    label = { Text("Cápsulas", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CosmicGold,
                        indicatorColor = CosmicGold,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = state.selectedTab == 5,
                    onClick = { viewModel.selectTab(5) },
                    icon = { Icon(Icons.Default.Public, contentDescription = "Comunidade") },
                    label = { Text("Rede", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicBgDark)
                .padding(innerPadding)
        ) {
            when (state.selectedTab) {
                0 -> CoreScreen(
                    state = state,
                    onCoreClick = { viewModel.onCoreClick() },
                    onCatchComet = { viewModel.catchComet() },
                    onQuickBuy = { viewModel.buyGenerator(it, 1) },
                    onWatchBoostAd = { viewModel.startRewardedAd(it) }
                )
                1 -> UpgradesScreen(
                    state = state,
                    onBuyGenerator = { id, mult -> viewModel.buyGenerator(id, mult) }
                )
                2 -> DuelArenaScreen(
                    state = state,
                    recentDuelsFlow = viewModel.recentDuels,
                    onStartMatchmaking = { viewModel.startDuelMatchmaking() },
                    onDuelTap = { viewModel.onDuelTap() }
                )
                3 -> PrestigeSeasonScreen(
                    state = state,
                    onPerformPrestige = { viewModel.performPrestige() },
                    onBuyPrestigeUpgrade = { viewModel.buyPrestigeUpgrade(it) },
                    onClaimQuest = { viewModel.claimDailyQuest(it) }
                )
                4 -> CosmeticsShopScreen(
                    state = state,
                    onOpenLootBox = { useGems -> viewModel.openLootBox(useGems) },
                    onEquipCosmetic = { viewModel.equipCosmetic(it) },
                    onWatchAdReward = { viewModel.startRewardedAd(it) }
                )
                5 -> CommunitySupportScreen(
                    state = state,
                    chatMessagesFlow = viewModel.chatMessages,
                    onSendMessage = { text, ch -> viewModel.sendChatMessage(text, ch) },
                    onToggleVoiceMute = { viewModel.toggleVoiceMute() },
                    onSaveToCloud = { viewModel.saveToCloud() },
                    onExportSave = { viewModel.exportSaveCode() },
                    onImportSave = { viewModel.importSaveCode(it) }
                )
            }

            // Dialogs & Modals
            if (state.showOfflineDialog) {
                OfflineRewardDialog(
                    earnedEnergy = state.offlineRewardEarned,
                    secondsAway = state.offlineSecondsAway,
                    onClaimNormal = { viewModel.closeOfflineDialog(doubleWithAd = false) },
                    onClaimDouble = { viewModel.closeOfflineDialog(doubleWithAd = true) }
                )
            }

            LootBoxModal(
                state = state.lootBoxState,
                onClose = { viewModel.closeLootBoxModal() },
                onEquip = { viewModel.equipCosmetic(it) }
            )

            if (state.isAdModalOpen && state.activeAdReward != null) {
                RewardedAdDialog(
                    countdownSeconds = state.adCountdownSeconds,
                    rewardType = state.activeAdReward!!,
                    onClaimReward = { viewModel.claimRewardedAdReward() },
                    onDismiss = { viewModel.dismissAdModal() }
                )
            }

            state.lastDuelReport?.let { report ->
                DuelPostMatchDialog(
                    report = report,
                    onDismiss = { viewModel.dismissDuelReport() }
                )
            }
        }
    }
}
