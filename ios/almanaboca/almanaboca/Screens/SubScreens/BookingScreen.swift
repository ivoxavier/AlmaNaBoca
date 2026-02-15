import SwiftUI

struct BookingScreen: View {
    @StateObject private var viewModel = BookingViewModel()
    @State private var showNewBookingSheet = false
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            // Fundo
            Color.backgroundGray.ignoresSafeArea()
            
            VStack(spacing: 0) {
                // --- CABEÇALHO PERSONALIZADO (Garante que o botão aparece) ---
                HStack {
                    Text(viewModel.isAdmin ? "Gestão de Reservas" : "Minhas Marcações")
                        .font(.largeTitle)
                        .bold()
                        .foregroundColor(.textDark)
                    
                    Spacer()
                    
                    // O Botão "+" agora vive aqui, visível e seguro
                    if !viewModel.isAdmin {
                        Button(action: { showNewBookingSheet = true }) {
                            Image(systemName: "plus.circle.fill") // Ícone mais preenchido
                                .font(.system(size: 32))
                                .foregroundColor(.brandRed)
                                .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
                        }
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 16)
                .padding(.bottom, 8)
                .background(Color.backgroundGray) // Garante que o fundo do header combina
                
                // --- CONTEÚDO ---
                if viewModel.isLoading {
                    Spacer()
                    ProgressView("A carregar...")
                        .controlSize(.large)
                    Spacer()
                }
                else if viewModel.bookings.isEmpty {
                    // Estado Vazio
                    Spacer()
                    VStack(spacing: 20) {
                        Image(systemName: "calendar.badge.plus")
                            .font(.system(size: 60))
                            .foregroundColor(.gray.opacity(0.4))
                        
                        Text(viewModel.isAdmin ? "Tudo limpo!" : "Sem marcações")
                            .font(.title2)
                            .fontWeight(.bold)
                            .foregroundColor(.textDark)
                        
                        Text(viewModel.isAdmin ? "Não existem pedidos pendentes de momento." : "Começa a tua jornada de bem-estar agendando a tua primeira sessão.")
                            .font(.body)
                            .foregroundColor(.textGray)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)
                        
                        // Botão grande extra para quando está vazio
                        if !viewModel.isAdmin {
                            Button(action: { showNewBookingSheet = true }) {
                                Text("Agendar Agora")
                                    .fontWeight(.bold)
                                    .padding()
                                    .padding(.horizontal, 20)
                                    .background(Color.brandRed)
                                    .foregroundColor(.white)
                                    .cornerRadius(10)
                            }
                            .padding(.top, 10)
                        }
                    }
                    Spacer()
                }
                else {
                    // Lista de Marcações
                    ScrollView(showsIndicators: false) {
                        LazyVStack(spacing: 16) {
                            ForEach(viewModel.bookings) { booking in
                                BookingCard(booking: booking, viewModel: viewModel)
                            }
                        }
                        .padding(16)
                        
                        // Espaço no fim para scroll confortável
                        Spacer().frame(height: 40)
                    }
                    .refreshable {
                        viewModel.loadData()
                    }
                }
            }
        }
        .onAppear { viewModel.loadData() }
        .sheet(isPresented: $showNewBookingSheet) {
            NewBookingFlow(viewModel: viewModel, isPresented: $showNewBookingSheet)
        }
        .alert(isPresented: $viewModel.showAlert) {
            Alert(title: Text("Info"), message: Text(viewModel.alertMessage), dismissButton: .default(Text("OK")))
        }
    }
}

