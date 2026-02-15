package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ixsvf.almanaboca.services.model.PlaylistItem
import com.ixsvf.almanaboca.ui.theme.states.MeditationsUiState
import com.ixsvf.almanaboca.viewmodel.MeditationsViewModel

@Composable
fun MeditationsScreen(
    // 1. ADICIONADO: O parâmetro modifier
    modifier: Modifier = Modifier,
    onVideoClick: (String) -> Unit,
    viewModel: MeditationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        // 2. APLICADO: O modifier passado é usado aqui
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ... (resto do código igual) ...
        // Título da Secção
        Text(
            text = "Em destaque",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Text(
            text = "Uma jornada para a tranquilidade",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
        )

        // Conteúdo da Lista
        when (uiState) {
            is MeditationsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MeditationsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar meditações.")
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

// ... MeditationCardHero mantém-se igual ...
@Composable
fun MeditationCardHero(
    video: PlaylistItem,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(350.dp)
            .clickable { onClick(video.snippet.resourceId.videoId) },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = video.snippet.thumbnails.medium.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.9f)
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
                    text = video.snippet.title,
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