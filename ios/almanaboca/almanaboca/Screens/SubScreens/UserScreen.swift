import SwiftUI

struct UserScreen: View {
    @EnvironmentObject var sessionViewModel: SessionViewModel
    @Environment(\.dismiss) var dismiss // Para voltar atrás
    
    @State private var hasChatAccess = false
    @State private var showDeleteDialog = false
    @State private var isDeleting = false
    
    var body: some View {
        VStack(spacing: 0) {
            
            // --- INFO DO UTILIZADOR ---
            VStack(spacing: 8) {
                let name = sessionViewModel.currentUser?.name ?? "Utilizador"
                let initial = String(name.prefix(1)).uppercased()
                
                ZStack {
                    Circle()
                        .fill(Color(UIColor.systemGray5))
                        .frame(width: 80, height: 80)
                    
                    Text(initial)
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(.gray)
                }
                .padding(.top, 24)
                
                Spacer().frame(height: 8)
                
                Text(name)
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.textDark)
                
                Text(sessionViewModel.currentUser?.email ?? "")
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }
            
            Spacer().frame(height: 32)
            
            // --- STATUS ---
            StatusCard(isActive: hasChatAccess)
                .padding(.horizontal, 24)
            
            Spacer()
            
            // --- BOTÕES DE AÇÃO ---
            VStack(spacing: 16) {
                // Botão de Logout
                Button(action: {
                    sessionViewModel.logout()
                }) {
                    HStack {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                        Text("TERMINAR SESSÃO")
                            .fontWeight(.bold)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.brandRed)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
                }
                
                // Botão de Eliminar Conta
                Button(action: {
                    showDeleteDialog = true
                }) {
                    HStack(spacing: 8) {
                        if isDeleting {
                            ProgressView().tint(.gray)
                        } else {
                            Image(systemName: "trash.fill")
                            Text("Eliminar conta permanentemente")
                        }
                    }
                    .font(.caption)
                    .foregroundColor(.gray)
                }
                .disabled(isDeleting)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
            
        }
        .navigationTitle("A minha conta")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            // Verifica o acesso assim que o ecrã abre
            sessionViewModel.verifyAccessNow(
                onSuccess: { hasChatAccess = true },
                onFailure: { hasChatAccess = false }
            )
        }
        // --- ALERTA DE CONFIRMAÇÃO ---
        .alert("Eliminar Conta", isPresented: $showDeleteDialog) {
            Button("Cancelar", role: .cancel) { }
            Button("Sim, eliminar", role: .destructive) {
                isDeleting = true
                sessionViewModel.deleteAccount(
                    onSuccess: {
                        isDeleting = false
                        // O logout automático trata da navegação
                    },
                    onFailure: { erro in
                        isDeleting = false
                        print("Erro ao eliminar: \(erro)")
                    }
                )
            }
        } message: {
            Text("Tem a certeza que deseja eliminar a sua conta permanentemente? Esta ação não pode ser desfeita e perderá o acesso à comunidade.")
        }
    }
}

// MARK: - Subcomponente StatusCard
struct StatusCard: View {
    let isActive: Bool
    
    var body: some View {
        let containerColor = isActive ? Color(red: 232/255, green: 245/255, blue: 233/255) : Color(red: 255/255, green: 235/255, blue: 238/255)
        let contentColor = isActive ? Color(red: 46/255, green: 125/255, blue: 50/255) : Color(red: 198/255, green: 40/255, blue: 40/255)
        let iconName = isActive ? "checkmark.circle.fill" : "lock.fill"
        let statusText = isActive ? "ATIVO" : "INATIVO"
        let description = isActive ? "Tens acesso exclusivo ao chat da comunidade." : "Subscrição necessária para aceder."
        
        HStack(spacing: 16) {
            Image(systemName: iconName)
                .resizable()
                .scaledToFit()
                .frame(width: 32, height: 32)
                .foregroundColor(contentColor)
            
            VStack(alignment: .leading, spacing: 2) {
                Text("Comunidade AlmaNaBoca")
                    .font(.caption)
                    .foregroundColor(.black.opacity(0.7))
                
                Text(statusText)
                    .font(.title3)
                    .fontWeight(.bold)
                    .foregroundColor(contentColor)
                
                Text(description)
                    .font(.caption2)
                    .foregroundColor(contentColor.opacity(0.8))
                    .lineLimit(2)
            }
            Spacer()
        }
        .padding(16)
        .background(containerColor)
        .cornerRadius(16)
    }
}
