import SwiftUI

// --- Cores (Mantidas do teu código) ---
extension Color {
    static let cardWhite = Color.white
    static let textGray = Color(red: 107/255, green: 114/255, blue: 128/255) // #6B7280
    static let promoYellowBg = Color(red: 255/255, green: 249/255, blue: 196/255) // #FFF9C4
    static let promoYellowText = Color(red: 183/255, green: 121/255, blue: 31/255) // #B7791F
    static let spotifyGreen = Color(red: 29/255, green: 185/255, blue: 84/255) // #1DB954
    static let backgroundGray = Color(UIColor.systemGray6)
    
    // Cores das Redes Sociais
    static let instaPink = Color(red: 225/255, green: 48/255, blue: 108/255)
    static let facebookBlue = Color(red: 24/255, green: 119/255, blue: 242/255)
    static let youtubeRed = Color(red: 255/255, green: 0/255, blue: 0/255)
}

// --- HomeScreen Principal ---
struct HomeScreen: View {
    // Instancia o ViewModel
    @StateObject private var viewModel = HomeViewModel()
    
    var body: some View {
        ZStack {
            // Fundo cinza para destacar os cards
            Color.backgroundGray.ignoresSafeArea()
            
            switch viewModel.uiState {
            case .loading:
                ProgressView()
                    .scaleEffect(1.5)
                
            case .error(let message):
                VStack {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.largeTitle)
                        .foregroundColor(.red)
                    Text("Erro: \(message)")
                        .multilineTextAlignment(.center)
                        .padding()
                }
                
            case .success(let courses):
                // Scroll Vertical Principal
                ScrollView {
                    VStack(spacing: 24) { // Espaçamento global entre secções
                        
                        // 1. Logo (AlmanaBocaLogo)
                        // Substitui "fork.knife.circle" pelo nome do teu asset real quando tiveres
                        Image(systemName: "fork.knife.circle")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(height: 80)
                            .foregroundColor(.brandRed) // Usa a cor definida no LoginScreen
                            .padding(.top, 16)
                        
                        // 2. Secção: Programas
                        VStack(alignment: .leading, spacing: 16) {
                            // Header (SummaryTopPageText)
                            Text("Programas de Coaching") // R.string.lbl_coach_programs_available
                                .font(.headline)
                                .fontWeight(.bold)
                                .padding(.horizontal)
                            
                            if !courses.isEmpty {
                                // LazyRow equivalente
                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 16) {
                                        ForEach(courses) { item in
                                            ProgramCarouselCard(item: item)
                                        }
                                    }
                                    .padding(.horizontal) // Padding nas pontas do scroll
                                    .padding(.bottom, 10) // Espaço para a sombra não cortar
                                }
                            } else {
                                Text("Não há programas disponíveis no momento.")
                                    .font(.subheadline)
                                    .foregroundColor(.textGray)
                                    .padding(.horizontal)
                            }
                        }
                        
                        // 3. Secção: Meditação
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Círculos de Meditação") // R.string.lbl_meditations_circles
                                .font(.headline)
                                .fontWeight(.bold)
                                .padding(.horizontal)
                            
                            MeditationCircleCard()
                                .padding(.horizontal)
                        }
                        
                        // 4. Secção: Redes Sociais & Spotify
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Encontra-me") // R.string.lbl_find_me
                                .font(.headline)
                                .fontWeight(.bold)
                                .padding(.horizontal)
                            
                            SocialMediaCard(
                                instagramUrl: "https://instagram.com/almanaboca",
                                facebookUrl: "https://facebook.com/almanaboca",
                                youtubeUrl: "https://youtube.com/@almanaboca"
                            )
                            .padding(.horizontal)
                            
                            SpotifyButton(spotifyUrl: "https://open.spotify.com/show/trupodcast")
                                .padding(.horizontal)
                        }
                        
                        // Espaço extra no fundo (equivalente ao Spacer(100.dp))
                        Spacer().frame(height: 100)
                    }
                }
            }
        }
    }
}

// MARK: - Componentes Auxiliares

// --- Card do Programa (Carrossel) ---
struct ProgramCarouselCard: View {
    let item: HomeItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Título
            Text(item.coachProgram.isEmpty ? "Programa de Coaching" : item.coachProgram)
                .font(.title3)
                .fontWeight(.bold)
                .lineLimit(2)
                .foregroundColor(.black)
            
