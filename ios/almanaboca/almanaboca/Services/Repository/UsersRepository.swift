import Foundation

// Isto simula o teu UsersRepository e o Firebase
class UsersRepository {
    
    // Simula a função signInWithEmailAndPassword
    func signIn(email: String, password: String) async throws -> UserProfile? {
        
        // Simula delay de rede (1.5 segundos) para veres o LoadingSpinner a rodar
        try await Task.sleep(nanoseconds: 1_500_000_000)
        
        // Simulação simples de validação
        if password.count < 6 {
            throw NSError(domain: "Auth", code: 400, userInfo: [NSLocalizedDescriptionKey: "A palavra-passe deve ter 6 caracteres."])
        }
        
        // Simula sucesso
        // Num cenário real, isto retornaria o objeto AuthDataResult do Firebase
        return UserProfile(
            id: UUID().uuidString,
            name: "Utilizador iOS",
            email: email,
            isActive: true
        )
    }
}
