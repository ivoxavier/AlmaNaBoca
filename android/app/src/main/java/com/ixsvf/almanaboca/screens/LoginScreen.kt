package com.ixsvf.almanaboca.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.*
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.delay

// Cores para o gradiente moderno
private val BrandRedDark = Color(0xFF7A1212)
private val BrandRedLight = Color(0xFFC62828)
private val CardBg = Color.White.copy(alpha = 0.08f) // Efeito vidro (opcional se o fundo for escuro)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSplashFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            delay(1000)
            isSplashFinished = true
        }
    }

    // Fundo com Gradiente Linear
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(BrandRedLight, BrandRedDark)
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BrandRedDark // Cor de fallback
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {

                // --- SPLASH / LOADING INICIAL ---
                if (!isSplashFinished) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AlmanaBocaLogo(containerColor = Color.Transparent, elevation = 0.dp)
                    }
                } else {
                    // --- CONTEÚDO PRINCIPAL ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // Logo no topo
                        AlmanaBocaLogo(containerColor = Color.Transparent, elevation = 0.dp)

                        Spacer(modifier = Modifier.height(40.dp))

                        // Cartão do Formulário
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Bem-vindo(a)",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Inicia sessão para continuar",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Email
                                    OutlinedTextComponent(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = stringResource(R.string.lbl_account),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Password
                                    OutlinedTextComponent(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = stringResource(R.string.lbl_password),
                                        singleLine = true
                                    )

                                    // Exibição de Erro
                                    if (uiState is LoginUiState.Error) {
                                        Text(
                                            text = uiState.message,
                                            color = Color(0xFFFFCDD2), // Vermelho claro para erro
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Botão de Login
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LoginButton(
                                            label = stringResource(R.string.btn_login).uppercase(),
                                            onClick = {
                                                if (uiState !is LoginUiState.Loading) {
                                                    onLoginClick(email, password)
                                                }
                                            },
                                            enabled = uiState !is LoginUiState.Loading && email.isNotEmpty() && password.isNotEmpty()
                                        )

                                        if (uiState is LoginUiState.Loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Texto de Ajuda
                        Text(
                            text = stringResource(R.string.lbl_lost_password),
                            // Modifier adicional se necessário para aumentar área de clique
                        )
                    }
                }
            }
        }
    }
}