//
//  AlmanaBocaNavigation.swift
//  almanaboca
//

import SwiftUI

// 1. Definimos as rotas (igual à tua sealed class Screen)
enum Screen: Hashable {
    case login
    case menu
}

struct AlmanaBocaNavigation: View {
    // Equivalente ao viewModel() do Android.
    // O @StateObject garante que o ViewModel não morre quando a View recarrega.
    @StateObject private var sessionViewModel = SessionViewModel()
    
    // Equivalente ao rememberNavController()
    @State private var navigationPath = NavigationPath()

    var body: some View {
        NavigationStack(path: $navigationPath) {
            // Destino Inicial: Login
            LoginScreen(
                // No iOS passamos o objeto ViewModel ou as propriedades dele
                // Aqui estou a passar o método de login via closure para manter desacoplado
                uiState: sessionViewModel.uiState,
                onLoginClick: { email, password in
                    sessionViewModel.login(email: email, pass: password)
                    
                    // Navegar para o Menu
                    navigationPath.append(Screen.menu)
                }
            )
            // Definição dos destinos (o "grafo" de navegação)
            .navigationDestination(for: Screen.self) { screen in
                switch screen {
                case .login:
                    // Caso voltes para o login
                    LoginScreen(
                        uiState: sessionViewModel.uiState,
                        onLoginClick: { _,_ in }
                    )
                    
                case .menu:
                    MenuScreen()
                        // Esconde o botão "Back" nativo para simular o popUpTo inclusive
                        .navigationBarBackButtonHidden(true)
                }
            }
        }
    }
}
