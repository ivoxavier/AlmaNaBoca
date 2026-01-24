import SwiftUI
import Combine

// @MainActor garante que todas as atualizações de UI aconteçam na thread principal
@MainActor
class SessionViewModel: ObservableObject {
    
    // Inicializa o repositório (Vê a secção 3 abaixo para o código disto)
    private let usersRepository = UsersRepository()
    
    // Equivalente ao _uiState / uiState
    @Published var uiState: LoginUiState = .idle
    
    // Equivalente ao _currentUser / currentUser
    @Published var currentUser: UserProfile? = nil
    
    // Não precisamos de 'Job' no Swift, usamos Tasks que são canceladas automaticamente
    // se a View morrer, ou guardamos a referência se quisermos cancelar manualmente.
    
    func login(email: String, pass: String) {
        // 1. Atualizar UI para Loading
        self.uiState = .loading
        
        // 2. Iniciar a tarefa assíncrona (substituto do viewModelScope.launch)
        Task {
            do {
                let cleanEmail = email.trimmingCharacters(in: .whitespacesAndNewlines)
                let cleanPassword = pass.trimmingCharacters(in: .whitespacesAndNewlines)
                
                // Chamada ao repositório (com await)
                let firebaseUser = try await usersRepository.signIn(email: cleanEmail, password: cleanPassword)
                
                if let user = firebaseUser {
                    // Mapeamento do utilizador (Adaptei para Swift)
                    self.currentUser = user
                    
                    // Sucesso: Volta ao estado Idle ou Success
                    self.uiState = .idle
                    print("Login com sucesso: \(user.name)")
                } else {
                    self.uiState = .error("Login falhou: Utilizador nulo.")
                }
                
            } catch {
                // Captura de erros
                self.uiState = .error(error.localizedDescription)
            }
        }
    }
    
    func startSession(userId: String) {
        Task {
            // Lógica de sessão futura...
            print("Sessão iniciada para \(userId)")
        }
    }
}
