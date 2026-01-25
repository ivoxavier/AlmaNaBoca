import Foundation
import SwiftUI

// --- Modelos ---
enum BookingStatus {
    case confirmed
    case pending
}

struct BookingItem: Identifiable, Hashable {
    let id: String
    let serviceName: String
    let date: String
    let time: String
    let status: BookingStatus
}

// Estado específico para esta tela
enum BookingUiState: Equatable {
    case loading
    case success
    case error(String)
}

@MainActor
class BookingViewModel: ObservableObject {
    
    @Published var uiState: BookingUiState = .loading
    
    // Lista de marcações (Mutável para podermos apagar items)
    @Published var bookings: [BookingItem] = []
    
    init() {
        fetchBookings()
    }
    
    func fetchBookings() {
        self.uiState = .loading
        
        Task {
            do {
                // Simula delay de rede
                try await Task.sleep(nanoseconds: 1_000_000_000)
                
                // Dados Fictícios
                self.bookings = [
                    BookingItem(id: "1", serviceName: "Sessão de Coaching Executivo", date: "12 Fev 2026", time: "14:30", status: .confirmed),
                    BookingItem(id: "2", serviceName: "Mentoria de Carreira", date: "28 Fev 2026", time: "10:00", status: .pending),
                    BookingItem(id: "3", serviceName: "Círculo de Meditação", date: "05 Mar 2026", time: "18:00", status: .confirmed)
                ]
                
                self.uiState = .success
            } catch {
                self.uiState = .error("Erro ao carregar marcações")
            }
        }
    }
    
    func cancelBooking(_ item: BookingItem) {
        // Remove da lista localmente
        if let index = bookings.firstIndex(where: { $0.id == item.id }) {
            bookings.remove(at: index)
        }
    }
    
    func addNewBooking(date: Date) {
        // Lógica fictícia para adicionar nova marcação após selecionar data
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM yyyy"
        let dateString = formatter.string(from: date)
        
        let newItem = BookingItem(
            id: UUID().uuidString,
            serviceName: "Nova Consulta (Demo)",
            date: dateString,
            time: "09:00",
            status: .pending
        )
        bookings.append(newItem)
    }
}
