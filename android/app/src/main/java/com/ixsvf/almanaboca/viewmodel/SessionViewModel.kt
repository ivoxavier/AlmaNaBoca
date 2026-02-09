package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore // <--- IMPORTANTE: Faltava este import
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --- CORREÇÃO: Inicializar o Firestore aqui ---
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    fun updateFcmToken(email: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            // 1. Obter o Token atual deste telemóvel
            val token = task.result
            val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

            // 2. Guardar o token no Firestore (para sabermos onde notificar este user)
            val data = hashMapOf("fcmToken" to token, "email" to email)

            // Usa set com merge para não apagar outros dados do user se já existirem
            // AGORA VAI FUNCIONAR PORQUE 'db' JÁ EXISTE
            db.collection("users").document(uid)
                .set(data, SetOptions.merge())

            // 3. Lógica Especial de Admin
            // Se este email for de um admin, ele tem de escutar o canal "admin_notifications"
            val admins = listOf("martamartins340@gmail.com", "ivofernandes12@gmail.com")

            if (admins.contains(email)) {
                FirebaseMessaging.getInstance().subscribeToTopic("admin_notifications")
                    .addOnSuccessListener {
                        android.util.Log.d("FCM", "Admin subscrito nas notificações globais")
                    }
            } else {
                // Se não for admin (ou deixou de ser), remove a subscrição para não receber spam
                FirebaseMessaging.getInstance().unsubscribeFromTopic("admin_notifications")
            }
        }
    }
}