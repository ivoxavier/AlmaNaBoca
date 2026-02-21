//
//  MenuScreen.swift
//  almanaboca
//

import SwiftUI

// 1. Enum para definir as Tabs
enum TabItem: String, CaseIterable {
    case home
    case meditations
    case booking
    
    // Títulos
    var title: String {
        switch self {
        case .home: return "Início"
        case .meditations: return "Meditação"
        case .booking: return "Agendar"
        }
    }
    
    // Ícones (SF Symbols)
    var iconName: String {
        switch self {
        case .home: return "house.fill"
        case .meditations: return "leaf.fill"
        case .booking: return "calendar"
        }
    }
}

struct MenuScreen: View {
    // A App vai iniciar com o .home selecionado (que agora estará no meio)
    @State private var selectedTab: TabItem = .home
    
    // 1. Recebe o path do AlmanaBocaNavigation
        @Binding var navigationPath: NavigationPath
    
    var body: some View {
        TabView(selection: $selectedTab) {
            
            // --- POSIÇÃO 1 (ESQUERDA): Meditações ---
            MeditationsScreen()
                .tabItem {
                    Label(TabItem.meditations.title, systemImage: TabItem.meditations.iconName)
                }
                .tag(TabItem.meditations)
            
            // --- POSIÇÃO 2 (MEIO): Home ---
            HomeScreen(
                
            )
                .tabItem {
                    Label(TabItem.home.title, systemImage: TabItem.home.iconName)
                }
                .tag(TabItem.home) // O Binding selection fará com que esta seja a ativa ao iniciar
            
            // --- POSIÇÃO 3 (DIREITA): Booking ---
            BookingScreen()
                .tabItem {
                    Label(TabItem.booking.title, systemImage: TabItem.booking.iconName)
                }
                .tag(TabItem.booking)
        }
        // Personalização da cor (Vermelho da marca)
        .tint(Color.brandRed)
        .onAppear {
            let appearance = UITabBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor.systemBackground
            
            // Aplica a aparência tanto ao scroll como ao estado normal
            UITabBar.appearance().standardAppearance = appearance
            UITabBar.appearance().scrollEdgeAppearance = appearance
        }
    }
}

// MARK: - Previews
struct MenuScreen_Previews: PreviewProvider {
    static var previews: some View {
        MenuScreen(navigationPath: .constant(NavigationPath()))
            .environmentObject(SessionViewModel()) // Exemplo de injeção para preview
    }
}
