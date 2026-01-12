package com.ixsvf.almanaboca.ui.theme.states

sealed class LoginUiState
{
    object Idle: LoginUiState()
    object Loading: LoginUiState()
    data class Error(val message: String): LoginUiState()
}