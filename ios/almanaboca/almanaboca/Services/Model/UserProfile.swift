import Foundation

// Equivalente ao teu UserProfile data class
struct UserProfile: Identifiable, Codable {
    let id: String
    let name: String
    let email: String
    let isActive: Bool
}
