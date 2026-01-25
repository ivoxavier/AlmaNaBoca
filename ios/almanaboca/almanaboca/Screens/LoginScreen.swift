//
//  LoginScreen.swift
//  almanaboca
//

import SwiftUI

// Definição da cor da marca
extension Color {
    static let brandRed = Color(red: 158/255, green: 25/255, blue: 25/255)
}

struct LoginScreen: View {
    // Parâmetros recebidos
    var uiState: LoginUiState
    var onLoginClick: (String, String) -> Void

    // Estados locais
    @State private var email = ""
    @State private var password = ""
    @State private var isSplashFinished = false
    @State private var showFormAnimation = false
    
    // Helper para verificar loading
    var isLoading: Bool {
        if case .loading = uiState { return true }
        return false
    }

    var body: some View {
        ZStack {
            // Fundo Vermelho
            Color.brandRed
                .ignoresSafeArea()
            
            // Texto global branco
            .foregroundColor(.white)

            if !isSplashFinished {
                // --- FASE 1: SPLASH ---
                VStack {
                    // MUDANÇA 1: Usar a tua imagem "AppLogo"
                    // Se o teu logo for branco transparente, usa assim:
                    /*Image("AppIcon")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 150, height: 150)
                        .padding(32)
                    */
                    // DICA: Se o teu logo for preto ou colorido e quiseres forçar a ficar BRANCO
                    //   para combinar com o fundo vermelho, usa este código em vez do de cima:
                     
                    Image("AppLogo")
                        .renderingMode(.template) // Permite mudar a cor
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 280, height: 280)
                        //.foregroundColor(.white) // Pinta de branco
                        .padding(32)
                
                }
                .transition(.opacity)
            }
            else {
                // --- FASE 2: LOGIN ---
                VStack(spacing: 0) {
                    
                    Text("Bem-vindo")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Spacer().frame(height: 16)
                    
                    // MUDANÇA 2: O Logo mais pequeno no topo do formulário
                    Image("AppLogo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 200)
                        // .foregroundColor(.white) // Descomenta se precisares de forçar a cor branca
                    
                    // --- ÁREA ANIMADA ---
                    if showFormAnimation {
                        VStack(spacing: 0) {
                            
                            Spacer().frame(height: 30)
                            
                            CustomTextField(
                                text: $email,
                                label: "Conta",
                                isSecure: false
                            )
                            
                            Spacer().frame(height: 16)
                            
                            CustomTextField(
                                text: $password,
                                label: "Password",
                                isSecure: true
                            )
                            
                            Spacer().frame(height: 24)
                            
                            Button(action: {
                                if !isLoading {
                                    onLoginClick(email, password)
                                }
                            }) {
                                ZStack {
                                    if isLoading {
                                        ProgressView()
                                            .tint(.brandRed)
                                    } else {
                                        Text("Entrar")
                                            .fontWeight(.bold)
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(Color.white)
                                .foregroundColor(.brandRed)
                                .cornerRadius(8)
                            }
                            .disabled(isLoading)
                            
                            Spacer().frame(height: 32)
                            
                            Text("Esqueci-me da palavra-passe")
                                .font(.caption)
                                .underline()
                        }
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                }
                .padding(.top, 60)
                .padding(.horizontal, 24)
                .frame(maxWidth: .infinity, alignment: .top)
            }
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                withAnimation(.easeInOut(duration: 0.5)) {
                    isSplashFinished = true
                }
                
                withAnimation(.spring(response: 0.5, dampingFraction: 0.8).delay(0.1)) {
                    showFormAnimation = true
                }
            }
        }
    }
}

// MARK: - Componentes Auxiliares

struct CustomTextField: View {
  @Binding var text: String
  var label: String
  var isSecure: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                            .font(.caption)
                            .fontWeight(
                            .semibold)
                                if isSecure {
                                    SecureField("", text: $text)
                                    .padding()
                                    .background(Color.white.opacity(0.2))
                                    .cornerRadius(8)
                                    .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.white, lineWidth: 1)
                        )
                            } else {
                            TextField("", text: $text)
                            .padding()
                            .background(Color.white.opacity(0.2))
                            .cornerRadius(8)
                            .overlay(
                            RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.white, lineWidth: 1)
                    )
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled(true)
                }
        }
    }
}

// MARK: - Previews
struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        // CORREÇÃO AQUI: Passar o Enum .idle em vez da String "Idle"
    LoginScreen(uiState: .idle, onLoginClick: { _, _ in })
 }
}
