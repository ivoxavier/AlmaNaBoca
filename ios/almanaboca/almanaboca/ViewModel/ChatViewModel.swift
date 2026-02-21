import SwiftUI
import FirebaseFirestore

class ChatViewModel: ObservableObject {
    private let db = Firestore.firestore()
    
    @Published var messages: [ChatMessage] = []
    
    init() {
        fetchMessages()
    }
    
    func fetchMessages() {
        db.collection("community_chat")
            .order(by: "timestamp", descending: false) // Ordem cronológica
            .addSnapshotListener { snapshot, error in
                guard let documents = snapshot?.documents else {
                    print("Erro a carregar chat: \(String(describing: error))")
                    return
                }
                
                self.messages = documents.compactMap { doc -> ChatMessage? in
                    try? doc.data(as: ChatMessage.self)
                }
            }
    }
    
    func sendMessage(text: String, userId: String, userName: String) {
        if text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty { return }
        
        let newMessage = ChatMessage(
            senderId: userId,
            senderName: userName,
            text: text,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000)
        )
        
        do {
            try db.collection("community_chat").addDocument(from: newMessage)
        } catch {
            print("Erro ao enviar mensagem: \(error)")
        }
    }
}
