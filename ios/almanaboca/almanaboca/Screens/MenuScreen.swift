import SwiftUI

enum TabItem: String, CaseIterable {
    case home
    case meditations
    case booking
    
    var title: String {
        switch self {
        case .home: return "Início"
        case .meditations: return "Meditação"
        case .booking: return "Agendar"
        }
    }
    
    var iconName: String {
        switch self {
        case .home: return "house.fill"
        case .meditations: return "leaf.fill"
        case .booking: return "calendar"
        }
    }
}

struct MenuScreen: View {
    @State private var selectedTab: TabItem = .home
    
    // 2. Recebe a ação do pai
    var onNavigateToChat: () -> Void
    
    var onNavigateToProfile: () -> Void
    
    var body: some View {
        TabView(selection: $selectedTab) {
            
            MeditationsScreen()
                .tabItem { Label(TabItem.meditations.title, systemImage: TabItem.meditations.iconName) }
                .tag(TabItem.meditations)
            
            // 3. Entrega a ação ao HomeScreen
            HomeScreen(
                onNavigateToChat: onNavigateToChat,
                onNavigateToProfile: onNavigateToProfile)
                .tabItem { Label(TabItem.home.title, systemImage: TabItem.home.iconName) }
                .tag(TabItem.home)
            
            BookingScreen()
                .tabItem { Label(TabItem.booking.title, systemImage: TabItem.booking.iconName) }
                .tag(TabItem.booking)
        }
        .tint(Color.brandRed)
    }
}

struct MenuScreen_Previews: PreviewProvider {
    static var previews: some View {
        MenuScreen(onNavigateToChat: {},
                   onNavigateToProfile: {})
            .environmentObject(SessionViewModel())
    }
}
