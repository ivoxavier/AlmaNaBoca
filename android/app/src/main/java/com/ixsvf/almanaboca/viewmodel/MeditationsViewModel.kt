package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.repository.MeditationRepository
import com.ixsvf.almanaboca.ui.theme.states.MeditationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeditationsViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do Repositório (Ktor)
    private val repository = MeditationRepository()

    // Estado da UI (Começa em Loading)
    private val _uiState = MutableStateFlow<MeditationsUiState>(MeditationsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // CONSTANTES
    private val PLAYLIST_ID = "PLEYGOmHX7zjDgOwlwMAaA3qVJiP5SBsvD"
    private val API_KEY = "AIzaSyA-0LWipKoJ1nJJwLARHCw0BX-Loretq9g"

    // Bloco de inicialização: corre assim que o ViewModel é criado
    init {
        fetchMeditations()
    }

    fun fetchMeditations() {
        viewModelScope.launch {
            _uiState.value = MeditationsUiState.Loading

            try {
                // 1. Faz o pedido à API
                val response = repository.getMeditations(PLAYLIST_ID, API_KEY)

                // CORREÇÃO: Usa o operador elvis (?:) para garantir que não é nulo
                val videos = response.items ?: emptyList()

                // 2. Agora é seguro usar .size
                android.util.Log.d("ALMANABOCA_DEBUG", "Pedido feito. Vídeos encontrados: ${videos.size}")

                if (videos.isNotEmpty()) {
                    _uiState.value = MeditationsUiState.Success(videos)
                } else {
                    android.util.Log.e("ALMANABOCA_DEBUG", "Lista vazia! Verifique se a Playlist está como 'Não listada' (Unlisted) e não 'Privada'.")
                    _uiState.value = MeditationsUiState.Error("Nenhum vídeo encontrado.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MeditationsUiState.Error("Erro: ${e.message}")
            }
        }
    }
}