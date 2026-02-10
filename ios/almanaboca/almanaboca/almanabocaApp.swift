import SwiftUI
import FirebaseCore // <--- IMPORTANTE

// Criar o Adaptador para o Firebase arrancar
class AppDelegate: NSObject, UIApplicationDelegate {
  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    FirebaseApp.configure() // <--- AQUI ACONTECE A MAGIA
    return true
  }
}

@main
struct almanabocaApp: App {
    // Injetar o adaptador
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            AlmanaBocaNavigation()
        }
    }
}
