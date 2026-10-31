package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuelResult
import com.example.ui.DuelActiveState
import com.example.ui.GameUiState
import com.example.ui.theme.*
import com.example.util.FormatUtil
import kotlinx.coroutines.flow.Flow

@Composable
fun DuelArenaScreen(
    state: GameUiState,
    recentDuelsFlow: Flow<List<DuelResult>>,
    onStartMatchmaking: () -> Unit,
    onDuelTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val recentDuels by recentDuelsFlow.collectAsState(initial = emptyList())

    val tierName = when {
        state.playerMmr < 1000 -> "Bronze"
        state.playerMmr < 1500 -> "Prata"
        state.playerMmr < 2000 -> "Ouro"
        state.playerMmr < 2500 -> "Platina"
        state.playerMmr < 3000 -> "Diamante"
        else -> "Mestre Cósmico"
    }

    val tierColor = when (tierName) {
        "Bronze" -> Color(0xFFCD7F32)
        "Prata" -> Color(0xFFC0C0C0)
        "Ouro" -> CosmicGold
        "Platina" -> NeonCyan
        "Diamante" -> NeonPurple
        else -> PlasmaRed
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Active Duel View
        if (state.activeDuel != null) {
            ActiveDuelCombat(
                duel = state.activeDuel,
                onTap = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onDuelTap()
                }
            )
            return@Column
        }

        // Matchmaking Queue
        if (state.isMatchmaking) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "BUSCANDO OPONENTE...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pareamento otimizado: MMR ${state.playerMmr} (±40)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            return@Column
        }

        // Arena Hub
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CosmicCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ARENA DE DUELOS PVP", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Combates em tempo real por velocidade e habilidade", fontSize = 11.sp, color = TextSecondary)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tierColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tierColor)
                    ) {
                        Text(
                            text = tierName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = tierColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Seu MMR Atual", fontSize = 11.sp, color = TextSecondary)
                        Text("${state.playerMmr} pts", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = CosmicGold)
                    }

                    Button(
                        onClick = onStartMatchmaking,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BUSCAR DUELO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { FormatUtil.shareDuelInvite(context, state.playerMmr, "ASTRO-${state.playerMmr}") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Convidar Amigo para Duelo via Rede Social", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "HISTÓRICO RECENTE DE DUELOS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (recentDuels.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicCard,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Nenhum duelo disputado ainda. Clique em 'Buscar Duelo' para começar sua jornada competitiva!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(recentDuels, key = { it.id }) { duel ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (duel.isWin) CosmicGreen.copy(alpha = 0.5f) else PlasmaRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (duel.isWin) "🏆" else "💥", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "vs ${duel.opponentName} (MMR ${duel.opponentMmr})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${duel.playerTaps} cliques (${String.format("%.1f", duel.playerCps)} CPS)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Text(
                                text = "${if (duel.mmrChange > 0) "+" else ""}${duel.mmrChange} MMR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (duel.isWin) CosmicGreen else PlasmaRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveDuelCombat(
    duel: DuelActiveState,
    onTap: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CosmicSurface,
        border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Duel Header with countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DUELO EM ANDAMENTO!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PlasmaRed
                ) {
                    Text(
                        text = "${duel.remainingSeconds}s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opponent vs Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("VOCÊ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Text("${duel.playerTaps} Taps", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                }

                Text("VS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CosmicGold)

                Column(horizontalAlignment = Alignment.End) {
                    Text(duel.opponentName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PlasmaRed)
                    Text("${duel.opponentTaps} Taps", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tug of war power bar
            LinearProgressIndicator(
                progress = { (duel.playerPower / 100f).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp)),
                color = NeonCyan,
                trackColor = PlasmaRed
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (duel.playerPower >= 50) "Vantagem: VOCÊ DOMINANDO!" else "Atenção: OPONENTE AVANÇANDO!",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (duel.playerPower >= 50) NeonCyan else PlasmaRed
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Giant combat tap button
            Button(
                onClick = onTap,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                modifier = Modifier.size(130.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚡", fontSize = 32.sp)
                    Text("TAP!", fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
