package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuest
import com.example.data.model.PrestigeUpgradeDef
import com.example.ui.GameUiState
import com.example.ui.theme.*
import com.example.util.FormatUtil

@Composable
fun PrestigeSeasonScreen(
    state: GameUiState,
    onPerformPrestige: () -> Unit,
    onBuyPrestigeUpgrade: (String) -> Unit,
    onClaimQuest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Prestígio, 1: Missões & Temporada

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section selector
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = CosmicCard,
            contentColor = NeonCyan,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("🔮 Ascensão Cósmica", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("🌟 Temporada & Missões", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSection == 0) {
            // Prestige Section
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                item {
                    // Big Crunch Ascensão Card
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CosmicCard,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "BIG CRUNCH: ASCENSÃO",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeonPurple
                                    )
                                    Text(
                                        text = "Reinicie para despertar o poder da Matéria Escura",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CosmicElevated
                                ) {
                                    Text(
                                        text = "${state.prestigeCount} Ascensões",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Ganho no Reinício:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "+${state.prestigeGainOnReset} Matéria Escura 🔮",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NeonPurple
                                    )
                                }

                                Button(
                                    onClick = onPerformPrestige,
                                    enabled = state.prestigeGainOnReset > 0L,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonPurple,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("ASCENDER AGORA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            if (state.prestigeGainOnReset <= 0L) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Necessário acumular pelo menos 100.0K de Energia para a primeira Ascensão.",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "ÁRVORE TECNOLÓGICA PERMANENTE (MATÉRIA ESCURA: ${state.darkMatter} 🔮)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(state.prestigeUpgrades, key = { it.id }) { upgrade ->
                    val cost = upgrade.currentCost()
                    val canAfford = state.darkMatter >= cost && upgrade.level < upgrade.maxLevel

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CosmicCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(upgrade.iconEmoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(upgrade.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Nv. ${upgrade.level}/${upgrade.maxLevel}", fontSize = 10.sp, color = NeonPurple, fontWeight = FontWeight.Bold)
                                    }
                                    Text(upgrade.description, fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Button(
                                onClick = { onBuyPrestigeUpgrade(upgrade.id) },
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canAfford) NeonPurple else CosmicElevated,
                                    contentColor = if (canAfford) Color.White else TextMuted
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (upgrade.level >= upgrade.maxLevel) "MAX" else "$cost 🔮",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Season Pass and Daily Quests
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                item {
                    // Season Pass Banner
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CosmicCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGold.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("TEMPORADA 1: TEMPESTADE SOLAR", fontSize = 15.sp, fontWeight = FontWeight.Black, color = CosmicGold)
                                    Text("Encerra em 18 dias | Passe de Recompensas", fontSize = 11.sp, color = TextSecondary)
                                }
                                Text("Nv. ${(state.seasonPoints / 100) + 1}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { ((state.seasonPoints % 100) / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = CosmicGold,
                                trackColor = CosmicElevated
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Progresso atual: ${state.seasonPoints % 100}/100 XP para o próximo nível",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "MISSÕES DIÁRIAS (RESETAM EM 14h)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(state.dailyQuests, key = { it.id }) { quest ->
                    val isDone = quest.current >= quest.target
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CosmicCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDone && !quest.isClaimed) CosmicGreen.copy(alpha = 0.5f) else CosmicCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(quest.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(quest.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(quest.description, fontSize = 11.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${quest.current}/${quest.target}", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (quest.isClaimed) {
                                Surface(shape = RoundedCornerShape(6.dp), color = CosmicElevated) {
                                    Text("Coletado", fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            } else {
                                Button(
                                    onClick = { onClaimQuest(quest.id) },
                                    enabled = isDone,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDone) CosmicGreen else CosmicElevated,
                                        contentColor = if (isDone) Color.Black else TextMuted
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("+${quest.rewardGems} 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
