package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado da UI (Loading, Success, Error)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Estado do Utilizador Atual (para verificar admins, etc)
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    fun login(email: String, pass: String) {
        // 1. Validação básica
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = LoginUiState.Error("Preenche todos os campos")
            return
        }

        // 2. Iniciar Loading
        _uiState.value = LoginUiState.Loading

        // 3. Tentar Login no Firebase
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    // SUCESSO!
                    val user = result.user
                    _currentUser.value = user
                    // Atualiza o estado para Success para o MainActivity navegar
                    _uiState.value = LoginUiState.Success(user)
                }
                .addOnFailureListener { exception ->
                    // ERRO!
                    _uiState.value = LoginUiState.Error(exception.message ?: "Erro ao entrar")
                }
        }
    }

    // Função para fazer logout (útil para o menu)
    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _uiState.value = LoginUiState.Idle
    }
}