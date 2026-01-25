import Foundation
import SwiftUI

// --- Modelo de Dados (MeditationUiModel) ---
struct MeditationUiModel: Identifiable, Hashable {
    let id: Int
    let dayTitle: String // ex: "Dia 1"
    let title: String    // ex: "A Respiração Consciente"
    let duration: String // ex: "10 min"
    let isCompleted: Bool
    let isLocked: Bool
}

@MainActor
class MeditationsViewModel: ObservableObject {
    
    // Lista de Meditações
    @Published var allMeditations: [MeditationUiModel] = []
    
    // Meditação Selecionada (Hero Card)
    @Published var selectedMeditation: MeditationUiModel?
    
    init() {
        loadData()
    }
    
    private func loadData() {
        // Dados Fictícios (Réplica do teu Kotlin)
        let data = [
            MeditationUiModel(id: 1, dayTitle: "Dia 1", title: "Chegar ao Momento", duration: "08:00", isCompleted: true, isLocked: false),
            MeditationUiModel(id: 2, dayTitle: "Dia 2", title: "Escutar o Corpo", duration: "10:30", isCompleted: false, isLocked: false),
            MeditationUiModel(id: 3, dayTitle: "Dia 3", title: "Libertar a Ansiedade", duration: "12:00", isCompleted: false, isLocked: false),
            MeditationUiModel(id: 4, dayTitle: "Dia 4", title: "Cultivar a Gratidão", duration: "09:15", isCompleted: false, isLocked: true),
            MeditationUiModel(id: 5, dayTitle: "Dia 5", title: "O Poder do Silêncio", duration: "15:00", isCompleted: false, isLocked: true),
            MeditationUiModel(id: 6, dayTitle: "Dia 6", title: "Conexão Profunda", duration: "11:45", isCompleted: false, isLocked: true),
            MeditationUiModel(id: 7, dayTitle: "Dia 7", title: "Integração Total", duration: "14:20", isCompleted: false, isLocked: true)
        ]
        
        self.allMeditations = data
        // Seleciona o Dia 2 por defeito (como no teu exemplo)
        self.selectedMeditation = data.first { $0.id == 2 } ?? data.first
    }
    
    func selectMeditation(_ meditation: MeditationUiModel) {
        if !meditation.isLocked {
            self.selectedMeditation = meditation
        }
    }
}
