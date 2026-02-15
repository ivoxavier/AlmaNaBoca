import SwiftUI

@MainActor
class HomeViewModel: ObservableObject {
    
    // CORREÇÃO: 'let' em vez de 'val'
    private let repository = HomeRepository()
    
    @Published var uiState: HomeUiState = .loading
    
    init() {
        fetchHomeData()
    }
    
    func fetchHomeData() {
        self.uiState = .loading
        
        Task {
            let items = await repository.getHomeItems()
            
            if items.isEmpty {
                // No iOS usamos .success com array vazio
                self.uiState = .success(courses: [])
            } else {
                self.uiState = .success(courses: items)
            }
        }
    }
}
