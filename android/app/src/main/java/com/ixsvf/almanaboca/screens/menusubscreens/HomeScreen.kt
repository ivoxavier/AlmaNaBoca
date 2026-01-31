package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
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

// --- Cores ---
private val CardWhite = Color.White
private val TextGray = Color(0xFF6B7280)
private val TextDark = Color(0xFF1F2937)
private val PromoYellowBg = Color(0xFFFFF9C4)
private val PromoYellowText = Color(0xFFB7791F)
private val SpotifyGreen = Color(0xFF1DB954)
private val AccentPurple = Color(0xFF7C4DFF)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erro ao carregar dados", color = Color.Red, fontWeight = FontWeight.Bold)
                        Text(state.message, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
            is HomeUiState.Success -> {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    AlmanaBocaLogo()

                    // --- SEPARAÇÃO DOS DADOS ---
                    // 1. Cursos: Tudo o que tem 'coachProgram' preenchido
                    val coachingItems = state.courses.filter { it.coachProgram.isNotEmpty() }

                    // 2. Meditação: O item que tem 'meditationCirclesType' preenchido (vindo da raiz)
                    val meditationItem = state.courses.find { it.meditationCirclesType.isNotEmpty() }

                    Column(
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- CAROUSEL DE CURSOS ---
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_coach_programs_available)) }

                        if (coachingItems.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(coachingItems) { item -> ProgramCarouselCard(item) }
                            }
                        } else {
                            PaddingBox {
                                Text(stringResource(R.string.lbl_coach_programs_not_available), color = TextGray)
                            }
                        }

                        // --- CARTÃO DE MEDITAÇÃO ---
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_meditations_circles)) }

                        PaddingBox {
                            if (meditationItem != null) {
                                MeditationCircleCard(item = meditationItem)
                            } else {
                                Text(stringResource(R.string.lbl_meditations_next_meditations), color = TextGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- RODAPÉ ---
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_find_me)) }
                        PaddingBox {
                            SocialMediaCard(
                                "https://instagram.com/almanaboca",
                                "https://facebook.com/almanaboca",
                                "https://youtube.com/@almanaboca"
                            )
                        }
                        PaddingBox { SpotifyButton("https://open.spotify.com/show/trupodcast") }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProgramCarouselCard(item: HomeItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(300.dp).wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.coachProgram.ifEmpty { "Programa" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.whatToExpectProgram.ifEmpty { "Sem descrição." }, style = MaterialTheme.typography.bodySmall, color = TextGray, maxLines = 4)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            DetailRowSmall(Icons.Outlined.CalendarMonth, if (item.coachStartDate.isNotEmpty()) "${item.coachStartDate} - ${item.coachDateEnd}" else "Datas a anunciar")
            Spacer(modifier = Modifier.height(8.dp))
            DetailRowSmall(Icons.Outlined.Groups, "${item.coachVacancies} " + stringResource(R.string.lbl_coach_vacancies))

            if (item.coachDiscount > 0.0) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().background(PromoYellowBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(8.dp), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocalOffer, null, tint = PromoYellowText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${item.coachDiscount.toInt()}% OFF", style = MaterialTheme.typography.labelLarge, color = PromoYellowText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationCircleCard(item: HomeItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // TÍTULO DA MEDITAÇÃO (Lido da Raiz)
            Text(item.meditationCirclesType.ifEmpty { stringResource(R.string.lbl_meditations_in_group) }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)

            Spacer(modifier = Modifier.height(8.dp))
            Text(item.meditationCirclesDesc.ifEmpty { stringResource(R.string.lbl_meditations_join_us) }, style = MaterialTheme.typography.bodySmall, color = TextGray)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            DetailRowSmall(Icons.Outlined.CalendarMonth, if (item.meditationCirclesNextSession.isNotEmpty()) "Próxima: ${item.meditationCirclesNextSession}" else "Data a anunciar")
            Spacer(modifier = Modifier.height(8.dp))
            DetailRowSmall(Icons.Outlined.LocationOn, item.meditationCirclesLocation.ifEmpty { stringResource(R.string.lbl_meditations_type) })

            if (item.meditationCirclesPrice > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                DetailRowSmall(Icons.Outlined.LocalOffer, "${item.meditationCirclesPrice} €")
            }
        }
    }
}

// Componentes Auxiliares (Iguais)
@Composable
fun DetailRowSmall(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AccentPurple, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}
@Composable
fun SocialMediaCard(instagramUrl: String, facebookUrl: String, youtubeUrl: String) {
    val uriHandler = LocalUriHandler.current
    Card(colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            SocialIconItem(Icons.Default.Link, "Instagram", { uriHandler.openUri(instagramUrl) }, Color(0xFFE1306C))
            SocialIconItem(Icons.Default.Link, "Facebook", { uriHandler.openUri(facebookUrl) }, Color(0xFF1877F2))
            SocialIconItem(Icons.Default.Link, "YouTube", { uriHandler.openUri(youtubeUrl) }, Color(0xFFFF0000))
        }
    }
}
@Composable
fun SocialIconItem(icon: ImageVector, contentDescription: String, onClick: () -> Unit, tint: Color) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) { Icon(icon, contentDescription, modifier = Modifier.size(32.dp), tint = tint) }
}
@Composable
fun SpotifyButton(spotifyUrl: String) {
    val uriHandler = LocalUriHandler.current
    Button(onClick = { uriHandler.openUri(spotifyUrl) }, colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen), shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth().height(56.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
        Icon(Icons.Outlined.Headphones, null, tint = Color.White)
        Spacer(modifier = Modifier.width(12.dp))
        Text("Ouve-me no Spotify", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}