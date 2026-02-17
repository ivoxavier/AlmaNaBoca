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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidade", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id } // Usar o ID como chave ajuda o Compose nas animações
                ) { msg ->
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

            // --- INPUT DE MENSAGEM ARREDONDADO (MODERNO) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(12.dp) // Espaço entre a cápsula e as bordas do telemóvel
                    .navigationBarsPadding()
                    .imePadding(),
                tonalElevation = 8.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp), // Padding interno da cápsula
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escreve algo...", color = Color.Gray) },
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
                                    currentUser?.displayName ?: "Utilizador"
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
                            contentDescription = "Enviar",
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

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (!isMine) {
            Text(
                text = msg.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
        ) {
            if (isMine) {
                Text(timeFormatted, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(end = 6.dp, bottom = 2.dp))
            }

            Surface(
                color = if (isMine) AccentPurple else Color.White,
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isMine) 16.dp else 2.dp,
                    bottomEnd = if (isMine) 2.dp else 16.dp
                ),
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (isMine) Color.White else TextDark,
                    fontSize = 15.sp
                )
            }

            if (!isMine) {
                Text(timeFormatted, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 6.dp, bottom = 2.dp))
            }
        }
    }
}