//
//  LoginScreen.swift
//  almanaboca
//

import SwiftUI

// Definição da cor da marca


struct LoginScreen: View {
    // Parâmetros recebidos
    var uiState: LoginUiState
    var onLoginClick: (String, String) -> Void

    // Estados locais
    @State private var email = ""
    @State private var password = ""
    @State private var isSplashFinished = false
    @State private var showFormAnimation = false
    
    // VARIÁVEIS PARA O ALERTA DE ERRO
    @State private var showErrorAlert = false
    @State private var errorMessage = ""
    
    // Foco dos campos
    @FocusState private var focusedField: Field?
    enum Field {
        case email, password
    }
    
    // Helper para verificar loading
    var isLoading: Bool {
        if case .loading = uiState { return true }
        return false
    }

    var body: some View {
        ZStack {
            // 1. FUNDO SÓLIDO
            Color.brandRed
                .ignoresSafeArea()
            
            // Texto global branco
            .foregroundColor(.white)

            if !isSplashFinished {
                // --- FASE 1: SPLASH ---
                VStack {
                    Image("AppLogo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 300, height: 300)
                        .padding(32)
                }
                .transition(.opacity)
            } else {
                // --- FASE 2: LOGIN ---
                VStack(spacing: 0) {
                    
                    Spacer()
                    
                    // Logo
                    Image("AppLogo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 180)
                        .padding(.bottom, 10)
                    
                  
                    
                    // --- ÁREA DO FORMULÁRIO ---
                    if showFormAnimation {
                        VStack(spacing: 20) {
                            
                            Spacer().frame(height: 20)
                            
                            // Campo Email
                            ModernTextField(
                                text: $email,
                                placeholder: "Email",
                                iconName: "envelope.fill",
                                isSecure: false
                            )
                            .focused($focusedField, equals: .email)
                            .submitLabel(.next)
                            .onSubmit { focusedField = .password }
                            
                            // Campo Password
                            ModernTextField(
                                text: $password,
                                placeholder: "Password",
                                iconName: "lock.fill",
                                isSecure: true
                            )
                            .focused($focusedField, equals: .password)
                            .submitLabel(.go)
                            .onSubmit {
                                if !isLoading { onLoginClick(email, password) }
                            }
                            
                            Spacer().frame(height: 10)
                            
                            // Botão Entrar
                            Button(action: {
                                if !isLoading {
                                    focusedField = nil
                                    onLoginClick(email, password)
                                }
                            }) {
                                ZStack {
                                    if isLoading {
                                        ProgressView()
                                            .tint(.brandRed)
                                    } else {
                                        Text("Login")
                                            .font(.system(size: 18, weight: .bold, design: .rounded))
                                            .tracking(1)
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 56)
                                .background(Color.white)
                                .foregroundColor(.brandRed)
                                .clipShape(Capsule())
                                .shadow(color: .black.opacity(0.1), radius: 5, x: 0, y: 5)
                            }
                            .disabled(isLoading)
                            .opacity(isLoading ? 0.8 : 1.0)
                            
                            // Link Esqueci Password
                            Button(action: {
                                // Ação de recuperar password
                            }) {
                                Text("Esqueci-me da palavra-passe")
                                    .font(.system(size: 14, weight: .medium, design: .rounded))
                                    .underline()
                                    .foregroundColor(.white.opacity(0.8))
                            }
                            .padding(.top, 10)
                        }
                        .padding(.horizontal, 30)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    
                    Spacer()
                }
                .padding(.top, 20)
                .ignoresSafeArea(.keyboard, edges: .bottom)
            }
        }
        // --- ANIMAÇÕES DE ENTRADA ---
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                withAnimation(.easeInOut(duration: 0.6)) {
                    isSplashFinished = true
                }
                withAnimation(.spring(response: 0.6, dampingFraction: 0.7).delay(0.1)) {
                    showFormAnimation = true
                }
            }
        }
        // --- LÓGICA DO ALERTA DE ERRO ---
        .onChange(of: uiState) { newState in
            if case .error(let message) = newState {
                self.errorMessage = message
                self.showErrorAlert = true
            }
        }
        .alert("Atenção", isPresented: $showErrorAlert) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(errorMessage)
        }
    }
}

// MARK: - Componentes Modernos

struct ModernTextField: View {
    @Binding var text: String
    var placeholder: String
    var iconName: String
    var isSecure: Bool
    
    var body: some View {
        HStack(spacing: 15) {
            Image(systemName: iconName)
                .foregroundColor(.white.opacity(0.8))
                .frame(width: 20)
            
            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .foregroundColor(.white.opacity(0.6))
                        .font(.system(size: 16, design: .rounded))
                }
                
                if isSecure {
                    SecureField("", text: $text)
                        .foregroundColor(.white)
                        .accentColor(.white)
                } else {
                    TextField("", text: $text)
                        .foregroundColor(.white)
                        .accentColor(.white)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled(true)
                        .keyboardType(placeholder == "Email" ? .emailAddress : .default)
                }
            }
        }
        .padding()
        .background(Color.white.opacity(0.2))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.white.opacity(0.3), lineWidth: 1)
        )
    }
}

// MARK: - Previews
struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginScreen(uiState: .idle, onLoginClick: { _, _ in })
    }
}
