import SwiftUI

enum Screen: Hashable {
    case login
    case menu
    case chat // Rota do chat garantida aqui
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
                    // ⚠️ CORREÇÃO 1: Enviar o navigationPath para o MenuScreen
                    MenuScreen(navigationPath: $navigationPath)
                        .navigationBarBackButtonHidden(true)
                    
                case .chat:
                    // Destino do Chat
                    CommunityChatScreen()
                }
            }
            .onChange(of: sessionViewModel.currentUser) { newUser in
                if newUser != nil {
                    navigationPath.append(Screen.menu)
                }
            }
        }
        // ⚠️ CORREÇÃO 2: Injetar o ViewModel no ambiente
        .environmentObject(sessionViewModel)
    }
    // As chavetas agora estão todas nos sítios certos!
}
