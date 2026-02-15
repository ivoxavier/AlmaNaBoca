import Foundation
import FirebaseFirestore

class BookingRepository {
    
    private let db = Firestore.firestore()
    private let collection = "bookings"
    
    // 1. Cliente: Ver as suas reservas
    func getUserBookings(email: String) async -> [Booking] {
        do {
            let snapshot = try await db.collection(collection)
                .whereField("clientEmail", isEqualTo: email)
                .getDocuments()
            
            let bookings = snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
            
            // Ordenar por timestamp (mais recente primeiro) ou data
            return bookings.sorted { ($0.timestamp ?? 0) > ($1.timestamp ?? 0) }
        } catch {
            print("Erro ao buscar reservas: \(error)")
            return []
        }
    }
    
    // 2. Admin: Ver Pendentes (ou todas)
    func getAllPendingBookings() async -> [Booking] {
        do {
            let snapshot = try await db.collection(collection)
                .whereField("status", isEqualTo: "pending")
                .getDocuments()
            
            return snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
        } catch {
            print("Erro admin: \(error)")
            return []
        }
    }
    
    // 3. Verificação de Disponibilidade (Slots Ocupados)
    func getOccupiedSlots(date: String) async -> [String] {
        do {
            let snapshot = try await db.collection(collection)
                .whereField("date", isEqualTo: date)
                .getDocuments()
            
            let bookings = snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
            
            // Filtra apenas as que não foram rejeitadas/apagadas
            let activeBookings = bookings.filter { $0.status != "rejected" && $0.status != "deleted" }
            
            return activeBookings.map { $0.time }
        } catch {
            return []
        }
    }
    
    // 4. Criar Reserva
    func createBooking(_ booking: Booking) async -> Bool {
        do {
            // Verificar duplicados (Segurança extra)
            let existing = try await db.collection(collection)
                .whereField("date", isEqualTo: booking.date)
                .whereField("time", isEqualTo: booking.time)
                .getDocuments()
            
            if !existing.isEmpty { return false }
            
            try db.collection(collection).addDocument(from: booking)
            return true
        } catch {
            print("Erro ao criar: \(error)")
            return false
        }
    }
    
    // 5. Admin: Atualizar Status (Aprovar/Rejeitar)
    func updateStatus(bookingId: String, newStatus: String) async {
        do {
            try await db.collection(collection).document(bookingId).updateData([
                "status": newStatus
            ])
        } catch {
            print("Erro ao atualizar status: \(error)")
        }
    }
    
    // 6. Apagar (Cancelar)
    func deleteBooking(bookingId: String) async {
        do {
            try await db.collection(collection).document(bookingId).delete()
        } catch {
            print("Erro ao apagar: \(error)")
        }
    }
}
