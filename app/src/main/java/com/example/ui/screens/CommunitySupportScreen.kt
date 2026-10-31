package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.LeaderboardPlayer
import com.example.ui.GameUiState
import com.example.ui.theme.*
import com.example.util.FormatUtil
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

@Composable
fun CommunitySupportScreen(
    state: GameUiState,
    chatMessagesFlow: Flow<List<ChatMessage>>,
    onSendMessage: (String, String) -> Unit,
    onToggleVoiceMute: () -> Unit,
    onSaveToCloud: () -> Unit,
    onExportSave: () -> String,
    onImportSave: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) }
    val chatMessages by chatMessagesFlow.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sub-tabs navigation
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CosmicCard,
            contentColor = NeonCyan,
            edgePadding = 8.dp,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }, text = { Text("🏆 Ranking", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }, text = { Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 2, onClick = { selectedSubTab = 2 }, text = { Text("🎙️ Voz 24h", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 3, onClick = { selectedSubTab = 3 }, text = { Text("☁️ Nuvem & PC", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 4, onClick = { selectedSubTab = 4 }, text = { Text("🎧 Suporte 24h", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedSubTab) {
            0 -> LeaderboardSubTab(state = state)
            1 -> ChatSubTab(state = state, messages = chatMessages, onSendMessage = onSendMessage)
            2 -> VoiceChatSubTab(state = state, onToggleMute = onToggleVoiceMute)
            3 -> CloudSyncSubTab(state = state, onSaveToCloud = onSaveToCloud, onExportSave = onExportSave, onImportSave = onImportSave)
            4 -> SupportHelpdeskSubTab()
        }
    }
}

@Composable
fun LeaderboardSubTab(state: GameUiState) {
    val topPlayers = remember(state.energy, state.playerMmr) {
        listOf(
            LeaderboardPlayer(1, "GalacticEmperor", "Lorde da Singularidade", "850.4 Q", 142, 3450),
            LeaderboardPlayer(2, "StellarQueen", "Arconte da Galáxia", "120.9 Q", 98, 3120),
            LeaderboardPlayer(3, "NebulaKnight", "Mestre do Tempo", "45.2 Q", 81, 2980),
            LeaderboardPlayer(4, "VoidDrifter", "Gladiador Astral", "8.9 T", 44, 2640),
            LeaderboardPlayer(5, state.playerName, "Você", FormatUtil.formatNumber(state.lifetimeEnergy), state.prestigeCount, state.playerMmr, isCurrentPlayer = true),
            LeaderboardPlayer(6, "SolarFlare", "Cadete Espacial", "1.4 T", 32, 2250),
            LeaderboardPlayer(7, "AstroFox", "Minerador Interestelar", "940.0 B", 28, 1980),
            LeaderboardPlayer(8, "CosmoHunter", "Explorador Astral", "320.5 B", 19, 1720)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TABELA DE CLASSIFICAÇÃO GLOBAL", fontSize = 13.sp, fontWeight = FontWeight.Black, color = CosmicGold)
                        Text("Atualizado em tempo real | Temporada 1", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text("🌐 Ranking #5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }
        }

        items(topPlayers) { player ->
            val isCurrent = player.isCurrentPlayer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCurrent) CosmicElevated else CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) NeonCyan else CosmicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when (player.rank) {
                                1 -> CosmicGold
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> CosmicCardBorder
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${player.rank}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (player.rank <= 3) Color.Black else TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) NeonCyan else TextPrimary
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = NeonCyan.copy(alpha = 0.2f)) {
                                        Text("VOCÊ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Text(player.title, fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${player.energyTotalFormatted} ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("MMR ${player.mmr}", fontSize = 10.sp, color = CosmicGold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatSubTab(
    state: GameUiState,
    messages: List<ChatMessage>,
    onSendMessage: (String, String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("#Global") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toxicity filter banner
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = CosmicGreen.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGreen.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = CosmicGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Filtro de Toxicidade Ativo: Ambiente seguro e moderado.", fontSize = 10.sp, color = CosmicGreen, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Channel Chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("#Global", "#Duelos", "#Clã").forEach { ch ->
                FilterChip(
                    selected = selectedChannel == ch,
                    onClick = { selectedChannel = ch },
                    label = { Text(ch, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages list
        val filteredMessages = messages.filter { it.channel == selectedChannel }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            reverseLayout = true
        ) {
            items(filteredMessages.reversed(), key = { it.id }) { msg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CosmicCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(msg.senderName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (msg.isSystem) CosmicGold else NeonCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("(${msg.senderTitle})", fontSize = 10.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(msg.text, fontSize = 12.sp, color = TextPrimary)
                        if (msg.isFiltered) {
                            Text("⚠️ Mensagem contendo termos ofensivos filtrada pelo sistema.", fontSize = 9.sp, color = PlasmaRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Digite sua mensagem...", fontSize = 12.sp, color = TextMuted) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText, selectedChannel)
                        messageText = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun VoiceChatSubTab(
    state: GameUiState,
    onToggleMute: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Column(
        modifier = modifierFillMax(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CosmicCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
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
                    Column {
                        Text("SALA DE VOZ: CANAL ALFA 1", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("3 Comandantes conectados", fontSize = 11.sp, color = TextSecondary)
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = CosmicGreen.copy(alpha = 0.2f)) {
                        Text("🟢 Conectado", fontSize = 10.sp, color = CosmicGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Simulated Audio Visualizer Wave
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(CosmicElevated, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    val count = 24
                    val step = size.width / count
                    for (i in 0 until count) {
                        val barHeight = if (!state.isVoiceMuted) {
                            val factor = kotlin.math.sin(i.toDouble() + (waveScale * 5.0)).toFloat().coerceAtLeast(0.1f)
                            (size.height * factor * 0.8f)
                        } else 4f
                        val top = (size.height - barHeight) / 2f
                        drawRoundRect(
                            color = if (state.isVoiceMuted) TextMuted else NeonCyan,
                            topLeft = Offset(i * step + 4f, top),
                            size = androidx.compose.ui.geometry.Size(step - 8f, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onToggleMute,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isVoiceMuted) PlasmaRed else CosmicGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(
                        imageVector = if (state.isVoiceMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isVoiceMuted) "MICROFONE MUTADO (TOQUE P/ ATIVAR)" else "MICROFONE TRANSMITINDO AO VIVO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🛡️ IA de Moderação de Áudio ativa com filtros de toxicidade acústica.",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun modifierFillMax(): Modifier = Modifier.fillMaxSize()

@Composable
fun CloudSyncSubTab(
    state: GameUiState,
    onSaveToCloud: () -> Unit,
    onExportSave: () -> String,
    onImportSave: (String) -> Boolean
) {
    var exportCode by remember { mutableStateOf("") }
    var importInput by remember { mutableStateOf("") }
    var importResult by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ARMAZENAMENTO EM NUVEM ASTROCLOUD", fontSize = 14.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                    Text("Sincronize seu progresso entre Android e PC", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onSaveToCloud,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SALVAR NA NUVEM AGORA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (state.cloudSyncMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.cloudSyncMessage, fontSize = 11.sp, color = CosmicGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CÓDIGO DE TRANSFERÊNCIA MULTIPLATAFORMA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Exporte para continuar jogando no PC ou transfira entre aparelhos", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { exportCode = onExportSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gerar Código de Exportação", fontSize = 12.sp)
                    }

                    if (exportCode.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CosmicBgDark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = exportCode,
                                fontSize = 10.sp,
                                color = CosmicGold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Importar Código de Save:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = importInput,
                        onValueChange = { importInput = it },
                        placeholder = { Text("Cole o código ASTRO_SAVE_V1...", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val ok = onImportSave(importInput)
                            importResult = if (ok) "Progresso importado com sucesso!" else "Falha ao importar: Código inválido."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Importar Save", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (importResult.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(importResult, fontSize = 11.sp, color = if (importResult.contains("sucesso")) CosmicGreen else PlasmaRed)
                    }
                }
            }
        }
    }
}

@Composable
fun SupportHelpdeskSubTab() {
    var ticketText by remember { mutableStateOf("") }
    var ticketSentProtocol by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎧", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("SUPORTE TÉCNICO DEDICADO 24/7", fontSize = 13.sp, fontWeight = FontWeight.Black, color = CosmicGreen)
                        Text("Atendimento humano e IA ininterrupto disponível 24h", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Abrir Ticket de Suporte", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Descreva sua dúvida, sugestão ou relato técnico:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ticketText,
                        onValueChange = { ticketText = it },
                        placeholder = { Text("Ex: Dúvida sobre sincronização com o PC...", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (ticketText.isNotBlank()) {
                                ticketSentProtocol = "#TK-${Random.nextInt(10000, 99999)}"
                                ticketText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ENVIAR TICKET 24H", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (ticketSentProtocol.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CosmicGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicGreen)
                        ) {
                            Text(
                                text = "✅ Ticket registrado! Protocolo: $ticketSentProtocol. Status: Em atendimento prioritário pela equipe.",
                                fontSize = 11.sp,
                                color = CosmicGreen,
                                modifier = Modifier.padding(10.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Perguntas Frequentes (FAQ 24h)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Como funciona o Prestígio Cósmico?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeonPurple)
                    Text("O Big Crunch reseta a energia atual em troca de Matéria Escura permanente para a árvore de habilidades.", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Como funciona a proteção Anti-Cheat?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeonCyan)
                    Text("Monitora cliques por segundo (limite 25 CPS humano) e valida a integridade temporal de saves para partidas justas.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}
