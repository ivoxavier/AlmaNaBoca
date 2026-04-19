package com.ixsvf.almanaboca.screens.menusubscreens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.screens.components.AlmanaBocaLogo
import com.ixsvf.almanaboca.screens.components.MartaBanner
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.services.model.HomeItem
import com.ixsvf.almanaboca.ui.theme.AccentPurple
import com.ixsvf.almanaboca.ui.theme.CardWhite
import com.ixsvf.almanaboca.ui.theme.PromoYellowBg
import com.ixsvf.almanaboca.ui.theme.PromoYellowText
import com.ixsvf.almanaboca.ui.theme.SpotifyGreen
import com.ixsvf.almanaboca.ui.theme.TextDark
import com.ixsvf.almanaboca.ui.theme.TextGray
import com.ixsvf.almanaboca.ui.theme.WhatsAppGreen
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import com.ixsvf.almanaboca.viewmodel.HomeViewModel
import com.ixsvf.almanaboca.viewmodel.SessionViewModel
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// --- CONFIGURAÇÃO DE ADMINS ---
private val ADMIN_EMAILS = listOf(AlmanaBocaConstants.ADMINS.MARTA, AlmanaBocaConstants.ADMINS.IVO)
// --- NÚMERO DO WHATSAPP DE DESTINO ---
private const val TARGET_WHATSAPP_NUMBER = AlmanaBocaConstants.ADMINS.MARTA_NUMBER

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by sessionViewModel.currentUser.collectAsState()

    // Estado para controlar o menu de opções (Logout)
    var showMenu by remember { mutableStateOf(false) }

    // Obter o nome do utilizador para usar na mensagem do WhatsApp
    val userName = currentUser?.displayName ?: "Alguém"

    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
                        Text(stringResource(R.string.lbl_err_loading_data), color = Color.Red, fontWeight = FontWeight.Bold)
                        Text(state.message, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
            is HomeUiState.Success -> {
                Column(modifier = Modifier.verticalScroll(scrollState)) {

                    // --- CABEÇALHO: LOGÓTIPO + MENU LOGOUT ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        // 1. O Logótipo (com o gatilho secreto de Admin)
                        // Usamos Alignment.Center para garantir que o logo fica no meio
                        /*Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .combinedClickable(
                                    onClick = { },
                                    onLongClick = {
                                        val email = currentUser?.email ?: ""
                                        if (ADMIN_EMAILS.contains(email)) {
                                            Toast.makeText(context, "Bem-vinda Admin!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("admin_bookings")
                                        }
                                    }
                                )
                        ) {
                            //AlmanaBocaLogo()
                        }*/

                        // 2. O Ícone de Menu (Três Pontinhos) no topo direito
                        IconButton(
                            onClick = { navController.navigate("user_screen") },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // --- CONTEÚDO ---
                    val coachingItems = state.courses.filter { it.coachProgram.isNotEmpty() }
                    val meditationItem = state.courses.find { it.meditationCirclesType.isNotEmpty() }

                    Column(
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cursos
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_coach_programs_available)) }
                        if (coachingItems.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(coachingItems) { item -> ProgramCarouselCard(item,
                                    onClick = {
                                    navController.navigate("program_details/${item.coachProgram}")
                                }) }
                            }
                        } else {
                            PaddingBox { Text(stringResource(R.string.lbl_coach_programs_not_available), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }

                        // Meditação
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_meditations_circles)) }
                        PaddingBox {
                            if (meditationItem != null) {

                                MeditationCircleCard(item = meditationItem, userName = userName)
                            } else {
                                Text(stringResource(R.string.lbl_meditations_next_meditations), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_comunities)) }
                        PaddingBox {
                            CommunityCard(onClick = {
                                // Mostra feedback visual que está a verificar (opcional, mas bom UX)
                                // Aqui fazemos a verificação em tempo real:
                                sessionViewModel.verifyAccessNow(
                                    onSuccess = {
                                        // Se o Firebase disser que sim AGORA:
                                        navController.navigate("community_chat")
                                    },
                                    onFailure = {
                                        // Se o Firebase disser que não ou der erro:
                                        Toast.makeText(context, "Acesso negado. Verifica a tua subscrição.", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                            )
                        }

                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_shop_almanaboca)) }

                        PaddingBox {
                            ShopCard(enabled = false,onClick = {
                                navController.navigate("shop_screen")
                            })
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sobre Mim
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_about_me)) }
                        PaddingBox {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                MartaBanner(size = 110.dp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.lbl_marta_banner), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(stringResource(R.string.lbl_marta_banner_mission), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }

                        // Redes Sociais
                        PaddingBox {
                            SocialMediaIconsRow(
                                "https://instagram.com/almanaboca",
                                "https://facebook.com/almanaboca",
                                "https://youtube.com/@almanaboca"
                            )
                        }

                        PaddingBox { SpotifyButton("https://open.spotify.com/show/trupodcast") }
                        //Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// --- COMPONENTES UI ---

@Composable
fun ProgramCarouselCard(item: HomeItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.coachProgram.ifEmpty { "Programa" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.whatToExpectProgram.ifEmpty { stringResource(R.string.lbl_no_description) }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 4)
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
fun MeditationCircleCard(item: HomeItem, userName: String) {
    val context = LocalContext.current

    val tbdLabel = stringResource(R.string.lbl_date_to_be_determined)

    val nextOneLabel = stringResource(R.string.lbl_next_one)

    // --- LÓGICA DE DATA E HORA ---
    // Pair<String, Boolean> -> O primeiro é o texto a mostrar, o segundo é se está aberto
    val sessionInfo = remember(item.meditationCirclesNextSession) {
        try {
            if (item.meditationCirclesNextSession.isEmpty()) return@remember Pair(tbdLabel, false)

            // 1. Parse da data
            val formatter = DateTimeFormatter.ofPattern("[dd.MM.yyyy][dd/MM/yyyy]")
            val sessionDate = LocalDate.parse(item.meditationCirclesNextSession.trim(), formatter)

            val today = LocalDate.now()
            val currentDateTime = LocalDateTime.now()

            // Prazo: Dia da sessão às 11:30
            val deadlineDateTime = sessionDate.atTime(11, 30)

            // 2. Determinar o texto a mostrar
            // Se a data da sessão for ANTERIOR a hoje (ontem ou antes), mostramos "Data a definir"
            val displayText = if (sessionDate.isBefore(today)) {
                tbdLabel
            } else {
                nextOneLabel + ": ${item.meditationCirclesNextSession}"
            }

            // 3. Determinar se o botão está ativo
            // O botão só está ativo se a hora atual for ANTERIOR ao prazo
            val isOpen = currentDateTime.isBefore(deadlineDateTime)

            Pair(displayText, isOpen)
        } catch (e: Exception) {
            Pair(tbdLabel, false)
        }
    }

    val dateLabel = sessionInfo.first
    val isRegistrationOpen = sessionInfo.second

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.meditationCirclesType.ifEmpty { stringResource(R.string.lbl_meditations_in_group) }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.meditationCirclesDesc.ifEmpty { stringResource(R.string.lbl_meditations_join_us) }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            // AQUI USAMOS O TEXTO CALCULADO
            DetailRowSmall(Icons.Outlined.CalendarMonth, dateLabel)

            Spacer(modifier = Modifier.height(8.dp))
            DetailRowSmall(Icons.Outlined.LocationOn, item.meditationCirclesLocation.ifEmpty { stringResource(R.string.lbl_meditations_type) })

//            if (item.meditationCirclesPrice > 0.0) {
//                Spacer(modifier = Modifier.height(8.dp))
//                DetailRowSmall(Icons.Outlined.LocalOffer, "${item.meditationCirclesPrice} €")
//            }

            // --- BOTÃO DE INSCRIÇÃO ---
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val message = "Nome: $userName, conta comigo."
                        val encodedMessage = URLEncoder.encode(message, "UTF-8")
                        val url = "https://wa.me/$TARGET_WHATSAPP_NUMBER?text=$encodedMessage"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isRegistrationOpen,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistrationOpen) WhatsAppGreen else Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRegistrationOpen) stringResource(R.string.lbl_sign_me_now) else stringResource(R.string.lbl_registration_closed),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}




