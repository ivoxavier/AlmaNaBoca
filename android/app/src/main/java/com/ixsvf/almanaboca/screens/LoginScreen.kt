package com.ixsvf.almanaboca.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth // <--- IMPORTANTE
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.*
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.delay

private val BrandRed = Color(0xFF9E1919)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToHome: () -> Unit // <--- NOVO PARÂMETRO: Para ir para a Home automaticamente
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSplashFinished by remember { mutableStateOf(false) }

    // VERIFICAÇÃO DE LOGIN AUTOMÁTICO
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Se já existe utilizador logado, vai direto para a Home
            onNavigateToHome()
        } else {
            // Se não existe, espera a animação do Splash e mostra o login
            delay(1400)
            isSplashFinished = true
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BrandRed
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {

            // Se o splash não acabou (ou se está a redirecionar), mostra só o Logo
            if (!isSplashFinished) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val logoBitmap = rememberLogoBitmap()
                    if (logoBitmap != null) {
                        Image(
                            bitmap = logoBitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            } else {
                // --- MOSTRA O FORMULÁRIO (Apenas se não houver login automático) ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AlmanaBocaLogo(
                        containerColor = Color.Transparent,
                        elevation = 0.dp
                    )

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(animationSpec = tween(400), initialOffsetY = { 50 })
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            BlankSpace(30)

                            OutlinedTextComponent(
                                value = email,
                                onValueChange = { email = it },
                                label = stringResource(R.string.lbl_account),
                                singleLine = true
                            )

                            BlankSpace(16)

                            OutlinedTextComponent(
                                value = password,
                                onValueChange = { password = it },
                                label = stringResource(R.string.lbl_password),
                                singleLine = true
                            )

                            BlankSpace(24)

                            Box(contentAlignment = Alignment.Center) {
                                LoginButton(
                                    label = stringResource(R.string.btn_login),
                                    onClick = {
                                        if (uiState !is LoginUiState.Loading) {
                                            onLoginClick(email, password)
                                        }
                                    },
                                    enabled = uiState !is LoginUiState.Loading
                                )

                                if (uiState is LoginUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color.White
                                    )
                                }
                            }

                            BlankSpace(32)

                            HelpText(stringResource(R.string.lbl_lost_password))
                        }
                    }
                }
            }
        }
    }
}