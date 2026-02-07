package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.model.UserProfile
import com.ixsvf.almanaboca.services.repository.UserPreferences // Importante
import com.ixsvf.almanaboca.services.repository.UsersRepository
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application): AndroidViewModel(application) {

    private val usersRepository = UsersRepository(application)

    // 1. Inicializamos as Preferências
    private val userPreferences = UserPreferences(application)

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

                val firebaseUser = usersRepository.signInWithEmailAndPassword(cleanEmail, cleanPassword)

                if (firebaseUser != null) {
                    val name = firebaseUser.displayName ?: "Utilizador"

                    _currentUser.value = UserProfile(
                        id = firebaseUser.uid,
                        name = name,
                        email = firebaseUser.email ?: cleanEmail,
                        isActive = true
                    )

                    // 2. SUCESSO! Guardamos o email no DataStore para usar nas Reservas
                    userPreferences.saveUserSession(cleanEmail, name)

                    _uiState.value = LoginUiState.Idle
                } else {
                    _uiState.value = LoginUiState.Error("Login falhou: Utilizador nulo.")
                }
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

    // Função extra útil para Logout
    fun logout() {
        viewModelScope.launch {
            //usersRepository.signOut() // Assumindo que tem este método no repo
            userPreferences.clearSession()
            _currentUser.value = null
        }
    }
}