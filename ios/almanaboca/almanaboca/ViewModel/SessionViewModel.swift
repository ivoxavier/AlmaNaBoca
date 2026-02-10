import SwiftUI
import FirebaseAuth

@MainActor
class SessionViewModel: ObservableObject {
    
    @Published var uiState: LoginUiState = .idle
    @Published var currentUser: UserProfile? = nil
    
    func login(email: String, pass: String) {
        self.uiState = .loading
        
        let cleanEmail = email.trimmingCharacters(in: .whitespacesAndNewlines)
        let cleanPass = pass.trimmingCharacters(in: .whitespacesAndNewlines)
        
        Auth.auth().signIn(withEmail: cleanEmail, password: cleanPass) { [weak self] result, error in
            guard let self = self else { return }
            
            if let error = error as NSError? {
                // TRADUÇÃO DOS ERROS DO FIREBASE
                let errorMessage: String
                if let errorCode = AuthErrorCode(rawValue: error.code) {
                    switch errorCode {
                    case .invalidEmail:
                        errorMessage = "O formato do e-mail é inválido."
                    case .wrongPassword:
                        errorMessage = "A palavra-passe está incorreta."
                    case .userNotFound:
                        errorMessage = "Não existe conta com este e-mail."
                    case .userDisabled:
                        errorMessage = "Este utilizador foi desativado."
                    case .networkError:
                        errorMessage = "Erro de conexão. Verifique a internet."
                    default:
                        errorMessage = "Credenciais inválidas ou erro no sistema."
                    }
                } else {
                    errorMessage = error.localizedDescription
                }
                
                print("Erro Login: \(errorMessage)")
                self.uiState = .error(errorMessage)
                
            } else if let user = result?.user {
                print("Login Sucesso: \(user.uid)")
                
                self.currentUser = UserProfile(
                    id: user.uid,
                    name: user.displayName ?? "Utilizador",
                    email: user.email ?? "",
                    isActive: true
                )
                
                self.uiState = .idle
            }
        }
    }
    
    func logout() {
        try? Auth.auth().signOut()
        self.currentUser = nil
        self.uiState = .idle
    }
}
