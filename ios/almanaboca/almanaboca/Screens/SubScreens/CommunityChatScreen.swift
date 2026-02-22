import SwiftUI

struct CommunityChatScreen: View {
    @StateObject private var chatViewModel = ChatViewModel()
    @EnvironmentObject var sessionViewModel: SessionViewModel
    @State private var messageText = ""
    
    // Auto-scroll
    @Namespace var bottomID
    
    var body: some View {
        ZStack {
            // Fundo Cinza Claro (igual ao Android)
            Color(UIColor.systemGray6).ignoresSafeArea()
            
            VStack(spacing: 0) {
                // --- LISTA DE MENSAGENS ---
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(chatViewModel.messages) { msg in
                                let isMine = msg.senderId == sessionViewModel.currentUser?.id
                                ChatBubble(message: msg, isMine: isMine)
                                    .id(msg.id)
                            }
                            // Marcador invisível para scroll automático
                            Spacer().frame(height: 1).id(bottomID)
                        }
                        .padding()
                    }
                    // Scroll automático quando chega nova mensagem
                    .onChange(of: chatViewModel.messages.count) { _ in
                        withAnimation {
                            proxy.scrollTo(bottomID, anchor: .bottom)
                        }
                    }
                    .onAppear {
                        // Scroll inicial para o fundo sem animação
                        proxy.scrollTo(bottomID, anchor: .bottom)
                    }
                }
                
                // --- INPUT DE MENSAGEM ---
                HStack(alignment: .bottom) {
                    TextField("Escreve algo...", text: $messageText, axis: .vertical)
                        .padding(12)
                        .background(Color.white)
                        .cornerRadius(20)
                        .lineLimit(1...5)
                    
                    Button(action: {
                        guard let user = sessionViewModel.currentUser else { return }
                        chatViewModel.sendMessage(
                            text: messageText,
                            userId: user.id,
                            userName: user.name
                        )
                        messageText = ""
                    }) {
                        Image(systemName: "paperplane.fill")
                            .font(.system(size: 20))
                            .foregroundColor(.white)
                            .frame(width: 44, height: 44)
                            .background(messageText.isEmpty ? Color.gray : Color.accentPurple)
                            .clipShape(Circle())
                    }
                    .disabled(messageText.isEmpty)
                }
                .padding()
                .background(Color.white) // Fundo da barra de input
                .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: -5)
            }
        }
        .navigationTitle("Comunidade")
        navigationBarTitleDisplayMode(.inline)
                // 👇 CORREÇÃO AQUI: Forçar a barra de navegação a ser opaca (Sólida)
                .toolbarBackground(.visible, for: .navigationBar)
                .toolbarBackground(Color(UIColor.systemGray6), for: .navigationBar)
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Componente Chat Bubble
struct ChatBubble: View {
    let message: ChatMessage
    let isMine: Bool
    
    var timeFormatted: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: message.date)
    }
    
    var body: some View {
        HStack(alignment: .bottom, spacing: 8) {
            if isMine { Spacer() }
            
            // Layout da Bolha
            VStack(alignment: isMine ? .trailing : .leading, spacing: 4) {
                
                // Nome (Só aparece se não for eu)
                if !isMine {
                    Text(message.senderName)
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(Color.accentPurple)
                        .padding(.horizontal, 4)
                }
                
                // Texto + Hora
                HStack(alignment: .bottom, spacing: 8) {
                    Text(message.text)
                        .foregroundColor(isMine ? .white : .textDark)
                    
                    Text(timeFormatted)
                        .font(.caption2)
                        .foregroundColor(isMine ? .white.opacity(0.7) : .gray)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(isMine ? Color.accentPurple : Color.white)
                // Formato da Bolha (Arredondado com bico)
                .clipShape(
                    UnevenRoundedRectangle(
                        topLeadingRadius: 18,
                        bottomLeadingRadius: isMine ? 18 : 4,
                        bottomTrailingRadius: isMine ? 4 : 18,
                        topTrailingRadius: 18
                    )
                )
                .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
            }
            
            if !isMine { Spacer() }
        }
    }
}
