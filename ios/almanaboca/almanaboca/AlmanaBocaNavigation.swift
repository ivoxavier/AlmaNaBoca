//
//  AlmanaBocaNavigation.swift
//  almanaboca
//

import SwiftUI

// 1. Definimos as rotas
enum Screen: Hashable {
    case login
    case menu
}

struct AlmanaBocaNavigation: View {
    @StateObject private var sessionViewModel = SessionViewModel()
    @State private var navigationPath = NavigationPath()

    var body: some View {
        NavigationStack(path: $navigationPath) {
            // Destino Inicial: Login
            LoginScreen(
                uiState: sessionViewModel.uiState,
                onLoginClick: { email, password in
                    // 1. Apenas iniciamos o pedido. NÃO navegamos aqui.
                    sessionViewModel.login(email: email, pass: password)
                }
            )
            // Definição dos destinos
            .navigationDestination(for: Screen.self) { screen in
                switch screen {
                case .login:
                    LoginScreen(
                        uiState: sessionViewModel.uiState,
                        onLoginClick: { _,_ in }
                    )
                    
                case .menu:
                    MenuScreen()
                        .navigationBarBackButtonHidden(true)
                }
            }
            // 2. AQUI ESTÁ A CORREÇÃO: "Ouvimos" se o utilizador mudou
            .onChange(of: sessionViewModel.currentUser) { newUser in
                if newUser != nil {
                    // Se o Firebase devolveu um user com sucesso, avançamos!
                    navigationPath.append(Screen.menu)
                }
            }
        }
    }
}