            // Descrição
            Text(item.whatToExpectProgram.isEmpty ? "Sem descrição disponível." : item.whatToExpectProgram)
                .font(.caption)
                .foregroundColor(.textGray)
                .lineLimit(4)
                .multilineTextAlignment(.leading)
            
            Divider()
            
            // Datas (Ícone Calendar)
            DetailRowSmall(iconName: "calendar", text: "\(item.coachDateStart) - \(item.coachDateEnd)")
            
            // Vagas (Ícone Groups -> person.3)
            DetailRowSmall(iconName: "person.3", text: "\(item.coachVacancies) vagas restantes")
            
            // Desconto (Ícone LocalOffer -> tag.fill)
            if item.coachDiscount > 0 {
                HStack {
                    Image(systemName: "tag.fill")
                        .font(.caption)
                    Text("\(Int(item.coachDiscount))% OFF")
                        .font(.caption)
                        .fontWeight(.bold)
                }
                .padding(8)
                .background(Color.promoYellowBg)
                .foregroundColor(.promoYellowText)
                .cornerRadius(8)
                .padding(.top, 4)
            }
        }
        .padding(16)
        .frame(width: 300) // Largura fixa
        .background(Color.cardWhite)
        .cornerRadius(16)
        // Sombra (Elevation)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// --- Card de Meditação ---
struct MeditationCircleCard: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Meditações em Grupo") // R.string.lbl_meditations_in_group
                .font(.title3)
                .fontWeight(.bold)
                .foregroundColor(.black)
            
            Text("Sem descrição disponível.")
                .font(.caption)
                .foregroundColor(.textGray)
                .lineLimit(4)
            
            Divider()
            
            DetailRowSmall(iconName: "calendar", text: "31/01/2026")
            
            DetailRowSmall(iconName: "mappin.and.ellipse", text: "Leve-me até lá")
        }
        .padding(16)
        .frame(maxWidth: .infinity) // Ocupa a largura total
        .background(Color.cardWhite)
        .cornerRadius(20)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// --- Card Redes Sociais ---
struct SocialMediaCard: View {
    let instagramUrl: String
    let facebookUrl: String
    let youtubeUrl: String
    
    var body: some View {
        HStack {
            // O Spacer e o distribution funcionam automaticamente no HStack
            Spacer()
            SocialIconItem(iconName: "link", color: .instaPink, url: instagramUrl)
            Spacer()
            SocialIconItem(iconName: "link", color: .facebookBlue, url: facebookUrl)
            Spacer()
            SocialIconItem(iconName: "link", color: .youtubeRed, url: youtubeUrl)
            Spacer()
        }
        .padding(.vertical, 24)
        .background(Color.cardWhite)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

struct SocialIconItem: View {
    let iconName: String // SF Symbol name
    let color: Color
    let url: String
    
    var body: some View {
        // Link nativo do SwiftUI (substitui o UriHandler)
        Link(destination: URL(string: url) ?? URL(string: "https://google.com")!) {
            Image(systemName: iconName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 32, height: 32)
                .foregroundColor(color)
                .padding(8) // Aumenta a área de toque
        }
    }
}

// --- Botão Spotify ---
struct SpotifyButton: View {
    let spotifyUrl: String
    
    var body: some View {
        Link(destination: URL(string: spotifyUrl) ?? URL(string: "https://spotify.com")!) {
            HStack {
                Image(systemName: "headphones")
                    .font(.title2)
                Spacer().frame(width: 12)
                Text("Ouve-me no Spotify")
                    .font(.title3)
                    .fontWeight(.bold)
            }
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(Color.spotifyGreen)
            .clipShape(Capsule()) // Botão totalmente redondo
            .shadow(radius: 4)
        }
    }
}

// --- Linha de Detalhe (Pequena) ---
struct DetailRowSmall: View {
    let iconName: String
    let text: String
    
    var body: some View {
        HStack(alignment: .center) {
            Image(systemName: iconName)
                .font(.system(size: 18))
                .foregroundColor(.black)
                .frame(width: 20) // Largura fixa para alinhar ícones
            
            Spacer().frame(width: 8)
            
            Text(text)
                .font(.body)
                .fontWeight(.semibold)
                .foregroundColor(.black)
        }
    }
}

// MARK: - Preview
struct HomeScreen_Previews: PreviewProvider {
    static var previews: some View {
        HomeScreen()
    }
}
