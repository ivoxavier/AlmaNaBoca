//
//  MenuScreen.swift
//  almanaboca
//

import SwiftUI

// 1. Enum para definir as Tabs (Substitui a sealed class BottomBarScreen)
enum TabItem: String, CaseIterable {
    case home
    case meditations
    case booking
    
    // Títulos
    var title: String {
        switch self {
        case .home: return "Início" // R.string.lbl_btbar_home
        case .meditations: return "Meditação" // R.string.lbl_btbar_meditation
        case .booking: return "Agendar" // R.string.lbl_btbar_booking
        }
    }
    
    // Ícones (Estou a usar SF Symbols nativos da Apple como placeholder)
    // Quando tiveres os teus ícones no Assets.xcassets, podes mudar para Image("nome_do_asset")
    var iconName: String {
        switch self {
        case .home: return "house.fill" // R.drawable.ic_home
        case .meditations: return "leaf.fill" // R.drawable.ic_meditation
        case .booking: return "calendar" // R.drawable.ic_consultation
        }
    }
}

struct MenuScreen: View {
    // Controla qual tab está ativa
    @State private var selectedTab: TabItem = .home
    
    // Se precisares de acesso ao ViewModel aqui, podes injetar:
    // @ObservedObject var sessionViewModel: SessionViewModel

    var body: some View {
        // 2. TabView substitui o Scaffold + BottomNavigationBar + NavHost aninhado
        TabView(selection: $selectedTab) {
            
            // Rota: Home
            HomeScreen()
                .tabItem {
                    Label(TabItem.home.title, systemImage: TabItem.home.iconName)
                }
                .tag(TabItem.home) // Importante para o binding selection funcionar
            
            // Rota: Meditações
            MeditationsScreen()
                .tabItem {
                    Label(TabItem.meditations.title, systemImage: TabItem.meditations.iconName)
                }
                .tag(TabItem.meditations)
            
            // Rota: Booking
            BookingScreen()
                .tabItem {
                    Label(TabItem.booking.title, systemImage: TabItem.booking.iconName)
                }
                .tag(TabItem.booking)
        }
        // Personalização da cor da TabBar (Opcional)
        .tint(Color.brandRed) // Usa a cor vermelha que definimos antes quando selecionado
        .onAppear {
            // Configurações extra de aparência da TabBar se necessário
            let appearance = UITabBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor.systemBackground
            UITabBar.appearance().scrollEdgeAppearance = appearance
        }
    }
}

// MARK: - Sub-telas (Stubs para o código compilar)

// Podes mover estas structs para ficheiros separados:
// HomeScreen.swift, MeditationsScreen.swift, BookingScreen.swift




// Pequeno helper para simular o Modifier do Compose, caso queiras manter a estrutura
struct Modifier {
    func fillMaxSize() -> Modifier { return self }
}

struct MenuScreen_Previews: PreviewProvider {
    static var previews: some View {
        MenuScreen()
    }
}
