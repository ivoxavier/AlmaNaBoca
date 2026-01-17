package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser // Importante
import com.ixsvf.almanaboca.services.model.UserProfile
import com.ixsvf.almanaboca.services.repository.UsersRepository
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application): AndroidViewModel(application) {
    // Inicializa o repositório
    private val usersRepository = UsersRepository(application)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser = _currentUser.asStateFlow()

    private var sessionJob: Job? = null

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val cleanEmail = email.trim()
                val cleanPassword = pass.trim()

                // --- CORREÇÃO AQUI ---
                // 1. Chamar através do usersRepository
                // 2. Atribuir o resultado à variável firebaseUser
                val firebaseUser = usersRepository.signInWithEmailAndPassword(cleanEmail, cleanPassword)

                if (firebaseUser != null) {
                    _currentUser.value = UserProfile(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Utilizador",
                        email = firebaseUser.email ?: cleanEmail,
                        isActive = true
                    )
                    // Sucesso: Volta ao estado Idle (a navegação observará o currentUser)
                    _uiState.value = LoginUiState.Idle
                } else {
                    _uiState.value = LoginUiState.Error("Login falhou: Utilizador nulo.")
                }
            } catch (e: Exception) {
                // Captura erros do Firebase (senha errada, user não existe, etc)
                _uiState.value = LoginUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun startSession(userId: String) {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            // Lógica de sessão...
        }
    }
}