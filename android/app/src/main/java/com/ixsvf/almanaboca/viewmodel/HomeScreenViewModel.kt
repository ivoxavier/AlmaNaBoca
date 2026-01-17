package com.ixsvf.almanaboca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.model.HomeItem
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // SIMULAÇÃO: Criar uma lista com vários cursos para testar o carousel

                val item1 = HomeItem(
                    id = "1",
                    coachProgram = "Coaching Executivo",
                    whatToExpectProgram = "Focado em liderança e gestão de equipas de alta performance.",
                    coachDateStart = "01/02/2026",
                    coachDateEnd = "01/03/2026",
                    coachVacancies = 8,
                    coachDiscount = 20.0
                )

                val item2 = HomeItem(
                    id = "2",
                    coachProgram = "Carreira & Propósito",
                    whatToExpectProgram = "Descobre o teu caminho profissional e alinha os teus objetivos de vida.",
                    coachDateStart = "15/03/2026",
                    coachDateEnd = "15/05/2026",
                    coachVacancies = 3,
                    coachDiscount = 0.0 // Sem desconto
                )

                val item3 = HomeItem(
                    id = "3",
                    coachProgram = "Inteligência Emocional",
                    whatToExpectProgram = "Aprende a gerir emoções e melhorar relacionamentos no ambiente de trabalho.",
                    coachDateStart = "01/06/2026",
                    coachDateEnd = "01/07/2026",
                    coachVacancies = 12,
                    coachDiscount = 15.0
                )

                // CORREÇÃO: Passamos uma lista (listOf) em vez de um objeto único
                _uiState.value = HomeUiState.Success(listOf(item1, item2, item3))

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Erro ao carregar dados")
            }
        }
    }
}