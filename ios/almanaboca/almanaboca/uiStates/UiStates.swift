import Foundation



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
