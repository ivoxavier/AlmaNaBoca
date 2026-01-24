import SwiftUI

// --- Cores ---
extension Color {
    static let cardWhite = Color.white
    static let textGray = Color(red: 107/255, green: 114/255, blue: 128/255) // #6B7280
    static let promoYellowBg = Color(red: 255/255, green: 249/255, blue: 196/255) // #FFF9C4
    static let promoYellowText = Color(red: 183/255, green: 121/255, blue: 31/255) // #B7791F
    static let spotifyGreen = Color(red: 29/255, green: 185/255, blue: 84/255) // #1DB954
    // Fundo cinza claro para destacar os cards brancos
    static let backgroundGray = Color(UIColor.systemGray6)
}

// Stub do ViewModel (Se ainda não tiveres o HomeViewModel real)
class HomeViewModel: ObservableObject {
    @Published var uiState: HomeUiState = .loading
    
    init() {
        // Simular carregamento
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.uiState = .success(courses: [
                HomeItem(id: "1", title: "Programa Detox", coachProgram: "Programa Detox 21 Dias", whatToExpectProgram: "Limpeza profunda e reeducação alimentar para transformar a tua vida.", coachDateStart: "01/02", coachDateEnd: "21/02", coachVacancies: 5, coachDiscount: 15.0),
                HomeItem(id: "2", title: "Yoga Matinal", coachProgram: "Yoga & Mindfulness", whatToExpectProgram: "Começa o dia com energia e foco total.", coachDateStart: "10/02", coachDateEnd: "10/03", coachVacancies: 2, coachDiscount: 0.0)
            ])
        }
    }
}

// Extensão ao HomeItem para garantir que temos os campos necessários para o UI
extension HomeItem {
    // Adiciona estas propriedades se não existirem no teu struct original
    var coachProgram: String { return title } // Exemplo de mapeamento
    var whatToExpectProgram: String { return "Descrição do programa..." }
    var coachDateStart: String { return "12/01" }
    var coachDateEnd: String { return "30/01" }
    var coachVacancies: Int { return 5 }
    var coachDiscount: Double { return 10.0 }
}
