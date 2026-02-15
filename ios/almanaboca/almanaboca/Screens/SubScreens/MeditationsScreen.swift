import SwiftUI

// --- Cores Específicas ---


struct MeditationsScreen: View {
    @StateObject private var viewModel = MeditationsViewModel()
    
    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()
            
            VStack(spacing: 0) {
                // Título da Página (Header)
                VStack(alignment: .leading) {
                    Text("Jornada de 7 Dias") // SummaryTopPageText
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .padding(.top, 16)
                        .padding(.horizontal)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                
                // Lista Scrollável
                ScrollView {
                    VStack(spacing: 24) {
                        
                        // ITEM 1: O HERO CARD
                        if let selected = viewModel.selectedMeditation {
                            VStack(alignment: .leading) {
                                HeroPlayerCard(
                                    meditation: selected,
                                    onPlayClick: {
                                        // Lógica de Play
                                        print("Play clicado em \(selected.title)")
                                    }
                                )
                                
                                Spacer().frame(height: 24)
                                
                                Text("Sessões Disponíveis")
                                    .font(.title2)
                                    .fontWeight(.bold)
                                    .foregroundColor(.textDark)
                            }
                            .padding(.horizontal)
                            .padding(.top, 8)
                        }
                        
                        // ITEM 2...N: A Lista
                        // LazyVStack é o equivalente ao LazyColumn do Compose
                        LazyVStack(spacing: 12) {
                            ForEach(viewModel.allMeditations) { meditation in
                                MeditationListItem(
                                    meditation: meditation,
                                    isSelected: meditation.id == viewModel.selectedMeditation?.id,
                                    onClick: {
                                        withAnimation {
                                            viewModel.selectMeditation(meditation)
                                        }
                                    }
                                )
                                .padding(.horizontal)
                            }
                        }
                        // Padding bottom extra para scroll
                        Spacer().frame(height: 100)
                    }
                }
            }
        }
    }
}

// MARK: - Componentes Auxiliares

// --- HERO PLAYER CARD ---
struct HeroPlayerCard: View {
    let meditation: MeditationUiModel
    let onPlayClick: () -> Void
    
    // Estado local de UI
    @State private var isPlaying = false
    @State private var progress: Double = 0.3
    
    var body: some View {
        ZStack {
            // 1. Fundo Gradiente
            LinearGradient(
                colors: [.heroPurple, .heroBlue],
                startPoint: .top,
                endPoint: .bottom
            )
            
            // 2. Conteúdo
            VStack(spacing: 0) {
                
                // Topo: Indicador do Dia
                Text(meditation.dayTitle.uppercased())
                    .font(.caption)
                    .tracking(2) // Letter spacing
                    .foregroundColor(Color.white.opacity(0.7))
                    .padding(.top, 24)
                
                Spacer()
                
                // Centro: Ícone e Título
                VStack(spacing: 16) {
                    Image(systemName: "headphones") // SF Symbol
                        .font(.system(size: 48))
                        .foregroundColor(.white)
                    
                    VStack(spacing: 8) {
                        Text(meditation.title)
                            .font(.title2)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .multilineTextAlignment(.center)
                        
                        Text("\(meditation.duration) • Relaxamento")
                            .font(.body)
                            .foregroundColor(Color.white.opacity(0.8))
                    }
                }
                
                Spacer()
                
                // Baixo: Controlos
                VStack(spacing: 16) {
                    // Slider
                    Slider(value: $progress, in: 0...1)
                        .tint(.white) // Cor da barra ativa
                        .padding(.horizontal, 24)
                    
                    // Botão Play/Pause
                    Button(action: {
                        isPlaying.toggle()
                        onPlayClick()
                    }) {
                        ZStack {
                            Circle()
                                .fill(Color.white)
                                .frame(width: 64, height: 64)
                            
                            Image(systemName: isPlaying ? "pause.fill" : "play.fill")
                                .font(.title)
                                .foregroundColor(.heroBlue)
                        }
                    }
                }
                .padding(.bottom, 24)
            }
        }
        .frame(height: 350) // Altura fixa
        .cornerRadius(24)
        .shadow(color: .heroBlue.opacity(0.4), radius: 10, x: 0, y: 5)
    }
}

// --- LIST ITEM ---
struct MeditationListItem: View {
    let meditation: MeditationUiModel
    let isSelected: Bool
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 16) {
                
                // 1. Ícone do Estado
                ZStack {
                    Circle()
                        .fill(iconBackgroundColor)
                        .frame(width: 40, height: 40)
                    
                    Image(systemName: iconName)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(iconColor)
                }
                
                // 2. Textos
                VStack(alignment: .leading, spacing: 4) {
                    Text(meditation.dayTitle)
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(meditation.isLocked ? .textDark : .meditationPrimary)
                    
                    Text(meditation.title)
                        .font(.body)
                        .fontWeight(.semibold)
                        .foregroundColor(meditation.isLocked ? .textDark : .textDark)
                }
                
                Spacer()
                
                // 3. Duração
                Text(meditation.duration)
                    .font(.subheadline)
                    .foregroundColor(.textGray)
            }
            .padding(16)
            .background(isSelected ? Color.meditationPrimary.opacity(0.1) : Color.white)
            .cornerRadius(16)
            // Borda condicional (overlay)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(isSelected ? Color.meditationPrimary : Color.clear, lineWidth: 1)
            )
            // Sombra condicional
            .shadow(color: Color.black.opacity(isSelected ? 0 : 0.05), radius: 2, x: 0, y: 1)
        }
        .disabled(meditation.isLocked) // Desativa o clique se estiver bloqueado
    }
    
    // Lógica para determinar Ícones e Cores
    var iconName: String {
        if meditation.isLocked { return "lock.fill" }
        if meditation.isCompleted { return "checkmark.circle.fill" }
        if isSelected { return "play.fill" }
        return "headphones"
    }
    
    var iconColor: Color {
        if meditation.isLocked { return .gray }
        if meditation.isCompleted { return Color(red: 16/255, green: 185/255, blue: 129/255) } // Verde
        return .meditationPrimary
    }
    
    var iconBackgroundColor: Color {
        if meditation.isLocked { return Color.gray.opacity(0.1) }
        return Color(red: 224/255, green: 231/255, blue: 255/255) // Azul clarinho
    }
}

// Preview
struct MeditationsScreen_Previews: PreviewProvider {
    static var previews: some View {
        MeditationsScreen()
    }
}
