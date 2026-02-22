import SwiftUI
import FirebaseAuth
import FirebaseFirestore
import FirebaseMessaging // <--- 1. NOVO IMPORT PARA AS NOTIFICAÇÕES

@MainActor
class SessionViewModel: ObservableObject {
    
    private let db = Firestore.firestore()
    
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
                
                // <--- 2. LOGO APÓS O LOGIN, CONFIGURAMOS AS NOTIFICAÇÕES --->
                self.updateFcmToken(email: user.email ?? "")
                self.checkChatAccessForNotifications()
            }
        }
    }
    
    func logout() {
        try? Auth.auth().signOut()
        self.currentUser = nil
        self.uiState = .idle
        
        // Opcional: Remover subscrição do chat ao fazer logout para não receber mensagens
        Messaging.messaging().unsubscribe(fromTopic: "community_chat")
    }
    
    // Usada pelo botão da Home para navegar
    func verifyAccessNow(onSuccess: @escaping () -> Void, onFailure: @escaping () -> Void) {
        guard let uid = Auth.auth().currentUser?.uid else {
            DispatchQueue.main.async { onFailure() }
            return
        }
        
        db.collection("users").document(uid).getDocument { document, error in
            DispatchQueue.main.async {
                if let error = error {
                    print("Erro do Firebase ao ler acesso: \(error.localizedDescription)")
                    onFailure()
                    return
                }
                
                if let document = document, document.exists {
                    let hasAccess = document.data()?["hasChat"] as? Bool ?? false
                    
                    if hasAccess {
                        onSuccess()
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }
            }
        }
    }
    
    // MARK: - LÓGICA DE NOTIFICAÇÕES (FCM)
    
    // Guarda o Token do dispositivo no Firebase e subscreve Admins
    private func updateFcmToken(email: String) {
        Messaging.messaging().token { [weak self] token, error in
            guard let self = self else { return }
            guard let token = token, let uid = Auth.auth().currentUser?.uid else { return }
            
            // Guarda o Token na DB para podermos enviar mensagens diretas, se necessário
            let data: [String: Any] = ["fcmToken": token, "email": email]
            self.db.collection("users").document(uid).setData(data, merge: true)
            
            // Verifica se é Admin e subscreve o tópico de gestão
            let admins = ["martamartins340@gmail.com", "ivofernandes12@gmail.com"]
            if admins.contains(email) {
                Messaging.messaging().subscribe(toTopic: "admin_notifications")
                print("📣 Admin subscrito para notificações")
            } else {
                Messaging.messaging().unsubscribe(fromTopic: "admin_notifications")
            }
        }
    }
    
    // Subscreve as notificações do chat da Comunidade se tiver permissão
    private func checkChatAccessForNotifications() {
        guard let uid = Auth.auth().currentUser?.uid else { return }
        
        db.collection("users").document(uid).getDocument { doc, error in
            let hasAccess = doc?.data()?["hasChat"] as? Bool ?? false
            
            if hasAccess {
                Messaging.messaging().subscribe(toTopic: "community_chat") { error in
                    if error == nil { print("✅ Subscrito no community_chat para notificações") }
                }
            } else {
                Messaging.messaging().unsubscribe(fromTopic: "community_chat")
            }
        }
    }
    
    func deleteAccount(onSuccess: @escaping () -> Void, onFailure: @escaping (String) -> Void) {
            guard let user = Auth.auth().currentUser else { return }
            let uid = user.uid
            
            user.delete { [weak self] error in
                if let error = error {
                    DispatchQueue.main.async { onFailure(error.localizedDescription) }
                } else {
                    // Remove os dados da base de dados Firestore
                    self?.db.collection("users").document(uid).delete()
                    
                    DispatchQueue.main.async {
                        self?.currentUser = nil
                        self?.uiState = .idle
                        onSuccess()
                    }
                }
            }
        }
}
