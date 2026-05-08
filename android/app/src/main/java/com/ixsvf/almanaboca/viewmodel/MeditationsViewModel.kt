package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.almanaboca.services.model.QuoteItem
import com.ixsvf.almanaboca.services.repository.MeditationRepository
import com.ixsvf.almanaboca.ui.theme.states.MeditationsUiState
import com.ixsvf.almanaboca.ui.theme.states.QuoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MeditationsViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do Repositório (Ktor)
    private val repository = MeditationRepository()

    // Estado da UI (Começa em Loading)
    private val _uiState = MutableStateFlow<MeditationsUiState>(MeditationsUiState.Loading)
    private val _quoteState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val quoteState = _quoteState.asStateFlow()


    private val firestore = FirebaseFirestore.getInstance()
    private val prefs = application.getSharedPreferences("AlmanaBocaPrefs", Context.MODE_PRIVATE)

    val uiState = _uiState.asStateFlow()

    // CONSTANTES
    private val PLAYLIST_ID = "PLEYGOmHX7zjDgOwlwMAaA3qVJiP5SBsvD"
    private val API_KEY = "AIzaSyA-0LWipKoJ1nJJwLARHCw0BX-Loretq9g"

    // Bloco de inicialização: corre assim que o ViewModel é criado
    init {
        fetchMeditations()
        fetchDailyQuote()
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

    private fun fetchDailyQuote() {
        try {
            // 1. Obter a data de forma segura para qualquer versão Android
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val today = formatter.format(java.util.Date())

            val savedDate = prefs.getString("quote_date", "")

            if (savedDate == today) {
                val savedText = prefs.getString("quote_text", "") ?: ""
                val savedAuthor = prefs.getString("quote_author", "") ?: ""
                _quoteState.value = QuoteUiState.Success(QuoteItem(savedText, savedAuthor))
            } else {
                // 2. Tentar ir ao Firebase com tratamento de erros
                firestore.collection("daily_quotes").get()
                    .addOnSuccessListener { snapshot ->
                        try {
                            if (!snapshot.isEmpty) {
                                val quotes = snapshot.toObjects(QuoteItem::class.java)
                                if (quotes.isNotEmpty()) {
                                    val randomQuote = quotes.random()

                                    // Guardar na memória
                                    prefs.edit()
                                        .putString("quote_date", today)
                                        .putString("quote_text", randomQuote.text)
                                        .putString("quote_author", randomQuote.author)
                                        .apply()

                                    _quoteState.value = QuoteUiState.Success(randomQuote)
                                } else {
                                    _quoteState.value = QuoteUiState.Error
                                }
                            } else {
                                android.util.Log.e("ALMANABOCA_QUOTE", "A coleção está vazia!")
                                _quoteState.value = QuoteUiState.Error
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ALMANABOCA_QUOTE", "Erro a converter frase: ${e.message}")
                            _quoteState.value = QuoteUiState.Error
                        }
                    }
                    .addOnFailureListener { exception ->
                        android.util.Log.e("ALMANABOCA_QUOTE", "Falha de Permissão ou Rede no Firebase: ${exception.message}")
                        _quoteState.value = QuoteUiState.Error
                    }
            }
        } catch (e: Exception) {
            android.util.Log.e("ALMANABOCA_QUOTE", "Erro geral na data/prefs: ${e.message}")
            _quoteState.value = QuoteUiState.Error
        }
    }
}