package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ixsvf.almanaboca.services.model.PlaylistItem
import com.ixsvf.almanaboca.ui.theme.states.MeditationsUiState
import com.ixsvf.almanaboca.ui.theme.states.QuoteUiState
import com.ixsvf.almanaboca.viewmodel.MeditationsViewModel

@Composable
fun MeditationsScreen(
    modifier: Modifier = Modifier,
    onVideoClick: (String) -> Unit,
    viewModel: MeditationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val quoteState by viewModel.quoteState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            // IMPORTANTE: Adicionado padding no fundo para não ficar atrás da BottomBar flutuante
            .padding(bottom = 100.dp)
    ) {
        Text(
            text = "Em destaque",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface, // Adaptável ao tema
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Text(
            text = "Uma jornada para a tranquilidade",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Adaptável ao tema
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
        )

        // --- 2. MOSTRAR O CARTÃO DA FRASE ---
        DailyQuoteCard(
            quoteState = quoteState,
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)
        )

        when (uiState) {
            is MeditationsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MeditationsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar meditações.", color = MaterialTheme.colorScheme.error)
                }
            }
            is MeditationsUiState.Success -> {
                val videos = (uiState as MeditationsUiState.Success).videos

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(videos) { video ->
                        MeditationCardHero(video = video, onClick = onVideoClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationCardHero(
    video: PlaylistItem,
    onClick: (String) -> Unit
) {
    // --- CORREÇÃO DO CRASH: Safe Access ---
    val imageUrl = remember(video) {
        video.snippet?.thumbnails?.medium?.url
            ?: video.snippet?.thumbnails?.default?.url
            ?: "" // Fallback para string vazia em vez de crashar
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(350.dp)
            .clickable {
                // Proteção extra: só clica se houver ID
                video.snippet?.resourceId?.videoId?.let { onClick(it) }
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Adaptável Dark/Light
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagem com proteção
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente para leitura do texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = video.snippet?.title ?: "Meditação Sem Título",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Meditação • Mindfulness",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


@Composable
fun DailyQuoteCard(
    quoteState: QuoteUiState,
    modifier: Modifier = Modifier
) {
    when (quoteState) {
        is QuoteUiState.Loading -> {
            Box(modifier = modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        is QuoteUiState.Success -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                    )

                    Text(
                        text = "\"${quoteState.quote.text}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (quoteState.quote.author.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "- ${quoteState.quote.author}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        is QuoteUiState.Error -> {
            Box(modifier = modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "A tua frase do dia está a caminho...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}