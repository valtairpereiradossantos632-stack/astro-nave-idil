package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GameUiState
import com.example.ui.theme.*
import com.example.util.FormatUtil

@Composable
fun TopNavBar(
    state: GameUiState,
    onWatchBoostAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CosmicSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top status indicators (Online/Offline, Anti-Cheat, MMR)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CosmicGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Offline Seguro",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Anti-Cheat",
                    tint = NeonCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Anti-Cheat 100%",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            // Player MMR Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚔️ MMR:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.playerMmr}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Resources Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Energy Display
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = FormatUtil.formatNumber(state.energy),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan
                    )
                }
                Text(
                    text = "+${FormatUtil.formatNumber(state.totalCps)}/s",
                    fontSize = 12.sp,
                    color = if (state.isBoostActive) CosmicGreen else TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Secondary Resources (Dark Matter & Gems)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Dark Matter (Prestige)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CosmicCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔮", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.darkMatter}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }
                }

                // Gems (Diamonds)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CosmicCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💎", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.gems}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicGold
                        )
                    }
                }
            }
        }

        // Boost Status Banner if active or ready
        AnimatedVisibility(visible = state.isBoostActive) {
            val remainingSec = (state.boostActiveUntil - System.currentTimeMillis()) / 1000L
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CosmicGreen.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGreen.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = CosmicGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SOBRECARGA 2x ATIVA!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicGreen
                        )
                    }
                    Text(
                        text = FormatUtil.formatTime(maxOf(0L, remainingSec)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CosmicGreen
                    )
                }
            }
        }

        // Anti-Cheat warning if any anomaly detected
        AnimatedVisibility(visible = state.antiCheatNotice.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PlasmaRed.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlasmaRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = state.antiCheatNotice,
                    fontSize = 11.sp,
                    color = PlasmaRed,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
