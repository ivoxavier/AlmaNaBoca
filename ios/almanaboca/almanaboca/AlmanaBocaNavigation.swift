import SwiftUI

enum Screen: Hashable {
    case login
    case menu
    case chat
    case userProfile // <--- NOVO DESTINO
}

struct AlmanaBocaNavigation: View {
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
                    LoginScreen(uiState: sessionViewModel.uiState, onLoginClick: { _,_ in })
                    
                case .menu:
                    MenuScreen(
                        onNavigateToChat: { navigationPath.append(Screen.chat) },
                        onNavigateToProfile: { navigationPath.append(Screen.userProfile) } // <--- NAVEGAÇÃO PERFIL
                    )
                    .navigationBarBackButtonHidden(true)
                    
                case .chat:
                    CommunityChatScreen()
                    
                case .userProfile:
                    UserScreen() // <--- O NOVO ECRÃ
                }
            }
            // Navega para o Menu quando o login é feito com sucesso
            .onChange(of: sessionViewModel.currentUser) { newUser in
                if newUser != nil {
                    navigationPath.append(Screen.menu)
                } else {
                    // SE FIZER LOGOUT OU APAGAR CONTA: Limpa tudo e volta ao Login
                    navigationPath.removeLast(navigationPath.count)
                }
            }
        }
        .environmentObject(sessionViewModel)
    }
}
