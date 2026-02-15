import SwiftUI

// --- Constantes e Cores ---


// Configurações Admin
let ADMIN_EMAILS = ["martamartins340@gmail.com", "ivofernandes12@gmail.com"]
let TARGET_WHATSAPP_NUMBER = "351912345678"

struct HomeScreen: View {
    @StateObject private var viewModel = HomeViewModel()
    @EnvironmentObject var sessionViewModel: SessionViewModel
    
    var body: some View {
        ZStack {
            // MUDANÇA: Fundo Branco
            Color.white.ignoresSafeArea()
            
            switch viewModel.uiState {
            case .loading:
                ProgressView()
                    .tint(.accentPurple) // Cor personalizada para o loader
                
            case .error(let message):
                VStack {
                    Text("Erro ao carregar dados")
                        .foregroundColor(.red)
                        .bold()
                    Text(message)
                        .font(.caption)
                        .foregroundColor(.gray)
                }
                
            case .success(let items):
                let coachingItems = items.filter { !$0.coachProgram.isEmpty }
                let meditationItem = items.first { !$0.meditationCirclesType.isEmpty }
                
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 24) {
                        
                        // --- CABEÇALHO ---
                        HStack {
                        
                            
                            Spacer()
                            
                            // Botão Perfil (Menu)
                            Button(action: {
                                // Navegar para Perfil
                            }) {
                                Image(systemName: "person.circle")
                                    .font(.system(size: 28))
                                    .foregroundColor(.textDark)
                            }
                            .padding(.trailing, 16)
                        }
                        .overlay(alignment: .center) {
                            
                        }
                        
                        // --- SECÇÃO: PROGRAMAS ---
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Programas de Coaching Disponíveis")
                                .font(.headline)
                                .bold()
                                .foregroundColor(.textDark) // Forçar cor escura no fundo branco
                                .padding(.horizontal)
                            
                            if !coachingItems.isEmpty {
                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 16) {
                                        ForEach(coachingItems) { item in
                                            ProgramCarouselCard(item: item)
                                        }
                                    }
                                    .padding(.horizontal)
                                }
                            } else {
                                Text("Não existem programas disponíveis de momento.")
                                    .font(.subheadline)
                                    .foregroundColor(.textGray)
                                    .padding(.horizontal)
                            }
                        }
                        
                        // --- SECÇÃO: MEDITAÇÃO ---
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Círculos de Meditação")
                                .font(.headline)
                                .bold()
                                .foregroundColor(.textDark)
                                .padding(.horizontal)
                            
                            if let medItem = meditationItem {
                                MeditationCircleCard(
                                    item: medItem,
                                    userName: sessionViewModel.currentUser?.name ?? "Alguém"
                                )
                                .padding(.horizontal)
                            } else {
                                Text("Novas datas em breve.")
                                    .foregroundColor(.textGray)
                                    .padding(.horizontal)
                            }
                        }
                        
                        // --- SECÇÃO: SOBRE MIM ---
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Sobre Mim")
                                .font(.headline)
                                .bold()
                                .foregroundColor(.textDark)
                                .padding(.horizontal)
                            
                            HStack(alignment: .top, spacing: 16) {
                                Image("marta_photo")
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: 110, height: 110)
                                    .clipShape(Circle())
                                    .shadow(radius: 3) // Sombra leve na foto
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Olá, eu sou a Marta!")
                                        .font(.headline)
                                        .bold()
                                        .foregroundColor(.textDark)
                                    
                                    Text("A minha missão é ajudar-te a encontrar a tua voz e o teu equilíbrio...")
                                        .font(.caption)
                                        .foregroundColor(.textGray)
                                        .lineLimit(5)
                                }
                            }
                            .padding(.horizontal)
                        }
                        
                        // --- REDES SOCIAIS & SPOTIFY ---
                        SocialMediaRow(
                            instagram: "https://instagram.com/almanaboca",
                            facebook: "https://facebook.com/almanaboca",
                            youtube: "https://youtube.com/@almanaboca"
                        )
                        .padding(.horizontal)
                        
                        SpotifyButton(url: "https://open.spotify.com/show/trupodcast")
                            .padding(.horizontal)
                        
                        Spacer().frame(height: 100)
                    }
                    .padding(.vertical)
                }
            }
        }
    }
    
    func checkAdminAccess() {
        let email = sessionViewModel.currentUser?.email ?? ""
        if ADMIN_EMAILS.contains(email) {
            print("Bem-vinda Admin!")
        }
    }
}

// MARK: - Subcomponentes

struct ProgramCarouselCard: View {
    let item: HomeItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(item.coachProgram.isEmpty ? "Programa" : item.coachProgram)
                .font(.title3)
                .bold()
                .foregroundColor(.textDark)
                .lineLimit(2)
            
            Text(item.whatToExpectProgram.isEmpty ? "Sem descrição." : item.whatToExpectProgram)
                .font(.caption)
                .foregroundColor(.textGray)
                .lineLimit(4)
            
            Divider()
            
            DetailRowSmall(icon: "calendar", text: item.coachStartDate.isEmpty ? "Datas a anunciar" : "\(item.coachStartDate) - \(item.coachDateEnd)")
            
