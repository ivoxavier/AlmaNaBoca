package com.ixsvf.almanaboca.ui.theme.states

import com.ixsvf.almanaboca.services.model.HomeItem

sealed class LoginUiState
{
    object Idle: LoginUiState()
    object Loading: LoginUiState()
    data class Error(val message: String): LoginUiState()
}


sealed interface HomeUiState {
    object Loading : HomeUiState
    // MUDANÇA AQUI: 'data' agora é uma List<HomeItem>
    data class Success(val courses: List<HomeItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}