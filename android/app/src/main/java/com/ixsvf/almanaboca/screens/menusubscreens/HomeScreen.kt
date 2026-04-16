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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
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
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.screens.components.AlmanaBocaLogo
import com.ixsvf.almanaboca.screens.components.MartaBanner
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.services.model.HomeItem
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import com.ixsvf.almanaboca.viewmodel.HomeViewModel
import com.ixsvf.almanaboca.viewmodel.SessionViewModel
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// --- Cores ---
private val CardWhite = Color.White
val TextGray = Color(0xFF6B7280)
val TextDark = Color(0xFF1F2937)
private val PromoYellowBg = Color(0xFFFFF9C4)
private val PromoYellowText = Color(0xFFB7791F)
private val SpotifyGreen = Color(0xFF1DB954)
private val WhatsAppGreen = Color(0xFF25D366) // Cor do WhatsApp
val AccentPurple = Color(0xFF7C4DFF)

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
                        Text("Erro ao carregar dados", color = Color.Red, fontWeight = FontWeight.Bold)
                        Text(state.message, color = Color.Gray, fontSize = 12.sp)
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
                                tint = TextDark,
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
                            PaddingBox { Text(stringResource(R.string.lbl_coach_programs_not_available), color = TextGray) }
                        }

                        // Meditação
                        PaddingBox { SummaryTopPageText(stringResource(R.string.lbl_meditations_circles)) }
                        PaddingBox {
                            if (meditationItem != null) {

                                MeditationCircleCard(item = meditationItem, userName = userName)
                            } else {
                                Text(stringResource(R.string.lbl_meditations_next_meditations), color = TextGray)
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
                                    Text(stringResource(R.string.lbl_marta_banner), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(stringResource(R.string.lbl_marta_banner_mission), style = MaterialTheme.typography.bodySmall, color = TextGray, lineHeight = 18.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
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
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.coachProgram.ifEmpty { "Programa" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.whatToExpectProgram.ifEmpty { stringResource(R.string.lbl_no_description) }, style = MaterialTheme.typography.bodySmall, color = TextGray, maxLines = 4)
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

    // --- LÓGICA DE DATA E HORA ---
    // Pair<String, Boolean> -> O primeiro é o texto a mostrar, o segundo é se está aberto
    val sessionInfo = remember(item.meditationCirclesNextSession) {
        try {
            if (item.meditationCirclesNextSession.isEmpty()) return@remember Pair("Data a definir", false)

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
                "Data a definir"
            } else {
                "Próxima: ${item.meditationCirclesNextSession}"
            }

            // 3. Determinar se o botão está ativo
            // O botão só está ativo se a hora atual for ANTERIOR ao prazo
            val isOpen = currentDateTime.isBefore(deadlineDateTime)

            Pair(displayText, isOpen)
        } catch (e: Exception) {
            Pair("Data a definir", false)
        }
    }

    val dateLabel = sessionInfo.first
    val isRegistrationOpen = sessionInfo.second

    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.meditationCirclesType.ifEmpty { stringResource(R.string.lbl_meditations_in_group) }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.meditationCirclesDesc.ifEmpty { stringResource(R.string.lbl_meditations_join_us) }, style = MaterialTheme.typography.bodySmall, color = TextGray)
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
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRegistrationOpen) "INSCREVER AGORA" else "INSCRIÇÕES FECHADAS",
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
        colors = CardDefaults.cardColors(containerColor = CardWhite),
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
                Text("Comunidade AlmaNaBoca", fontWeight = FontWeight.Bold, color = TextDark)
                Text("Espaço exclusivo de partilha e apoio.", fontSize = 12.sp, color = TextGray)
            }

            Icon(Icons.Default.Send, null, tint = AccentPurple.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}





@Composable
fun DetailRowSmall(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AccentPurple, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextDark)
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
        Icon(Icons.Outlined.Headphones, null, tint = Color.White)
        Spacer(modifier = Modifier.width(12.dp))
        Text("Ouve-me no Spotify", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}