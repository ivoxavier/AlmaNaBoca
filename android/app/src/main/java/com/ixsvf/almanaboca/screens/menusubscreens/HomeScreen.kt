package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.AlmanaBocaLogo
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.services.model.HomeItem
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import com.ixsvf.almanaboca.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// --- Cores ---
private val CardWhite = Color.White
private val TextGray = Color(0xFF6B7280)
private val PromoYellowBg = Color(0xFFFFF9C4)
private val PromoYellowText = Color(0xFFB7791F)
private val SpotifyGreen = Color(0xFF1DB954) // Cor oficial do Spotify

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Erro: ${state.message}", color = Color.Red)
                }
            }
            is HomeUiState.Success -> {
                Column(modifier = Modifier.verticalScroll(scrollState)) {

                    AlmanaBocaLogo()

                    Column(
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- SECÇÃO: PROGRAMAS ---
                        PaddingBox {
                            SummaryTopPageText(stringResource(R.string.lbl_coach_programs_available))
                        }

                        if (state.courses.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.courses) { item ->
                                    ProgramCarouselCard(item)
                                }
                            }
                        } else {
                            PaddingBox { Text("Não há programas disponíveis no momento.") }
                        }

                        // --- SECÇÃO: MEDITAÇÃO ---
                        PaddingBox {
                            SummaryTopPageText(stringResource(R.string.lbl_meditations_circles))
                        }

                        PaddingBox {
                            MeditationCircleCard()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- SECÇÃO: REDES SOCIAIS & SPOTIFY ---
                        PaddingBox {
                            SummaryTopPageText(stringResource(R.string.lbl_find_me))
                        }

                        PaddingBox {
                            // Card com ícones sociais
                            SocialMediaCard(
                                instagramUrl = "https://instagram.com/almanaboca",
                                facebookUrl = "https://facebook.com/almanaboca",
                                youtubeUrl = "https://youtube.com/@almanaboca"
                            )
                        }

                        PaddingBox {
                            // Botão Spotify
                            SpotifyButton(spotifyUrl = "https://open.spotify.com/show/trupodcast")
                        }

                        // Espaço extra para o fundo não ficar colado à barra de navegação
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// --- NOVOS COMPONENTES ---

@Composable
fun SocialMediaCard(
    instagramUrl: String,
    facebookUrl: String,
    youtubeUrl: String
) {
    val uriHandler = LocalUriHandler.current

    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Instagram (Link Genérico com cor da marca)
            SocialIconItem(
                icon = Icons.Default.Link,
                contentDescription = "Instagram",
                onClick = { uriHandler.openUri(instagramUrl) },
                tint = Color(0xFFE1306C) // Rosa Insta
            )

            // Facebook (Link Genérico com cor da marca)
            SocialIconItem(
                icon = Icons.Default.Link,
                contentDescription = "Facebook",
                onClick = { uriHandler.openUri(facebookUrl) },
                tint = Color(0xFF1877F2) // Azul Face
            )

            // YouTube (Link Genérico com cor da marca)
            SocialIconItem(
                icon = Icons.Default.Link,
                contentDescription = "YouTube",
                onClick = { uriHandler.openUri(youtubeUrl) },
                tint = Color(0xFFFF0000) // Vermelho YouTube
            )
        }
    }
}

@Composable
fun SocialIconItem(
    icon: ImageVector, // MUDANÇA: Agora recebe um vetor em vez de um Int (resourceId)
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon, // Usa o vetor passado (Link)
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp),
            tint = tint
        )
    }
}

@Composable
fun SpotifyButton(spotifyUrl: String) {
    val uriHandler = LocalUriHandler.current

    Button(
        onClick = { uriHandler.openUri(spotifyUrl) },
        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
        shape = RoundedCornerShape(50), // Botão bem redondo
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Headphones, // Ícone genérico de áudio se não tiver o logo
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Ouve-me no Spotify",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// --- COMPONENTES EXISTENTES (MANTIDOS) ---


@Composable
fun ProgramCarouselCard(item: HomeItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.coachProgram.ifEmpty { "Programa de Coaching" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.whatToExpectProgram.ifEmpty { "Sem descrição disponível." },
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            DetailRowSmall(
                icon = Icons.Outlined.CalendarMonth,
                text = "${item.coachDateStart} - ${item.coachDateEnd}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRowSmall(
                icon = Icons.Outlined.Groups,
                text = "${item.coachVacancies} vagas restantes"
            )

            if (item.coachDiscount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PromoYellowBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocalOffer,
                            contentDescription = null,
                            tint = PromoYellowText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${item.coachDiscount.toInt()}% OFF",
                            style = MaterialTheme.typography.labelLarge,
                            color = PromoYellowText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationCircleCard(){
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.lbl_meditations_in_group),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sem descrição disponível." ,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            DetailRowSmall(
                icon = Icons.Outlined.CalendarMonth,
                text = "31/01/2026"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRowSmall(
                icon = Icons.Outlined.LocationOn,
                text = "Take me there"
            )
        }
    }
}

@Composable
fun DetailRowSmall(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}