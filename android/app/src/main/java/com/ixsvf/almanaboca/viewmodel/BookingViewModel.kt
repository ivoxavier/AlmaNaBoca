package com.ixsvf.almanaboca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.almanaboca.services.model.Booking // Confirme se a sua classe é Booking ou Bookings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // --- ESTADOS ---
    private val _bookingsList = MutableStateFlow<List<Booking>>(emptyList())
    val bookingsList = _bookingsList.asStateFlow()

    private val _occupiedSlots = MutableStateFlow<List<String>>(emptyList())
    val occupiedSlots = _occupiedSlots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bookingResult = MutableStateFlow<String?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    // Verifica se é Admin
    val isAdmin = auth.currentUser?.email?.let {
        it == "martamartins340@gmail.com" || it == "ivofernandes12@gmail.com"
    } ?: false

    fun clearResult() { _bookingResult.value = null }

    // -----------------------------------------------------------------
    // 1. CARREGAMENTO DA LISTA (O SEU PEDIDO)
    // -----------------------------------------------------------------

    // ADMIN: Vê tudo o que está Pendente (Mantém-se igual, pois estava OK)
    fun loadAllPendingBookings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val snapshot = db.collection("bookings")
                    .whereEqualTo("status", "pending")
                    .get().await()
                _bookingsList.value = snapshot.toObjects(Booking::class.java)
            } catch (e: Exception) { e.printStackTrace() }
            finally { _isLoading.value = false }
        }
    }

    // CLIENTE:
    // 1. Vê as SUAS marcações (Pendente OU Confirmada).
    // 2. Se a data já passou (ontem ou antes), desaparece da lista.
    fun loadUserBookings() {
        val currentUserEmail = auth.currentUser?.email ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // CORREÇÃO CRÍTICA: Removi o .orderBy para não bloquear a lista se faltar o índice
                val snapshot = db.collection("bookings")
                    .whereEqualTo("clientEmail", currentUserEmail)
                    .get()
                    .await()

                val allMyBookings = snapshot.toObjects(Booking::class.java)

                // FILTRAGEM DE DATA (Lógica para esconder as antigas)
                // Mantém apenas as que são Hoje ou no Futuro
                val futureBookings = allMyBookings.filter { booking ->
                    isDateTodayOrFuture(booking.date)
                }

                _bookingsList.value = futureBookings

            } catch (e: Exception) {
                e.printStackTrace()
                // Log de erro para ajudar a perceber se falhar
                android.util.Log.e("BookingViewModel", "Erro ao carregar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -----------------------------------------------------------------
    // 2. DISPONIBILIDADE (CALENDÁRIO)
    // -----------------------------------------------------------------
    fun checkAvailabilityForDate(selectedDateMillis: Long) {
        val dateString = convertMillisToDateString(selectedDateMillis)
        viewModelScope.launch {
            try {
                val snapshot = db.collection("bookings")
                    .whereEqualTo("date", dateString)
                    .get().await()

                val bookingsDoDia = snapshot.toObjects(Booking::class.java)

                // Filtra as que não contam como ocupação
                val activeBookings = bookingsDoDia.filter {
                    it.status != "rejected" && it.status != "deleted"
                }

                _occupiedSlots.value = activeBookings.map { it.time }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // -----------------------------------------------------------------
    // 3. ADICIONAR MARCAÇÃO
    // -----------------------------------------------------------------
    fun addBooking(dateMillis: Long?, time: String, name: String, phone: String, service: String) {
        if (dateMillis == null || time.isBlank()) return

        val dateString = convertMillisToDateString(dateMillis)
        val currentUser = auth.currentUser
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if (_occupiedSlots.value.contains(time)) {
                    _bookingResult.value = "Erro: Hora já ocupada!"
                    return@launch
                }

                val newBooking = hashMapOf(
                    "clientEmail" to (currentUser?.email ?: ""),
                    "clientName" to name,
                    "clientPhone" to phone,
                    "date" to dateString,
                    "time" to time,
                    "serviceType" to service,
                    "status" to "pending",
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("bookings").add(newBooking).await()

                _bookingResult.value = "Sucesso! Pedido enviado."

                // ATUALIZA A LISTA IMEDIATAMENTE APÓS MARCAR
                loadUserBookings()

            } catch (e: Exception) {
                _bookingResult.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- FUNÇÕES AUXILIARES DE DATA ---

    private fun convertMillisToDateString(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    // Função que verifica se a string "dd.MM.yyyy" é hoje ou futuro
    private fun isDateTodayOrFuture(dateString: String): Boolean {
        return try {
            val date = dateFormat.parse(dateString) ?: return true

            // Zerar as horas para comparar apenas dia/mês/ano
            val hoje = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dataBooking = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            // Retorna true se for igual ou depois de hoje
            !dataBooking.before(hoje)
        } catch (e: Exception) {
            true // Em caso de erro, mostramos a reserva para não sumir dados
        }
    }

    fun loadBookingsForDate(selectedDateMillis: Long) {
        val dateString = convertMillisToDateString(selectedDateMillis)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Admin quer ver TODAS as reservas desse dia específico (Pendentes e Confirmadas)
                val snapshot = db.collection("bookings")
                    .whereEqualTo("date", dateString)
                    .get()
                    .await()

                _bookingsList.value = snapshot.toObjects(Booking::class.java)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}