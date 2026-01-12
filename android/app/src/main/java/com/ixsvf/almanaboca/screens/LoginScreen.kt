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
fun LoginScreen(modifier: Modifier = Modifier,
                uiState: LoginUiState,
                onLoginClick: (String, String) -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopPageText(label = stringResource(R.string.lbl_welcome))

        BlankSpace(4)

        TopPageText(label = stringResource(R.string.app_name))

        BlankSpace(4)

        SummaryTopPageText(label=stringResource(id=R.string.lbl_login_to_account))

        BlankSpace(16)

        OutlinedTextComponent(loginValue,{loginViewModel.onLoginChanged(it)},label = stringResource(R.string.lbl_account), true)

        BlankSpace(16)

        OutlinedTextComponent(passwordValue,{loginViewModel.onPasswordChanged(it)},label = stringResource(R.string.lbl_password), false)

        BlankSpace(16)

        Box(contentAlignment = Alignment.Center){
            LoginButton(label = stringResource(R.string.btn_login), onClick = {
                if(loginState !is LoginUiState.Loading){
                    loginViewModel.performLogin()
                }
            }, enabled = loginState !is LoginUiState.Loading)

            if (loginState is LoginUiState.Loading){
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        BlankSpace(32)

        HelpText(stringResource(R.string.lbl_lost_password))
    }
}