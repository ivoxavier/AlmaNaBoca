package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.screens.menusubscreens.BookingItemUI
import com.ixsvf.almanaboca.screens.menusubscreens.BookingStatus
import com.ixsvf.almanaboca.services.model.Booking
import com.ixsvf.almanaboca.services.repository.BookingRepository
import com.ixsvf.almanaboca.services.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository()
    private val userPreferences = UserPreferences(application)

    // LISTA DE ADMINS
    private val ADMIN_EMAILS = listOf(AlmanaBocaConstants.ADMINS.MARTA, AlmanaBocaConstants.ADMINS.IVO)

    // Estados
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    private val _bookingsList = MutableStateFlow<List<BookingItemUI>>(emptyList())
    val bookingsList = _bookingsList.asStateFlow()

    private val _occupiedSlots = MutableStateFlow<List<String>>(emptyList())
    val occupiedSlots = _occupiedSlots.asStateFlow()

    private val _bookingResult = MutableStateFlow<String?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    val availableHours = listOf("09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00")

    init {
        checkUserAndLoad()
    }

    private fun checkUserAndLoad() {
        viewModelScope.launch {
            _isLoading.value = true
            val email = userPreferences.userEmail.first() ?: ""

            // Verifica se é Admin
            val isUserAdmin = ADMIN_EMAILS.contains(email.trim().lowercase())
            _isAdmin.value = isUserAdmin

            if (isUserAdmin) {
                // --- NOVIDADE AQUI ---
                // 1. Primeiro faz a limpeza das reservas antigas
                repository.deletePastBookings()

                // 2. Depois carrega a lista (já limpa)
                loadAllBookings()
            } else {
                // SE FOR CLIENTE: Carrega só as suas
                loadUserBookings(email)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadAllBookings() {
        val bookings = repository.getAllBookings()
        _bookingsList.value = mapBookingsToUI(bookings)
    }

    private suspend fun loadUserBookings(email: String) {
        if (email.isNotEmpty()) {
            val bookings = repository.getUserBookings(email)
            _bookingsList.value = mapBookingsToUI(bookings)
        }
    }

    // AÇÕES DE ADMINISTRAÇÃO (Long Press)
    fun approveBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.updateBookingStatus(bookingId, "confirmed")
            if (success) {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_made)
                refreshData() // Recarrega a lista
            } else {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_error_on_approval)
            }
            _isLoading.value = false
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteBooking(bookingId)
            if (success) {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_deleted)
                refreshData()
            } else {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_deleted_error)
            }
            _isLoading.value = false
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            checkUserAndLoad()
        }
    }

    // --- Criação de Reservas ---
    fun onDateSelected(dateMillis: Long) {
        val dateString = convertMillisToDate(dateMillis)
        fetchOccupiedSlots(dateString)
    }

    private fun fetchOccupiedSlots(date: String) {
        viewModelScope.launch {
            // Admin não precisa disto, mas o cliente sim
            val bookings = repository.getBookingsForDate(date)
            _occupiedSlots.value = bookings.map { it.time }
        }
    }

    fun createBooking(dateMillis: Long, time: String, name: String, phone: String, service: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val email = userPreferences.userEmail.first() ?: ""
            val dateString = convertMillisToDate(dateMillis)

            val newBooking = Booking(
                date = dateString,
                time = time,
                clientName = name,
                clientEmail = email,
                clientPhone = phone,
                serviceType = service,
                status = "pending" // Começa sempre pendente
            )

            if (repository.createBooking(newBooking)) {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_request_made)
                refreshData()
            } else {
                _bookingResult.value = getApplication<Application>().getString(R.string.lbl_booking_hour_error)
            }
            _isLoading.value = false
        }
    }

    fun clearResult() { _bookingResult.value = null }

    // Helpers
    private fun mapBookingsToUI(list: List<Booking>): List<BookingItemUI> {
        return list.map {
            BookingItemUI(
                id = it.bookingId,
                serviceName = it.serviceType,
                clientName = it.clientName, // Adicionado campo para o Admin ver quem é
                date = formatDateDisplay(it.date),
                time = it.time,
                status = if (it.status == "confirmed") BookingStatus.CONFIRMED else BookingStatus.PENDING
            )
        }
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    private fun formatDateDisplay(dateString: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM", Locale("pt", "PT")) // "20 Fev"
            val date = input.parse(dateString)
            output.format(date ?: Date())
        } catch (e: Exception) { dateString }
    }
}