package com.ixsvf.almanaboca.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.AlmanaBocaLogo
import com.ixsvf.almanaboca.screens.components.BlankSpace
import com.ixsvf.almanaboca.screens.components.HelpText
import com.ixsvf.almanaboca.screens.components.LoginButton
import com.ixsvf.almanaboca.screens.components.OutlinedTextComponent
import com.ixsvf.almanaboca.screens.components.TopPageText
import com.ixsvf.almanaboca.screens.components.rememberLogoBitmap
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.delay

private val BrandRed = Color(0xFF9E1919)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSplashFinished by remember { mutableStateOf(false) }

    // 1. REDUÇÃO DO TEMPO DE ESPERA
    // De 3000ms (3s) para 1500ms (1.5s) -> O utilizador chega ao login mais rápido
    LaunchedEffect(Unit) {
        delay(1500)
        isSplashFinished = true
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BrandRed
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {

            if (!isSplashFinished) {
                // --- FASE 1: SPLASH ---
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
                // --- FASE 2: LOGIN ---
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

                    // 2. ANIMAÇÃO MAIS RÁPIDA E SINCRONIZADA
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            // 400ms é um tempo "snappy" (rápido mas visível)
                            animationSpec = tween(400)
                        ) + slideInVertically(
                            // IMPORTANTE: Aplicar o mesmo tween aqui para evitar o efeito "mola" lento
                            animationSpec = tween(400),
                            initialOffsetY = { 50 } // Desliza 50 pixeis para cima
                        )
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