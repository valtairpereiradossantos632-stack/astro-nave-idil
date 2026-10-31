package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AdRewardType
import com.example.ui.GameUiState
import com.example.ui.components.CosmicCrystalCore
import com.example.ui.theme.*
import com.example.util.FormatUtil

@Composable
fun CoreScreen(
    state: GameUiState,
    onCoreClick: () -> Unit,
    onCatchComet: () -> Unit,
    onQuickBuy: (String) -> Unit,
    onWatchBoostAd: (AdRewardType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Interactive Crystal
        CosmicCrystalCore(
            state = state,
            onCoreClick = onCoreClick,
            onCatchComet = onCatchComet
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rewarded Boost Quick Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CosmicCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (state.isBoostActive) CosmicGreen else CosmicGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isBoostActive) "SOBRECARGA 2x ATIVA" else "SOBRECARGA 2x DISPONÍVEL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isBoostActive) CosmicGreen else CosmicGold
                        )
                    }
                    Text(
                        text = if (state.isBoostActive) "Produção dobrada! Aproveite a aceleração cósmica." else "Assista a um holo-anúncio rápido e dobre toda sua renda por 2 horas!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                if (!state.isBoostActive) {
                    Button(
                        onClick = { onWatchBoostAd(AdRewardType.DoubleBoost) },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("ATIVAR 2x", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Purchase Top 3 Generators
        Text(
            text = "GERADORES EM DESTAQUE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        val featuredGenerators = state.generators.take(3)
        featuredGenerators.forEach { gen ->
            val cost = gen.currentCost(state.costReductionFactor)
            val canAfford = state.energy >= cost

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(gen.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(gen.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CosmicElevated
                                ) {
                                    Text(
                                        text = "Nv. ${gen.level}",
                                        fontSize = 10.sp,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "+${FormatUtil.formatNumber(gen.currentCps(1.0))}/s",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { onQuickBuy(gen.id) },
                        enabled = canAfford,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) NeonCyan else CosmicElevated,
                            contentColor = if (canAfford) Color.Black else TextMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = FormatUtil.formatNumber(cost) + " ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
