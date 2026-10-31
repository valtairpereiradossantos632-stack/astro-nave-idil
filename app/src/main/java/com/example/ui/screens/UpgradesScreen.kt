package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratorDef
import com.example.ui.GameUiState
import com.example.ui.theme.*
import com.example.util.FormatUtil

@Composable
fun UpgradesScreen(
    state: GameUiState,
    onBuyGenerator: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var buyMode by remember { mutableStateOf(1) } // 1 or 10

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with buy mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MELHORIAS AUTOMÁTICAS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Produção Total: +${FormatUtil.formatNumber(state.totalCps)}/s",
                    fontSize = 12.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 1x / 10x Toggle
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder)
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    Button(
                        onClick = { buyMode = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (buyMode == 1) NeonCyan else Color.Transparent,
                            contentColor = if (buyMode == 1) Color.Black else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("1x", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { buyMode = 10 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (buyMode == 10) NeonCyan else Color.Transparent,
                            contentColor = if (buyMode == 10) Color.Black else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("10x", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generators List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            items(state.generators, key = { it.id }) { gen ->
                GeneratorCard(
                    gen = gen,
                    energy = state.energy,
                    costReductionFactor = state.costReductionFactor,
                    buyMode = buyMode,
                    onBuy = { onBuyGenerator(gen.id, buyMode) }
                )
            }
        }
    }
}

@Composable
fun GeneratorCard(
    gen: GeneratorDef,
    energy: Double,
    costReductionFactor: Double,
    buyMode: Int,
    onBuy: () -> Unit
) {
    var calculatedCost = 0.0
    var tempLevel = gen.level
    for (i in 0 until buyMode) {
        val c = gen.baseCost * Math.pow(1.15, tempLevel.toDouble()) * costReductionFactor
        calculatedCost += c
        tempLevel++
    }
    val canAfford = energy >= calculatedCost

    val nextMilestone = when {
        gen.level < 25 -> 25
        gen.level < 50 -> 50
        gen.level < 100 -> 100
        else -> ((gen.level / 100) + 1) * 100
    }
    val progress = (gen.level.toFloat() / nextMilestone.toFloat()).coerceIn(0f, 1f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CosmicCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (canAfford) NeonCyan.copy(alpha = 0.4f) else CosmicCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(gen.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(gen.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CosmicElevated
                            ) {
                                Text(
                                    text = "Nv. ${gen.level}",
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+${FormatUtil.formatNumber(gen.currentCps(1.0))}/s (${gen.description})",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) NeonCyan else CosmicElevated,
                        contentColor = if (canAfford) Color.Black else TextMuted
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Comprar ${if (buyMode > 1) "$buyMode" else ""}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${FormatUtil.formatNumber(calculatedCost)} ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Milestone Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = CosmicGold,
                    trackColor = CosmicElevated
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${gen.level}/$nextMilestone",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
