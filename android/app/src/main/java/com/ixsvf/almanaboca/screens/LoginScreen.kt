package com.ixsvf.almanaboca.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.BlankSpace
import com.ixsvf.almanaboca.screens.components.HelpText
import com.ixsvf.almanaboca.screens.components.LoginButton
import com.ixsvf.almanaboca.screens.components.OutlinedTextComponent
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.screens.components.TopPageText
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit // Callback para devolver os dados
) {
    // Estado local para gerir o texto enquanto o utilizador digita
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopPageText(label = stringResource(R.string.lbl_welcome))
        BlankSpace(4)
        TopPageText(label = stringResource(R.string.app_name))
        BlankSpace(4)
        SummaryTopPageText(label = stringResource(id = R.string.lbl_login_to_account))
        BlankSpace(16)

        // CAMPO EMAIL
        OutlinedTextComponent(
            value = email,
            onValueChange = { email = it }, // Atualiza a variável local
            label = stringResource(R.string.lbl_account),
            singleLine = true // Verifique se o seu componente usa 'isEmail' ou 'singleLine'
        )

        BlankSpace(16)

        // CAMPO PASSWORD
        OutlinedTextComponent(
            value = password,
            onValueChange = { password = it }, // Atualiza a variável local
            label = stringResource(R.string.lbl_password),
            singleLine = true // Verifique se o seu componente usa 'isEmail' ou 'singleLine'
        )

        BlankSpace(16)

        Box(contentAlignment = Alignment.Center) {
            LoginButton(
                label = stringResource(R.string.btn_login),
                onClick = {
                    if (uiState !is LoginUiState.Loading) {
                        // Envia os dados locais para o ViewModel via callback
                        onLoginClick(email, password)
                    }
                },
                enabled = uiState !is LoginUiState.Loading
            )

            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        BlankSpace(32)

        HelpText(stringResource(R.string.lbl_lost_password))
    }
}