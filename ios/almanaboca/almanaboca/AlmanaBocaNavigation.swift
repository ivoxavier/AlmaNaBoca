import SwiftUI

enum Screen: Hashable {
    case login
    case menu
}

struct AlmanaBocaNavigation: View {
    // Aqui criamos o ViewModel principal
    @StateObject private var sessionViewModel = SessionViewModel()
    @State private var navigationPath = NavigationPath()

    var body: some View {
        NavigationStack(path: $navigationPath) {
            LoginScreen(
                uiState: sessionViewModel.uiState,
                onLoginClick: { email, password in
                    sessionViewModel.login(email: email, pass: password)
                }
            )
            .navigationDestination(for: Screen.self) { screen in
                switch screen {
                case .login:
                    LoginScreen(
                        uiState: sessionViewModel.uiState,
                        onLoginClick: { _,_ in }
                    )
                    
                case .menu:
                    // Se estiveres a usar o MenuScreen que contém o HomeScreen:
                    MenuScreen()
                        .navigationBarBackButtonHidden(true)
                    // Se estiveres a testar o HomeScreen diretamente, usa: HomeScreen()
                }
            }
            .onChange(of: sessionViewModel.currentUser) { newUser in
                if newUser != nil {
                    navigationPath.append(Screen.menu)
                }
            }
        }
        // ⚠️ CORREÇÃO CRÍTICA: Injetar o ViewModel no ambiente
        .environmentObject(sessionViewModel)
    }
}
