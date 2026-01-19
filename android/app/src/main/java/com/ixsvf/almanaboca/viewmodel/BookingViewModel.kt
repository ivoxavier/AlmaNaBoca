package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    // Começa em Loading
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Assim que o ViewModel é criado, iniciamos o "carregamento"
        fetchBookings()
    }

    private fun fetchBookings() {
        viewModelScope.launch {
            // 1. Opcional: Simula um pequeno atraso de rede (ex: 1 segundo) para ver o loading
            delay(1000)

            try {
                // 2. MUDANÇA DE ESTADO: Passamos para Success
                // Como na BookingScreen estamos a usar uma lista dummy local por enquanto,
                // podemos passar uma lista vazia aqui apenas para ativar o estado de Sucesso.
                _uiState.value = HomeUiState.Success(emptyList())

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Erro ao carregar marcações")
            }
        }
    }
}