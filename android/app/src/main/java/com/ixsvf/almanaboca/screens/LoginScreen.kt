package com.ixsvf.almanaboca.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.*
import com.ixsvf.almanaboca.ui.theme.BrandRedMain
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch



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

    // Estado para controlar os Dialogs
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "v${packageInfo.versionName}"
        } catch (e: Exception) {
            ""
        }
    }



    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            email = ""
            password = ""
            delay(1000)
            isSplashFinished = true
        }
    }

    // --- DIALOGS ---
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = email, // Passa o email que o user já escreveu (se houver)
            onDismiss = { showForgotPasswordDialog = false }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.Black) {

                if (!isSplashFinished) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.almanaboca_white),
                            contentDescription = "Logo",
                            modifier = Modifier.size(280.dp),
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

                        Image(
                            painter = painterResource(id = R.drawable.almanaboca_white),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(190.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )



                        // TIP: SIGAM ATOMIC DESIGN SEMPRE QUE POSSIVEL
                        // POR MAIS ESTUPIDO QUE POSSA PARECER
                        //Spacer(modifier = Modifier.height(10.dp))
                        BlankSpace(10)


                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    //Spacer(modifier = Modifier.height(8.dp))
                                    BlankSpace(8)

                                    Text(
                                        text = stringResource(R.string.lbl_login_to_account),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    //Spacer(modifier = Modifier.height(32.dp))
                                    BlankSpace(32)

                                    LoginInputText(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = stringResource(R.string.lbl_account),
                                        icon = Icons.Outlined.Email // Ícone de carta/email
                                    )

                                    //Spacer(modifier = Modifier.height(16.dp))
                                    BlankSpace(16)

                                    LoginInputText(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = stringResource(R.string.lbl_password),
                                        icon = Icons.Outlined.Lock, // Ícone de cadeado
                                        isPassword = true // <-- Muito importante para esconder o texto e mostrar o olhinho
                                    )

                                    if (uiState is LoginUiState.Error) {
                                        Text(
                                            text = uiState.message,
                                            color = BrandRedMain,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    //Spacer(modifier = Modifier.height(32.dp))
                                    BlankSpace(32)

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

                                    //Spacer(modifier = Modifier.height(16.dp))
                                    BlankSpace(16)
                                    PrivacyPolicyText(
                                        onClick = { showPrivacyDialog = true }
                                    )
                                }
                            }
                        }

                        //Spacer(modifier = Modifier.height(20.dp))
                        BlankSpace(16)

                        // --- LINK RECUPERAR PASSWORD ---
                        Text(
                            text = stringResource(R.string.lbl_lost_password),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { showForgotPasswordDialog = true } // Abre o popup
                                .padding(8.dp),
                            textDecoration = TextDecoration.Underline // Sublinhado para parecer link
                        )

                        //Spacer(modifier = Modifier.height(5.dp))
                        BlankSpace(5)


                        Text(
                            text = appVersion,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = stringResource(R.string.lbl_developed_by) + " Ivo Xavier <ixsvf>",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- NOVO DIALOG: RECUPERAR PASSWORD ---
@Composable
fun ForgotPasswordDialog(
    initialEmail: String,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val msgTypeEmail = stringResource(R.string.lbl_type_email)
    val msgCheckEmail = stringResource(R.string.lbl_check_your_email)
    val msgError = stringResource(R.string.lbl_error)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.lbl_recover_password), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(stringResource(R.string.lbl_recover_password_email), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.lbl_email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                if (successMessage != null) {
                    Text(text = successMessage!!, color = Color(0xFF2E7D32), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = msgTypeEmail
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        try {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                            successMessage = msgCheckEmail
                            isLoading = false
                            delay(2000) // Espera 2s para lerem a mensagem e fecha
                            onDismiss()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = msgError + " ${e.message}"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandRedMain),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(16.dp))
                } else {
                    Text(stringResource(R.string.lbl_send_email))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.lbl_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun PrivacyPolicyText(onClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.lbl_rgpd_label_1))


        val link = LinkAnnotation.Clickable(
            tag = "privacy_policy",
            styles = TextLinkStyles(
                style = SpanStyle(
                    color = BrandRedMain,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ),
            linkInteractionListener = {
                // Quando clicado, executa a ação
                onClick()
            }
        )

        // Aplicamos o link ao texto específico
        withLink(link) {
            append(" " + stringResource(R.string.lbl_rgpd_label_2) + " " )
        }

        append(stringResource(R.string.lbl_rgpd_label_3))
    }

    // Agora usamos o Text normal (o ClickableText desaparece)
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 11.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.lbl_rgpd_label_2), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // Limita a altura
                    .verticalScroll(rememberScrollState()) // Permite scroll se o texto for longo
            ) {
                Text(
                    text =stringResource(R.string.lbl_rgpd_disclaimer).trimIndent(),
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandRedMain)
            ) {
                Text(stringResource(R.string.lbl_close), color = MaterialTheme.colorScheme.surface)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}