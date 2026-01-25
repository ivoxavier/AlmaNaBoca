import Foundation
import SwiftUI



// 3. O ViewModel
@MainActor // Garante que as atualizações de UI ocorrem na thread principal
class HomeViewModel: ObservableObject {
    
    // Equivalente ao MutableStateFlow / asStateFlow
    @Published var uiState: HomeUiState = .loading
    
    init() {
        fetchHomeData()
    }
    
    func fetchHomeData() {
        // Define estado inicial
        self.uiState = .loading
        
        // Equivalente ao viewModelScope.launch
        Task {
            do {
                // SIMULAÇÃO DE REDE: Espera 1 segundo
                // 1.000.000.000 nanosegundos = 1 segundo
                try await Task.sleep(nanoseconds: 1_000_000_000)
                
                // Criação dos dados Mock
                let item1 = HomeItem(
                    id: "1",
                    coachProgram: "Coaching Executivo",
                    whatToExpectProgram: "Focado em liderança e gestão de equipas de alta performance.",
                    coachDateStart: "01/02/2026",
                    coachDateEnd: "01/03/2026",
                    coachDiscount: 20.0,
                    coachVacancies: 8
                   
                )
                
                let item2 = HomeItem(
                    id: "2",
                    coachProgram: "Carreira & Propósito",
                    whatToExpectProgram: "Descobre o teu caminho profissional e alinha os teus objetivos de vida.",
                    coachDateStart: "15/03/2026",
                    coachDateEnd: "15/05/2026",
                    coachDiscount: 0.0, // Sem desconto
                    coachVacancies: 3
                    
                )
                
                let item3 = HomeItem(
                    id: "3",
                    coachProgram: "Inteligência Emocional",
                    whatToExpectProgram: "Aprende a gerir emoções e melhorar relacionamentos no ambiente de trabalho.",
                    coachDateStart: "01/06/2026",
                    coachDateEnd: "01/07/2026",
                    coachDiscount: 15.0,
                    coachVacancies: 12
                )
                
                // Atualiza o estado com a lista
                self.uiState = .success(courses: [item1, item2, item3])
                
            } catch {
                // Tratamento de erros
                self.uiState = .error(error.localizedDescription)
            }
        }
    }
}