// Adicione este card aos seus componentes UI no final do ficheiro
@Composable
fun CommunityCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(AccentPurple.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Groups, null, tint = AccentPurple, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.lbl_community_almanaboca), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(stringResource(R.string.lbl_exclusive_space_share_support), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(Icons.Default.Send, null, tint = AccentPurple.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}


@Composable
fun ShopCard(
    enabled: Boolean = false, // Por defeito desativado como pedido
    onClick: () -> Unit
) {
    Card(
        // Se não estiver enabled, o onClick não faz nada
        onClick = { if (enabled) onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) CardWhite else Color(0xFFF0F0F0) // Cor mais cinza se desativado
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 4.dp else 0.dp // Remove a sombra se desativado
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer(alpha = if (enabled) 1f else 0.6f) // Torna o card meio transparente se desativado
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if (enabled) AccentPurple.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    // Usei LocalMall como fallback se não tiveres o ic_shop
                    imageVector = Icons.Outlined.LocalMall,
                    contentDescription = null,
                    tint = if (enabled) AccentPurple else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.lbl_shop_almanaboca),
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) TextDark else Color.Gray
                )
                Text(
                    text = if (enabled) stringResource(R.string.lbl_explore_our_products) else stringResource(R.string.lbl_available_soon),
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            if (enabled) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = AccentPurple.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DetailRowSmall(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AccentPurple, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SocialMediaIconsRow(
    instagramUrl: String,
    facebookUrl: String,
    youtubeUrl: String
) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialIconItem(R.drawable.ic_instagram, "Instagram", { uriHandler.openUri(instagramUrl) }, Color(0xFFE1306C))
        SocialIconItem(R.drawable.ic_facebook, "Facebook", { uriHandler.openUri(facebookUrl) }, Color(0xFF1877F2))
        SocialIconItem(R.drawable.ic_youtube, "YouTube", { uriHandler.openUri(youtubeUrl) }, Color(0xFFFF0000))
    }
}

@Composable
fun SocialIconItem(iconRes: Int, contentDescription: String, onClick: () -> Unit, tint: Color) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(painter = painterResource(id = iconRes), contentDescription = contentDescription, modifier = Modifier.size(32.dp), tint = tint)
    }
}

@Composable
fun SpotifyButton(spotifyUrl: String) {
    val uriHandler = LocalUriHandler.current
    Button(onClick = { uriHandler.openUri(spotifyUrl) }, colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen), shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth().height(56.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
        Icon(painter = painterResource(id = R.drawable.ic_spotify),contentDescription = "Spotify Logo",tint = Color.Unspecified)
        Spacer(modifier = Modifier.width(12.dp))
        Text(stringResource(R.string.lbl_listen_on_spotify), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}