            DetailRowSmall(icon: "person.2", text: "\(item.coachVacancies) vagas restantes")
            
            if item.coachDiscount > 0 {
                HStack {
                    Image(systemName: "tag.fill")
                        .resizable()
                        .frame(width: 12, height: 12)
                    Text("\(Int(item.coachDiscount))% OFF")
                        .font(.caption)
                        .bold()
                }
                .padding(8)
                .background(Color.promoYellowBg)
                .foregroundColor(.promoYellowText)
                .cornerRadius(8)
            }
        }
        .padding(16)
        .frame(width: 300)
        // Sombra mais suave para fundo branco
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
        // Borda subtil para destacar do fundo branco
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.gray.opacity(0.1), lineWidth: 1)
        )
    }
}

struct MeditationCircleCard: View {
    let item: HomeItem
    let userName: String
    
    var sessionState: (text: String, isOpen: Bool) {
        let dateString = item.meditationCirclesNextSession.trimmingCharacters(in: .whitespacesAndNewlines)
        if dateString.isEmpty { return ("Data a definir", false) }
        
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        
        if let date = formatter.date(from: dateString) {
            if date < Date() { return ("Data a definir", false) }
            let deadline = Calendar.current.date(bySettingHour: 11, minute: 30, second: 0, of: date) ?? date
            let isOpen = Date() < deadline
            return ("Próxima: \(dateString)", isOpen)
        }
        
        formatter.dateFormat = "dd.MM.yyyy"
        if let date = formatter.date(from: dateString) {
             if date < Date() { return ("Data a definir", false) }
             return ("Próxima: \(dateString)", true)
        }
        
        return ("Data a definir", false)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(item.meditationCirclesType.isEmpty ? "Meditações em Grupo" : item.meditationCirclesType)
                .font(.title3)
                .bold()
                .foregroundColor(.textDark)
            
            Text(item.meditationCirclesDesc.isEmpty ? "Junta-te a nós..." : item.meditationCirclesDesc)
                .font(.caption)
                .foregroundColor(.textGray)
            
            Divider()
            
            DetailRowSmall(icon: "calendar", text: sessionState.text)
            DetailRowSmall(icon: "mappin.and.ellipse", text: item.meditationCirclesLocation)
            
            if item.meditationCirclesPrice > 0 {
                DetailRowSmall(icon: "eurosign.circle", text: String(format: "%.2f €", item.meditationCirclesPrice))
            }
            
            Button(action: {
                openWhatsApp()
            }) {
                HStack {
                    Image(systemName: "paperplane.fill")
                    Text(sessionState.isOpen ? "INSCREVER AGORA" : "INSCRIÇÕES FECHADAS")
                        .bold()
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(sessionState.isOpen ? Color.whatsAppGreen : Color.gray)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .disabled(!sessionState.isOpen)
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(20)
        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(Color.gray.opacity(0.1), lineWidth: 1)
        )
    }
    
    func openWhatsApp() {
        let message = "Nome: \(userName), conta comigo."
        let urlString = "https://wa.me/\(TARGET_WHATSAPP_NUMBER)?text=\(message.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? "")"
        
        if let url = URL(string: urlString) {
            UIApplication.shared.open(url)
        }
    }
}

struct DetailRowSmall: View {
    let icon: String
    let text: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.accentPurple)
                .frame(width: 20)
            Text(text)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(.textDark)
        }
    }
}

struct SocialMediaRow: View {
    let instagram: String
    let facebook: String
    let youtube: String
    
    var body: some View {
        HStack {
            Spacer()
            SocialIcon(icon: "link", color: .pink, url: instagram)
            Spacer()
            SocialIcon(icon: "link", color: .blue, url: facebook)
            Spacer()
            SocialIcon(icon: "play.rectangle.fill", color: .red, url: youtube)
            Spacer()
        }
        .padding(.vertical, 8)
    }
}

struct SocialIcon: View {
    let icon: String
    let color: Color
    let url: String
    
    var body: some View {
        Button(action: {
            if let link = URL(string: url) { UIApplication.shared.open(link) }
        }) {
            Image(systemName: icon)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 32, height: 32)
                .foregroundColor(color)
        }
    }
}

struct SpotifyButton: View {
    let url: String
    
    var body: some View {
        Button(action: {
            if let link = URL(string: url) {
                UIApplication.shared.open(link)
            }
        }) {
            HStack {
                Image(systemName: "headphones")
                    .font(.title2)
                
                Spacer().frame(width: 12)
                
                Text("Ouve-me no Spotify")
                    .font(.headline)
                    .fontWeight(.bold)
            }
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(Color.spotifyGreen)
            .clipShape(Capsule())
            .shadow(radius: 4, x: 0, y: 2)
        }
    }
}

// Extensão para a cor do Spotify
extension Color {
    static let spotifyGreen = Color(red: 29/255, green: 185/255, blue: 84/255) // #1DB954
}

// MARK: - Previews
// ISTO PREVINE O CRASH NO PREVIEW DO XCODE
struct HomeScreen_Previews: PreviewProvider {
    static var previews: some View {
        HomeScreen()
            .environmentObject(SessionViewModel()) // <--- AQUI ESTÁ A CORREÇÃO PARA O PREVIEW
    }
}
