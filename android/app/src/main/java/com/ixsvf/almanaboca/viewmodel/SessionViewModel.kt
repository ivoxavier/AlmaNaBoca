package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.model.UserProfile
import com.ixsvf.almanaboca.services.repository.UsersRepository
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application): AndroidViewModel(application) {
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
                // Tenta fazer login no Firebase

                val cleanEmail = email.trim()
                val cleanPassword = pass.trim()

                // Faz o login
                signInWithEmailAndPassword(cleanEmail, cleanPassword)

                // SUCESSO! Vamos atualizar o currentUser manualmente para a UI reagir
                val firebaseUser =
                if (firebaseUser != null) {
                    _currentUser.value = UserProfile(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Utilizador",
                        email = firebaseUser.email ?: cleanEmail,
                        isActive = true
                    )
                }

                // Se sucesso, atualiza estado (a navegação vai reagir ao currentUser, não necessita de estado Success aqui)
                _uiState.value = LoginUiState.Idle
            } catch (e: Exception) {
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