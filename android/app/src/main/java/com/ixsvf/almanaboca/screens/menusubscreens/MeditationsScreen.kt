package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.viewmodel.MeditationsViewModel

// --- Modelo de Dados Visual (UI Model) ---
data class MeditationUiModel(
    val id: Int,
    val dayTitle: String,     // ex: "Dia 1"
    val title: String,        // ex: "A Respiração Consciente"
    val duration: String,     // ex: "10 min"
    val isCompleted: Boolean,
    val isLocked: Boolean
)

// --- Cores Específicas para esta Tela ---
private val MeditationPrimary = Color(0xFF6C63FF) // Um roxo/azul suave
private val MeditationSurface = Color(0xFFF3F4F6)
private val TextDark = Color(0xFF1F2937)
private val TextLight = Color(0xFF9CA3AF)

@Composable
fun MeditationsScreen(
    modifier: Modifier = Modifier,
    viewModel: MeditationsViewModel = viewModel()
) {
    // --- Dados Fictícios (Isto viria do ViewModel) ---
    val allMeditations = remember {
        listOf(
            MeditationUiModel(1, "Dia 1", "Chegar ao Momento", "08:00", true, false),
            MeditationUiModel(2, "Dia 2", "Escutar o Corpo", "10:30", false, false),
            MeditationUiModel(3, "Dia 3", "Libertar a Ansiedade", "12:00", false, false),
            MeditationUiModel(4, "Dia 4", "Cultivar a Gratidão", "09:15", false, true), // Bloqueado exemplo
            MeditationUiModel(5, "Dia 5", "O Poder do Silêncio", "15:00", false, true),
            MeditationUiModel(6, "Dia 6", "Conexão Profunda", "11:45", false, true),
            MeditationUiModel(7, "Dia 7", "Integração Total", "14:20", false, true),
        )
    }

    // Estado: Qual meditação está no Hero Card? (Começa com a primeira não bloqueada ou a última feita)
    var selectedMeditation by remember { mutableStateOf(allMeditations[1]) } // Começa no Dia 2 para exemplo

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Título da Página
        PaddingBox {
            Spacer(modifier = Modifier.height(16.dp))
            SummaryTopPageText("Jornada de 7 Dias")
        }

        // Lista com conteúdo
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ITEM 1: O HERO CARD (Ocupa o topo da lista)
            item {
                PaddingBox {
                    Spacer(modifier = Modifier.height(8.dp))
                    HeroPlayerCard(
                        meditation = selectedMeditation,
                        onPlayClick = {
                            // Lógica de Play/Pause viria aqui
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sessões Disponíveis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ITEM 2...N: A Lista das restantes meditações
            items(allMeditations) { meditation ->
                PaddingBox {
                    MeditationListItem(
                        meditation = meditation,
                        isSelected = meditation.id == selectedMeditation.id,
                        onClick = {
                            if (!meditation.isLocked) {
                                selectedMeditation = meditation
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// --- COMPONENTE: HERO PLAYER CARD (O Destaque) ---
@Composable
fun HeroPlayerCard(
    meditation: MeditationUiModel,
    onPlayClick: () -> Unit
) {
    // Simulação de estado de Play (UI only)
    var isPlaying by remember { mutableStateOf(false) }
    // Simulação de progresso (0.0 a 1.0)
    var progress by remember { mutableStateOf(0.3f) }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Altura fixa para dar destaque
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Fundo com Gradiente (Simula uma imagem bonita)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF8E2DE2), // Roxo
                                Color(0xFF4A00E0)  // Azul escuro
                            )
                        )
                    )
            )

            // 2. Conteúdo Sobreposto
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Topo: Indicador do Dia
                Text(
                    text = meditation.dayTitle.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )

                // Centro: Título e Ícone
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Headphones,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = meditation.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${meditation.duration} • Relaxamento",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Baixo: Controlos de Player
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Barra de Progresso
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    // Botões de Controlo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botão Play/Pause Grande
                        IconButton(
                            onClick = {
                                isPlaying = !isPlaying
                                onPlayClick()
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = Color(0xFF4A00E0),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTE: ITEM DA LISTA (Os outros dias) ---
@Composable
fun MeditationListItem(
    meditation: MeditationUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MeditationPrimary.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isSelected) MeditationPrimary else Color.Transparent

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shadowElevation = if (isSelected) 0.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !meditation.isLocked) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícone de Estado (Play, Lock ou Check)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (meditation.isLocked) Color.Gray.copy(alpha = 0.1f) else Color(0xFFE0E7FF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when {
                        meditation.isLocked -> Icons.Filled.Lock
                        meditation.isCompleted -> Icons.Filled.CheckCircle
                        isSelected -> Icons.Filled.PlayArrow
                        else -> Icons.Outlined.Headphones
                    }
                    val iconTint = when {
                        meditation.isLocked -> Color.Gray
                        meditation.isCompleted -> Color(0xFF10B981) // Verde
                        else -> MeditationPrimary
                    }

                    Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Textos
                Column {
                    Text(
                        text = meditation.dayTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (meditation.isLocked) TextLight else MeditationPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = meditation.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (meditation.isLocked) TextLight else TextDark
                    )
                }
            }

            // Duração
            Text(
                text = meditation.duration,
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight
            )
        }
    }
}
