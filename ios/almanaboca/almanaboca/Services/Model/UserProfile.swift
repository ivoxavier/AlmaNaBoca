import Foundation

// Equivalente ao teu UserProfile data class
struct UserProfile: Identifiable, Codable, Equatable {
    let id: String
    let name: String
    let email: String
    let isActive: Bool
}
