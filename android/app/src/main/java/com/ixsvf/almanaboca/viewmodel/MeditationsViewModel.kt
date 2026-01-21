package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MeditationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()
}