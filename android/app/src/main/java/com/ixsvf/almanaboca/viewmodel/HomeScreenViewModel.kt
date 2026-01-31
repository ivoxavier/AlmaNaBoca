package com.ixsvf.almanaboca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.repository.HomeRepository
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Inicializa o Repositório
    private val repository = HomeRepository()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // 1. Chama o Firebase através do repositório
                val items = repository.getHomeItems()

                // 2. Verifica se vieram dados
                if (items.isEmpty()) {
                    // Opcional: Pode manter Success com lista vazia
                    // ou criar um estado específico de "Empty"
                    _uiState.value = HomeUiState.Success(emptyList())
                } else {
                    _uiState.value = HomeUiState.Success(items)
                }

            } catch (e: Exception) {
                // Erro comum: Permissões do Firestore ou Falta de Internet
                _uiState.value = HomeUiState.Error(e.message ?: "Erro ao carregar dados do Firebase")
            }
        }
    }
}