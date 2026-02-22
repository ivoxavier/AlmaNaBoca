//
//  LoginScreen.swift
//  almanaboca
//

import SwiftUI

struct LoginScreen: View {
    // Parâmetros recebidos
    var uiState: LoginUiState
    var onLoginClick: (String, String) -> Void

    // Estados locais
    @State private var email = ""
    @State private var password = ""
    @State private var isSplashFinished = false
    @State private var showFormAnimation = false
    @State private var showPassword = false
    
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
            // 1. MUDANÇA AQUI: Fundo Branco Puro
            Color.white
                .ignoresSafeArea()

            if !isSplashFinished {
                // --- FASE 1: SPLASH ---
                VStack {
                    // ATENÇÃO: Usa o nome do teu LOGO PRETO aqui
                    Image("almanaboca_white")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 250)
                        .padding(32)
                }
                .transition(.opacity)
            } else {
                // --- FASE 2: LOGIN ---
                VStack(spacing: 0) {
                    
                    Spacer().frame(height: 30)
                    
                    // Logo de Topo
                    Image("almanaboca_white") // ATENÇÃO: Usa o nome do teu LOGO PRETO aqui
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 120)
                        .padding(.bottom, 20)
                    
                    // --- ÁREA DO CARTÃO (CARD) ---
                    if showFormAnimation {
                        VStack(spacing: 24) {
                            
                            // Título do Cartão
                            Text("Login into Your Account")
                                .font(.system(size: 16, weight: .medium, design: .rounded))
                                .foregroundColor(.gray)
                                .padding(.top, 10)
                            
                            // Campos de Texto
                            VStack(spacing: 16) {
                                // Campo Account (Email)
                                AndroidStyleTextField(
                                    text: $email,
                                    placeholder: "Account",
                                    iconName: "envelope",
                                    isSecure: false
                                )
                                .focused($focusedField, equals: .email)
                                .submitLabel(.next)
                                .onSubmit { focusedField = .password }
                                
                                // Campo Password
                                AndroidStyleTextField(
                                    text: $password,
                                    placeholder: "Password",
                                    iconName: "lock",
                                    isSecure: true,
                                    showPassword: $showPassword
                                )
                                .focused($focusedField, equals: .password)
                                .submitLabel(.go)
                                .onSubmit {
                                    if !isLoading && !email.isEmpty && !password.isEmpty {
                                        onLoginClick(email, password)
                                    }
                                }
                            }
                            
                            // Botão Login (Pequeno e arredondado como no Android)
                            Button(action: {
                                if !isLoading {
                                    focusedField = nil
                                    onLoginClick(email, password)
                                }
                            }) {
                                ZStack {
                                    if isLoading {
                                        ProgressView().tint(Color.white)
                                    } else {
                                        Text("LOGIN")
                                            .font(.system(size: 14, weight: .bold))
                                            .tracking(1)
                                    }
                                }
                                .frame(width: 140, height: 45)
                                // Fica cinza se os campos estiverem vazios, vermelho se estiver preenchido
                                .background((email.isEmpty || password.isEmpty) ? Color.gray.opacity(0.3) : Color.brandRed)
                                .foregroundColor((email.isEmpty || password.isEmpty) ? .gray : .white)
                                .clipShape(Capsule())
                            }
                            .disabled(isLoading || email.isEmpty || password.isEmpty)
                            .padding(.top, 8)
                            
                            // Texto Legal de Política de Privacidade
                            Text("By logging in, you confirm that you have read and\naccept our ")
                                .font(.system(size: 12))
                                .foregroundColor(.gray)
                            + Text("Privacy Policy")
                                .font(.system(size: 12))
                                .foregroundColor(.brandRed)
                            + Text(" and the processing of\nyour data.")
                                .font(.system(size: 12))
                                .foregroundColor(.gray)
                                
                        }
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 24)
                        .padding(.bottom, 32)
                        // ESTILO DO CARTÃO BRANCO (Sombra e Bordas)
                        .background(Color.white)
                        .cornerRadius(24)
                        .shadow(color: Color.black.opacity(0.06), radius: 15, x: 0, y: 8)
                        .padding(.horizontal, 24)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        
                        Spacer()
                        
                        // --- RODAPÉ ---
                        VStack(spacing: 20) {
                            Button(action: {
                                // Ação Lost Password
                            }) {
                                Text("Lost Password")
                                    .font(.system(size: 14, weight: .medium, design: .rounded))
                                    .underline()
                                    .foregroundColor(.gray)
                            }
                            
                            Text("v1.0\n\nDeveloped by Ivo Xavier <ixsvf>")
                                .font(.system(size: 12))
                                .foregroundColor(.gray.opacity(0.5))
                                .multilineTextAlignment(.center)
                        }
                        .padding(.bottom, 30)
                    }
                }
                .ignoresSafeArea(.keyboard, edges: .bottom)
            }
        }
        // --- ANIMAÇÕES ---
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
        // --- ALERTAS ---
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

// MARK: - Componente de Input ao estilo Android

struct AndroidStyleTextField: View {
    @Binding var text: String
    var placeholder: String
    var iconName: String
    var isSecure: Bool
    var showPassword: Binding<Bool>? = nil
    
    var body: some View {
        HStack(spacing: 15) {
            // Ícone da Esquerda
            Image(systemName: iconName)
                .foregroundColor(.gray.opacity(0.7))
                .frame(width: 20)
            
            // Campo de Texto
            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .foregroundColor(.gray.opacity(0.7))
                        .font(.system(size: 16, design: .rounded))
                }
                
                if isSecure && !(showPassword?.wrappedValue ?? false) {
                    SecureField("", text: $text)
                        .foregroundColor(.black)
                        .accentColor(Color.brandRed)
                } else {
                    TextField("", text: $text)
                        .foregroundColor(.black)
                        .accentColor(Color.brandRed)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled(true)
                        .keyboardType(placeholder == "Account" ? .emailAddress : .default)
                }
            }
            
            // Ícone de Mostrar/Esconder Password
            if isSecure {
                Button(action: {
                    showPassword?.wrappedValue.toggle()
                }) {
                    Image(systemName: (showPassword?.wrappedValue ?? false) ? "eye.slash" : "eye")
                        .foregroundColor(.gray.opacity(0.7))
                }
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(16)
        // Sombra leve para destacar o input (tal como no Android)
        .shadow(color: Color.black.opacity(0.04), radius: 5, x: 0, y: 3)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.gray.opacity(0.15), lineWidth: 1)
        )
    }
}

// MARK: - Previews
struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginScreen(uiState: .idle, onLoginClick: { _, _ in })
    }
}