// MARK: - Card de Marcação
struct BookingCard: View {
    let booking: Booking
    @ObservedObject var viewModel: BookingViewModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Cabeçalho: Data e Status
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: "calendar")
                        .foregroundColor(.brandRed)
                    Text(booking.date)
                        .fontWeight(.semibold)
                        .foregroundColor(.textDark)
                    Text("•")
                        .foregroundColor(.textGray)
                    Text(booking.time)
                        .foregroundColor(.textDark)
                }
                .font(.subheadline)
                
                Spacer()
                
                StatusTag(status: booking.status)
            }
            
            Divider()
            
            // Corpo: Serviço
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("SERVIÇO")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.textGray)
                    
                    Text(booking.serviceType)
                        .font(.title3)
                        .fontWeight(.medium)
                        .foregroundColor(.textDark)
                }
                Spacer()
            }
            
            // Área Admin
            if viewModel.isAdmin {
                VStack(alignment: .leading, spacing: 8) {
                    Divider()
                    Text("CLIENTE").font(.caption2).bold().foregroundColor(.textGray)
                    Label(booking.clientName, systemImage: "person").font(.caption).foregroundColor(.textDark)
                    Label(booking.clientPhone, systemImage: "phone").font(.caption).foregroundColor(.textDark)
                    
                    if booking.isPending {
                        HStack {
                            Button("Aprovar") { viewModel.updateBookingStatus(booking, newStatus: "confirmed") }
                                .buttonStyle(.borderedProminent).tint(.green)
                            Button("Rejeitar") { viewModel.updateBookingStatus(booking, newStatus: "rejected") }
                                .buttonStyle(.bordered).tint(.red)
                        }
                        .padding(.top, 5)
                    }
                }
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 3, x: 0, y: 1)
        .contextMenu {
            if !viewModel.isAdmin {
                Button(role: .destructive) {
                    viewModel.cancelBooking(booking)
                } label: { Label("Cancelar", systemImage: "trash") }
            }
        }
    }
}

// MARK: - Componentes Auxiliares
struct StatusTag: View {
    let status: String
    var color: Color {
        switch status {
        case "pending": return .orange
        case "confirmed": return .green
        case "rejected": return .red
        default: return .gray
        }
    }
    
    var body: some View {
        Text(status == "pending" ? "PENDENTE" : (status == "confirmed" ? "CONFIRMADO" : "REJEITADO"))
            .font(.caption2).bold()
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(color.opacity(0.15))
            .foregroundColor(color)
            .clipShape(Capsule())
    }
}

// MARK: - Fluxo de Nova Marcação
struct NewBookingFlow: View {
    @ObservedObject var viewModel: BookingViewModel
    @Binding var isPresented: Bool
    
    @State private var selectedDate = Date()
    @State private var selectedSlot: String? = nil
    @State private var name = ""
    @State private var phone = ""
    @State private var service = ""
    
    let allSlots = ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"]
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Data") {
                    DatePicker("Escolha o dia", selection: $selectedDate, in: Date()..., displayedComponents: .date)
                        .datePickerStyle(.graphical)
                        .onChange(of: selectedDate) { newDate in
                            viewModel.checkAvailability(for: newDate)
                            selectedSlot = nil
                        }
                }
                
                Section("Horários") {
                    if viewModel.isLoading {
                        ProgressView().frame(maxWidth: .infinity)
                    } else {
                        LazyVGrid(columns: [GridItem(.adaptive(minimum: 70))], spacing: 10) {
                            ForEach(allSlots, id: \.self) { slot in
                                let isTaken = viewModel.occupiedSlots.contains(slot)
                                let isSelected = selectedSlot == slot
                                Button(slot) { selectedSlot = slot }
                                    .buttonStyle(.bordered)
                                    .tint(isSelected ? .brandRed : (isTaken ? .gray : .blue))
                                    .disabled(isTaken)
                            }
                        }
                        .padding(.vertical)
                    }
                }
                
                if selectedSlot != nil {
                    Section("Dados") {
                        TextField("Nome", text: $name)
                        TextField("Telemóvel", text: $phone).keyboardType(.phonePad)
                        TextField("Serviço", text: $service)
                    }
                    
                    Section {
                        Button("Confirmar Marcação") {
                            viewModel.createBooking(date: selectedDate, time: selectedSlot!, name: name, phone: phone, service: service)
                            isPresented = false
                        }
                        .disabled(name.isEmpty || phone.isEmpty || service.isEmpty)
                        .frame(maxWidth: .infinity)
                        .foregroundColor(.brandRed)
                    }
                }
            }
            .navigationTitle("Nova Marcação")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { isPresented = false }
                }
            }
            .onAppear { viewModel.checkAvailability(for: Date()) }
        }
    }
}
