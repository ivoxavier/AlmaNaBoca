package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.services.model.ChatMessage
import com.ixsvf.almanaboca.viewmodel.ChatViewModel
import com.ixsvf.almanaboca.viewmodel.SessionViewModel

//private val AccentPurple = Color(0xFF7C4DFF)
//private val TextDark = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel()
) {
    val messages by chatViewModel.messages
    val currentUser by sessionViewModel.currentUser.collectAsState()
    val realName by sessionViewModel.userName.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Cor de fundo geral do ecrã
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lbl_community_almanaboca), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.lbl_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Garante fundo cinza atrás de tudo
        ) {

            // --- CAIXA BRANCA COM A LISTA DE MENSAGENS ---
            Surface(
                modifier = Modifier
                    .weight(1f) // Ocupa todo o espaço disponível menos o input
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 1.dp), // Margem para ver o fundo cinza
                color = Color.White, // Fundo branco pedido
                shape = RoundedCornerShape(24.dp), // Arredondamento em cima e em baixo
                shadowElevation = 2.dp // Pequena sombra para destacar (opcional)
            ) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(items = messages) { msg ->
                        // --- ANIMAÇÃO DE ENTRADA ---
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(400)) +
                                    slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(400)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val isMine = msg.senderId == currentUser?.uid
                            ChatBubble(msg, isMine)
                        }
                    }
                }
            }

            // --- INPUT DE MENSAGEM ARREDONDADO ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Protege contra a barra de navegação do Android
                    .imePadding() // Sobe com o teclado
                    .padding(bottom = 12.dp, start = 10.dp, end = 12.dp), // Espaçamento externo
                tonalElevation = 8.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.lbl_say_something) + "...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AccentPurple
                        ),
                        maxLines = 4
                    )

                    // Botão Enviar
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                chatViewModel.sendMessage(
                                    messageText,
                                    currentUser?.uid ?: "",
                                    realName
                                )
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(44.dp)
                            .background(
                                if (messageText.isNotBlank()) AccentPurple else Color(0xFFF0F0F0),
                                RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.lbl_send),
                            modifier = Modifier.size(20.dp),
                            tint = if (messageText.isNotBlank()) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, isMine: Boolean) {
    val timeFormatted = remember(msg.timestamp) {
        val date = java.util.Date(msg.timestamp)
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(date)
    }

    // Cores
    val nameColor = Color(0xFF7C4DFF) // AccentPurple
    // NOTA: Mudei a cor da TUA bolha para Roxo, e a dos OUTROS para Cinza claro
    // para contrastar melhor com o fundo Branco da lista.
    val bubbleColorMine = Color(0xFF7C4DFF)
    val bubbleColorOther = Color(0xFFF0F0F0) // Cinza claro para os outros (melhor em fundo branco)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        // --- NOME DO REMETENTE ---
        if (!isMine) {
            Text(
                text = msg.senderName.ifBlank { stringResource(R.string.lbl_unknown) },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = nameColor,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            )
        }

        // --- BOLHA DE TEXTO ---
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
        ) {
            if (isMine) {
                Text(
                    text = timeFormatted,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                )
            }

            Surface(
                color = if (isMine) bubbleColorMine else bubbleColorOther,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isMine) 18.dp else 4.dp,
                    bottomEnd = if (isMine) 4.dp else 18.dp
                ),
                tonalElevation = 1.dp,
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = if (isMine) Color.White else Color(0xFF1F2937),
                    fontSize = 16.sp
                )
            }

            if (!isMine) {
                Text(
                    text = timeFormatted,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
        }
    }
}