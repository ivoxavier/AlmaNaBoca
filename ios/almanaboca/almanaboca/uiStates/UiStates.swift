import Foundation

// MARK: - Models
// Criei este Stub para o código compilar, pois o HomeUiState depende dele.
// Substitui pelos campos reais do teu 'HomeItem.kt' quando o tiveres.
struct HomeItem: Identifiable, Hashable, Codable {
    var id: String
    var title: String
    // Adiciona outros campos aqui...
}

// MARK: - UI States

// Equivalente ao sealed class LoginUiState
enum LoginUiState: Equatable {
    case idle
    case loading
    case error(String)
}

// Equivalente ao sealed interface HomeUiState
enum HomeUiState: Equatable {
    case loading
    // Em Swift, List<HomeItem> traduz-se para [HomeItem]
    case success(courses: [HomeItem])
    case error(String)
}
