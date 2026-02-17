package com.ixsvf.almanaboca.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

// Cores atualizadas para o tema claro
private val BrandRedMain = Color(0xFFC62828) // Vermelho da marca para botões/detalhes

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

    // Estado para controlar o Scroll
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            delay(1000)
            isSplashFinished = true
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White // Fundo Branco
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Garante fundo branco
        ) {
            // Mudei o ContentColor para Preto porque o fundo é branco
            CompositionLocalProvider(LocalContentColor provides Color.Black) {

                if (!isSplashFinished) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // LOGO NO SPLASH
                        Image(
                            painter = painterResource(id = R.drawable.almanaboca_white),
                            contentDescription = "Logo",
                            modifier = Modifier.size(200.dp), // Ajusta o tamanho conforme necessário
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .imePadding()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // LOGO NO TOPO DO FORMULÁRIO
                        Image(
                            painter = painterResource(id = R.drawable.almanaboca_white),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(120.dp) // Altura controlada
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Cartão do Formulário
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                // Cor de fundo do cartão subtil (cinza muito claro) para contraste
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra suave
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Bem-vindo(a)",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black // Texto Preto
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Inicia sessão para continuar",
                                        fontSize = 14.sp,
                                        color = Color.Gray // Texto Cinza
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Email
                                    OutlinedTextComponent(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = stringResource(R.string.lbl_account),
                                        singleLine = true
                                        // Nota: Verifica se o OutlinedTextComponent suporta cores escuras por defeito,
                                        // senão terás de passar a cor do texto/label para preto lá dentro.
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Password
                                    OutlinedTextComponent(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = stringResource(R.string.lbl_password),
                                        singleLine = true
                                    )

                                    if (uiState is LoginUiState.Error) {
                                        Text(
                                            text = uiState.message,
                                            color = BrandRedMain, // Vermelho para erro
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

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
                                                color = BrandRedMain,
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.lbl_lost_password),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}