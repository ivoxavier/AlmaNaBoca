package com.ixsvf.almanaboca.services.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.almanaboca.services.model.Booking
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class BookingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val bookingsCollection = db.collection("bookings")

    // 1. Obter reservas de um dia específico para bloquear no calendário
    suspend fun getBookingsForDate(date: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("date", date)
                .whereEqualTo("status", "confirmed") // Só queremos as confirmadas
                .get()
                .await()

            snapshot.toObjects(Booking::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 2. Criar uma nova reserva
    suspend fun createBooking(booking: Booking): Boolean {
        return try {
            // Verifica se já existe reserva para aquela hora (Segurança extra)
            val existing = bookingsCollection
                .whereEqualTo("date", booking.date)
                .whereEqualTo("time", booking.time)
                .get()
                .await()

            if (!existing.isEmpty) {
                return false // Já está ocupado!
            }

            // Grava no Firebase (deixa ele gerar o ID)
            bookingsCollection.add(booking).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}