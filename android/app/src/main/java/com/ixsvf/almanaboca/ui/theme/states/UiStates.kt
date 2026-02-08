package com.ixsvf.almanaboca.ui.theme.states

import com.google.firebase.auth.FirebaseUser
import com.ixsvf.almanaboca.services.model.HomeItem

// Mudado para 'interface' para ser mais limpo e igual ao HomeUiState
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: FirebaseUser?) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val courses: List<HomeItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}