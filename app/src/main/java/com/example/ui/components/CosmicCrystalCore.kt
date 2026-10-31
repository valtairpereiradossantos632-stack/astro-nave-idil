package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.GameUiState
import com.example.ui.TapParticle
import com.example.ui.theme.*

@Composable
fun CosmicCrystalCore(
    state: GameUiState,
    onCoreClick: () -> Unit,
    onCatchComet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    // Core scale animation when tapped
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "coreScale"
    )

    // Continuous breathing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val cometOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cometFly"
    )

    val currentSkin = state.cosmetics.find { it.id == state.equippedSkinId }
    val currentTrail = state.cosmetics.find { it.id == state.equippedTrailId }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment = Alignment.Center
    ) {
        // Space nebula background hero image
        Image(
            painter = painterResource(id = R.drawable.cosmic_hero_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CosmicBgDark.copy(alpha = 0.6f),
                            CosmicBgDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Cosmic Halo Glow Rings
        Canvas(
            modifier = Modifier
                .size(260.dp)
                .scale(pulseGlow)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.35f),
                        NeonPurple.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 2f
                ),
                center = center,
                radius = size.width / 2f
            )
        }

        // The Clickable Crystal Core
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(scaleAnim)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.4f),
                            CosmicElevated.copy(alpha = 0.9f),
                            CosmicSurface
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    isPressed = true
                    onCoreClick()
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner Core Glow
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                NeonCyan.copy(alpha = 0.3f),
                                NeonPurple.copy(alpha = 0.5f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Skin Icon Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentSkin?.iconEmoji ?: "💎",
                        fontSize = 58.sp
                    )
                    Text(
                        text = currentSkin?.name ?: "Núcleo Cósmico",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // Floating Tap Numbers
        state.tapParticles.forEach { particle ->
            Text(
                text = particle.text,
                fontSize = if (particle.isCrit) 20.sp else 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (particle.isCrit) CosmicGold else NeonCyan,
                modifier = Modifier.offset(x = particle.xOffset.dp, y = particle.yOffset.dp)
            )
        }

        // Surprise Falling Comet Event
        AnimatedVisibility(
            visible = state.isCometActive,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = cometOffset.dp, y = (cometOffset * 0.45f).dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicGold,
                shadowElevation = 8.dp,
                modifier = Modifier.clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onCatchComet()
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌠", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COMETA DOURADO! TOQUE!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        // Bottom Core Status (Equipped Trail & Quick Tip)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CosmicGold, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Rastro: ${currentTrail?.name ?: "Estelar"} | Toque para extrair energia",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(80L)
            isPressed = false
        }
    }
}
