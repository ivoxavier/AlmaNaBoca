import SwiftUI
import FirebaseAuth
import FirebaseFirestore

@MainActor
class BookingViewModel: ObservableObject {
    
    private let db = Firestore.firestore()
    
    @Published var bookings: [Booking] = []
    @Published var occupiedSlots: [String] = []
    @Published var isLoading = false
    @Published var alertMessage: String = ""
    @Published var showAlert: Bool = false
    
    var isAdmin: Bool {
        let email = Auth.auth().currentUser?.email ?? ""
        return AlmanabocaConstants.adminEmails.contains(email)
    }
    
    func loadData() {
        self.isLoading = true
        
        Task {
            // Garante que o loading pára sempre
            defer { self.isLoading = false }
            
            do {
                if isAdmin {
                    let snapshot = try await db.collection("bookings")
                        .whereField("status", isEqualTo: "pending")
                        .getDocuments()
                    self.bookings = snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
                } else {
                    guard let email = Auth.auth().currentUser?.email else { return }
                    
                    let snapshot = try await db.collection("bookings")
                        .whereField("clientEmail", isEqualTo: email)
                        .getDocuments()
                    
                    let all = snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
                    
                    // Ordena por data (recente primeiro) e filtra as antigas
                    self.bookings = all
                        .filter { self.isDateFutureOrToday($0.date) }
                        .sorted { $0.date > $1.date }
                }
            } catch {
                print("Erro: \(error)")
            }
        }
    }
    
    // --- LÓGICA DE DISPONIBILIDADE ---
    func checkAvailability(for date: Date) {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        let dateString = formatter.string(from: date)
        
        Task {
            do {
                let snapshot = try await db.collection("bookings")
                    .whereField("date", isEqualTo: dateString)
                    .getDocuments()
                
                let items = snapshot.documents.compactMap { try? $0.data(as: Booking.self) }
                // Só bloqueia se não estiver rejeitado
                self.occupiedSlots = items
                    .filter { $0.status != "rejected" }
                    .map { $0.time }
            } catch { print(error) }
        }
    }
    
    // --- CRIAR ---
    func createBooking(date: Date, time: String, name: String, phone: String, service: String) {
        guard let userEmail = Auth.auth().currentUser?.email else { return }
        self.isLoading = true
        
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        let dateString = formatter.string(from: date)
        
        let newBooking = Booking(
            date: dateString,
            time: time,
            serviceType: service,
            clientName: name,
            clientEmail: userEmail,
            clientPhone: phone,
            status: "pending",
            timestamp: Int64(Date().timeIntervalSince1970 * 1000) // Timestamp adicionado
        )
        
        Task {
            do {
                try db.collection("bookings").addDocument(from: newBooking)
                self.alertMessage = "Sucesso! Aguarda confirmação."
                self.showAlert = true
                self.loadData()
            } catch {
                self.alertMessage = "Erro: \(error.localizedDescription)"
                self.showAlert = true
            }
            self.isLoading = false
        }
    }
    
    func cancelBooking(_ booking: Booking) {
        guard let id = booking.id else { return }
        Task {
            do {
                try await db.collection("bookings").document(id).delete()
                self.loadData()
            } catch {
                print("Erro ao cancelar: \(error)")
            }
        }
    }
    
    // CORREÇÃO DO ERRO DO SWIFT 6 AQUI:
    // Retiramos a inicialização direta do dicionário dentro do updateData para evitar o aviso "non-sendable type"
    func updateBookingStatus(_ booking: Booking, newStatus: String) {
        guard let id = booking.id else { return }
        
        // Criamos as variáveis fora do Task para serem seguras para concorrência
        let docRef = db.collection("bookings").document(id)
        let updateDict: [String: Any] = ["status": newStatus]
        
        Task {
            do {
                try await docRef.updateData(updateDict)
                self.loadData()
            } catch {
                print("Erro ao atualizar: \(error)")
            }
        }
    }
    
    // --- FUNÇÃO AUXILIAR DE DATA ---
    private func isDateFutureOrToday(_ dateString: String) -> Bool {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        
        guard let date = formatter.date(from: dateString) else { return true }
        
        let result = Calendar.current.compare(date, to: Date(), toGranularity: .day)
        return result == .orderedSame || result == .orderedDescending
    }
}
