import Foundation
import FirebaseFirestore

struct ChatMessage: Identifiable, Codable, Hashable {
    @DocumentID var id: String?
    
    let senderId: String
    let senderName: String
    let text: String
    let timestamp: Int64
    
    // Helper para converter timestamp em Date
    var date: Date {
        Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000)
    }
}
