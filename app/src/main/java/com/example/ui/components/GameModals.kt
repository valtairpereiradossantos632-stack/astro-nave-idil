package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DuelResult
import com.example.ui.AdRewardType
import com.example.ui.LootBoxState
import com.example.ui.theme.*
import com.example.util.FormatUtil

@Composable
fun OfflineRewardDialog(
    earnedEnergy: Double,
    secondsAway: Long,
    onClaimNormal: () -> Unit,
    onClaimDouble: () -> Unit
) {
    Dialog(onDismissRequest = onClaimNormal) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CosmicGreen),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⏳", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BÔNUS OFFLINE COLETADO!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicGreen,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Você esteve ausente por ${FormatUtil.formatTime(secondsAway)}. Seus geradores automáticos continuaram trabalhando a todo vapor no espaço profundo!",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CosmicElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Energia Acumulada", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${FormatUtil.formatNumber(earnedEnergy)} ⚡",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClaimDouble,
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DOBRAR 2x (+${FormatUtil.formatNumber(earnedEnergy * 2)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onClaimNormal,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Coletar Normal")
                }
            }
        }
    }
}

@Composable
fun LootBoxModal(
    state: LootBoxState,
    onClose: () -> Unit,
    onEquip: (String) -> Unit
) {
    if (state is LootBoxState.Closed) return

    Dialog(onDismissRequest = { if (state is LootBoxState.Opened) onClose() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicSurface,
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonPurple),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    is LootBoxState.Opening -> {
                        val infiniteTransition = rememberInfiniteTransition(label = "boxSpin")
                        val rotateAnim by infiniteTransition.animateFloat(
                            initialValue = 0.85f,
                            targetValue = 1.25f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(400, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "boxScale"
                        )

                        Text("🎁", fontSize = 64.sp, modifier = Modifier.scale(rotateAnim))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DESBLOQUEANDO CÁPSULA...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPurple
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sintonizando frequências estelares...", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            color = NeonPurple,
                            trackColor = CosmicCard,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
                        )
                    }

                    is LootBoxState.Opened -> {
                        val item = state.item
                        val rarityColor = Color(item.rarity.colorHex)

                        Text(item.iconEmoji, fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = rarityColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, rarityColor)
                        ) {
                            Text(
                                text = item.rarity.label.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = rarityColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        if (state.isDuplicate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Item repetido! Compensação de +${state.refundGems} Gemas concedida.",
                                fontSize = 11.sp,
                                color = CosmicGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onEquip(item.id)
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Equipar Agora", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onClose,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Fechar")
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun RewardedAdDialog(
    countdownSeconds: Int,
    rewardType: AdRewardType,
    onClaimReward: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (countdownSeconds <= 0) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicBgDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CosmicGold),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📺 Holo-Anúncio Patrocinado", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CosmicCardBorder
                    ) {
                        Text(
                            text = if (countdownSeconds > 0) "${countdownSeconds}s" else "Concluído",
                            fontSize = 11.sp,
                            color = CosmicGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CosmicCard, CosmicElevated, CosmicCard)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚀", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AstroTech Industries",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan
                        )
                        Text(
                            text = "Tecnologia Quântica para Colonização Estelar",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (countdownSeconds > 0) {
                    Text(
                        text = "Aguarde $countdownSeconds segundos para coletar sua recompensa gratuita...",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESGATAR RECOMPENSA GRÁTIS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DuelPostMatchDialog(
    report: DuelResult,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicSurface,
            border = androidx.compose.foundation.BorderStroke(2.dp, if (report.isWin) CosmicGreen else PlasmaRed),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (report.isWin) "🏆 VITÓRIA GLORIOSA!" else "💥 DERROTA ASTRAL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (report.isWin) CosmicGreen else PlasmaRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Duelo contra ${report.opponentName} (MMR ${report.opponentMmr})",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CosmicCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Velocidade (CPS):", fontSize = 12.sp, color = TextSecondary)
                            Text(String.format("%.1f taps/s", report.playerCps), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cliques Totais:", fontSize = 12.sp, color = TextSecondary)
                            Text("${report.playerTaps} cliques", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dano Total:", fontSize = 12.sp, color = TextSecondary)
                            Text("${report.playerDamage} pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CosmicGold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Precisão Crítica:", fontSize = 12.sp, color = TextSecondary)
                            Text("${report.critAccuracyPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = CosmicCardBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Variação de MMR:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = "${if (report.mmrChange > 0) "+" else ""}${report.mmrChange} MMR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (report.mmrChange > 0) CosmicGreen else PlasmaRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            FormatUtil.shareAchievement(
                                context,
                                if (report.isWin) "Vitória no Duelo contra ${report.opponentName}" else "Duelo Cósmico Intenso",
                                "${report.playerDamage}"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartilhar", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicElevated),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("Continuar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
