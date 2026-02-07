package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.services.model.Booking
import com.ixsvf.almanaboca.services.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository()

    // Estado da UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Lista de horários que JÁ ESTÃO OCUPADOS no dia selecionado
    private val _occupiedSlots = MutableStateFlow<List<String>>(emptyList())
    val occupiedSlots = _occupiedSlots.asStateFlow()

    // Mensagens de sucesso ou erro (para Toasts ou Snackbars)
    private val _bookingResult = MutableStateFlow<String?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    // Horários de funcionamento (Podes ajustar conforme a tua necessidade)
    val availableHours = listOf(
        "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"
    )

    // 1. Quando o utilizador escolhe uma data no calendário
    fun onDateSelected(dateMillis: Long) {
        val dateString = convertMillisToDate(dateMillis)
        fetchOccupiedSlots(dateString)
    }

    // 2. Vai ao Firebase ver o que está ocupado
    private fun fetchOccupiedSlots(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Vai buscar as reservas confirmadas para esse dia
            val bookings = repository.getBookingsForDate(date)

            // Cria uma lista só com as horas (ex: ["10:00", "14:00"])
            _occupiedSlots.value = bookings.map { it.time }
            _isLoading.value = false
        }
    }

    // 3. Criar uma nova reserva
    fun createBooking(
        dateMillis: Long,
        time: String,
        name: String,
        email: String,
        phone: String,
        service: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val dateString = convertMillisToDate(dateMillis)

            val newBooking = Booking(
                date = dateString,
                time = time,
                clientName = name,
                clientEmail = email,
                clientPhone = phone,
                serviceType = service,
                status = "confirmed" // Ou "pending" se quiseres aprovar manualmente
            )

            val success = repository.createBooking(newBooking)

            if (success) {
                _bookingResult.value = "Reserva confirmada com sucesso!"
                // Atualiza a lista para bloquear o horário imediatamente
                fetchOccupiedSlots(dateString)
            } else {
                _bookingResult.value = "Erro: Este horário já foi ocupado por outra pessoa."
            }
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _bookingResult.value = null
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}