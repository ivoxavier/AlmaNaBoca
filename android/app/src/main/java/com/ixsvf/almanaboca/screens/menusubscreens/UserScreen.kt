package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ixsvf.almanaboca.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val currentUser by sessionViewModel.currentUser.collectAsState()

    // 1. Ler o estado do acesso ao Chat
    val hasChatAccess by sessionViewModel.canAccessChat

    // 2. Ler o Nome Real (que configurámos antes)
    val realName by sessionViewModel.userName.collectAsState()

    // Atualiza a verificação sempre que entra neste ecrã
    LaunchedEffect(Unit) {
        sessionViewModel.checkChatAccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("A minha conta", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- INFO DO UTILIZADOR ---
            // Ícone de Perfil Genérico (Opcional, fica bonito)
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50),
                color = Color(0xFFE0E0E0)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = (realName.firstOrNull() ?: '?').toString().uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome
            Text(
                text = if (realName.isNotBlank()) realName else (currentUser?.displayName ?: "Utilizador"),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // Email
            Text(
                text = currentUser?.email ?: "",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- CARTÃO DE STATUS DA COMUNIDADE ---
            StatusCard(isActive = hasChatAccess)

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÃO DE LOGOUT ---
            Button(
                onClick = {
                    sessionViewModel.signOut()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("TERMINAR SESSÃO", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatusCard(isActive: Boolean) {
    val containerColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Verde claro vs Vermelho claro
    val contentColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828) // Verde escuro vs Vermelho escuro
    val icon = if (isActive) Icons.Default.CheckCircle else Icons.Default.Lock
    val statusText = if (isActive) "ATIVO" else "INATIVO"
    val description = if (isActive) "Tens acesso exclusivo ao chat da comunidade." else "Subscrição necessária para aceder."

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Comunidade AlmanaBoca",
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}