import SwiftUI
import FirebaseCore // <--- IMPORTANTE
import FirebaseMessaging // <--- NOVO
import UserNotifications // <--- NOVO

// Criar o Adaptador para o Firebase arrancar e gerir Notificações
class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        FirebaseApp.configure() // <--- AQUI ACONTECE A MAGIA
        
        // --- NOVA LÓGICA DE NOTIFICAÇÕES ---
        UNUserNotificationCenter.current().delegate = self
        
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { granted, error in
            if granted {
                print("Permissão de notificações concedida!")
            }
        }
        
        application.registerForRemoteNotifications()
        Messaging.messaging().delegate = self
        
        return true
    }
    
    // Liga o Token da Apple ao Firebase
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    // Recebe o Token do FCM
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("FCM Token gerado: \(fcmToken ?? "")")
    }
    
    // Permite mostrar a notificação mesmo se a App estiver aberta no ecrã (Foreground)
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .badge, .sound])
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
