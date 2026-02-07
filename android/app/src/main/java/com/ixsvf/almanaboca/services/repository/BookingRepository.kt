package com.ixsvf.almanaboca.services.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ixsvf.almanaboca.services.model.Booking
import kotlinx.coroutines.tasks.await

class BookingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val bookingsCollection = db.collection("bookings")

    // 1. Ver horários ocupados
    suspend fun getBookingsForDate(date: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("date", date)
                .get()
                .await()
            snapshot.toObjects(Booking::class.java)
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao buscar horários: ${e.message}")
            emptyList()
        }
    }

    // 2. Cliente: Ver as suas reservas
    suspend fun getUserBookings(userEmail: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("clientEmail", userEmail)
                .get()
                .await()

            snapshot.toObjects(Booking::class.java).sortedBy { it.date }
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao buscar reservas do user: ${e.message}")
            emptyList()
        }
    }

    // 3. Admin: Ver TUDO
    suspend fun getAllBookings(): List<Booking> {
        return try {
            // Nota: Se der erro aqui, pode ser falta de Índice no Firebase
            // Tente remover o .orderBy("date") temporariamente para testar
            val snapshot = bookingsCollection
                .orderBy("date")
                .get()
                .await()

            val list = snapshot.toObjects(Booking::class.java)
            Log.d("BOOKING_DEBUG", "Encontrei ${list.size} reservas")
            list
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao carregar tudo (Admin): ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // 4. Criar reserva
    suspend fun createBooking(booking: Booking): Boolean {
        return try {
            // Verifica duplicados
            val existing = bookingsCollection
                .whereEqualTo("date", booking.date)
                .whereEqualTo("time", booking.time)
                .get()
                .await()

            if (!existing.isEmpty) return false

            // IMPORTANTE: Ao usar @DocumentId, não precisamos de definir o ID manualmente
            // O .add() gera o ID e o Firestore gere o resto.
            bookingsCollection.add(booking).await()
            true
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao criar: ${e.message}")
            false
        }
    }

    // 5. Ações de Admin
    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Boolean {
        return try {
            if (bookingId.isEmpty()) {
                Log.e("BOOKING_ERROR", "Erro: ID da reserva está vazio!")
                return false
            }
            bookingsCollection.document(bookingId).update("status", newStatus).await()
            true
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao atualizar: ${e.message}")
            false
        }
    }

    suspend fun deleteBooking(bookingId: String): Boolean {
        return try {
            if (bookingId.isEmpty()) {
                Log.e("BOOKING_ERROR", "Erro: ID da reserva está vazio!")
                return false
            }
            bookingsCollection.document(bookingId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Erro ao apagar: ${e.message}")
            false
        }
    }

    suspend fun deletePastBookings() {
        try {
            // Pega a data de hoje formato "yyyy-MM-dd"
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            // Procura tudo o que tem data MENOR que hoje
            val snapshot = bookingsCollection
                .whereLessThan("date", today)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                // Prepara um lote para apagar tudo de uma vez
                val batch = db.batch()
                for (document in snapshot.documents) {
                    batch.delete(document.reference)
                }
                // Executa a eliminação
                batch.commit().await()
                android.util.Log.d("BOOKING_CLEANUP", "Limpeza concluída: ${snapshot.size()} reservas antigas apagadas.")
            }
        } catch (e: Exception) {
            android.util.Log.e("BOOKING_ERROR", "Erro na limpeza automática: ${e.message}")
        }
    }

}