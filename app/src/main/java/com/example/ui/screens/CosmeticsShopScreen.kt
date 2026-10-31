package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
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
import com.example.data.model.CosmeticItem
import com.example.data.model.CosmeticType
import com.example.ui.AdRewardType
import com.example.ui.GameUiState
import com.example.ui.theme.*

@Composable
fun CosmeticsShopScreen(
    state: GameUiState,
    onOpenLootBox: (Boolean) -> Unit, // true = use gems, false = with ad
    onEquipCosmetic: (String) -> Unit,
    onWatchAdReward: (AdRewardType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCosmeticType by remember { mutableStateOf(CosmeticType.CORE_SKIN) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        // Loot Box Capsule Section
        item {
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
                            Text("CÁPSULA CÓSMICA (LOOT BOX)", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeonPurple)
                            Text("Desbloqueie skins lendárias, rastros e títulos", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text("🎁", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rarity drop rates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Comum: 50%", fontSize = 10.sp, color = Color(0xFF9EACCC), fontWeight = FontWeight.Bold)
                        Text("Raro: 30%", fontSize = 10.sp, color = Color(0xFF2979FF), fontWeight = FontWeight.Bold)
                        Text("Épico: 15%", fontSize = 10.sp, color = Color(0xFFD500F9), fontWeight = FontWeight.Bold)
                        Text("Lendário: 5%", fontSize = 10.sp, color = CosmicGold, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenLootBox(true) },
                            enabled = state.gems >= 25,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("Abrir (25 💎)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onOpenLootBox(false) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicElevated, contentColor = CosmicGold),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("Abrir com Ad 📺", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Rewarded Ads Boost Hub
        item {
            Text(
                text = "RECOMPENSAS DE HOLO-ANÚNCIOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Ad Option 1: Boost 2x
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("⚡ Sobrecarga Quântica 2x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Dobra a renda por 2 horas", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { onWatchAdReward(AdRewardType.DoubleBoost) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Assistir 📺", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = CosmicCardBorder)

                    // Ad Option 2: 30 Gems
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("💎 Pacote de Gemas Extras", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Receba +30 Gemas gratuitas instantaneamente", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { onWatchAdReward(AdRewardType.LootBoxKey) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicGold, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Assistir 📺", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cosmetic Inventory Wardrobe
        item {
            Text(
                text = "GUARDA-ROUPA COSMÉTICO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCosmeticType == CosmeticType.CORE_SKIN,
                    onClick = { selectedCosmeticType = CosmeticType.CORE_SKIN },
                    label = { Text("Skins do Cristal", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedCosmeticType == CosmeticType.TAP_TRAIL,
                    onClick = { selectedCosmeticType = CosmeticType.TAP_TRAIL },
                    label = { Text("Rastros", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedCosmeticType == CosmeticType.PLAYER_TITLE,
                    onClick = { selectedCosmeticType = CosmeticType.PLAYER_TITLE },
                    label = { Text("Títulos", fontSize = 11.sp) }
                )
            }
        }

        val filteredItems = state.cosmetics.filter { it.type == selectedCosmeticType }
        items(filteredItems, key = { it.id }) { item ->
            val rarityColor = Color(item.rarity.colorHex)
            val isEquipped = item.isEquipped

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CosmicCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isEquipped) NeonCyan else CosmicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(item.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = rarityColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = item.rarity.label,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = rarityColor,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(item.description, fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    if (!item.isUnlocked) {
                        Surface(shape = RoundedCornerShape(6.dp), color = CosmicElevated) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bloqueado", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    } else if (isEquipped) {
                        Surface(shape = RoundedCornerShape(6.dp), color = NeonCyan.copy(alpha = 0.2f)) {
                            Text(
                                text = "EQUIPADO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onEquipCosmetic(item.id) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicElevated, contentColor = TextPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Equipar", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
