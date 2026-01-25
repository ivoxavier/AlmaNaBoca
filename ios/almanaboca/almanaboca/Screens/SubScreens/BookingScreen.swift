import SwiftUI

// --- Cores ---
extension Color {
    static let statusConfirmedBg = Color(red: 232/255, green: 245/255, blue: 233/255) // #E8F5E9
    static let statusConfirmedText = Color(red: 46/255, green: 125/255, blue: 50/255) // #2E7D32
    static let statusPendingBg = Color(red: 255/255, green: 243/255, blue: 224/255)   // #FFF3E0
    static let statusPendingText = Color(red: 239/255, green: 108/255, blue: 0/255)   // #EF6C00
    static let deleteRed = Color(red: 211/255, green: 47/255, blue: 47/255)           // #D32F2F
}

struct BookingScreen: View {
    @StateObject private var viewModel = BookingViewModel()
    
    // Estado para controlar o DatePicker (Sheet)
    @State private var showDatePicker = false
    @State private var selectedDate = Date()
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) { // ZStack para o FAB ficar em cima
            
            // Fundo
            Color.backgroundGray.ignoresSafeArea()
            
            // Conteúdo Principal
            VStack {
                switch viewModel.uiState {
                case .loading:
                    Spacer()
                    ProgressView()
                    Spacer()
                    
                case .error(let message):
                    Spacer()
                    Text("Erro: \(message)")
                        .foregroundColor(.red)
                    Spacer()
                    
                case .success:
                    ScrollView {
                        VStack(spacing: 24) {
                            // Título
                            VStack(alignment: .leading) {
                                Text("As Minhas Marcações") // R.string.lbl_my_bookings
                                    .font(.largeTitle)
                                    .fontWeight(.bold)
                                    .padding(.top, 16)
                                    .padding(.horizontal)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            
                            // Card Único Agrupado
                            BookingGroupCard(
                                bookings: viewModel.bookings,
                                onCancelBooking: { item in
                                    withAnimation {
                                        viewModel.cancelBooking(item)
                                    }
                                }
                            )
                            .padding(.horizontal)
                            
                            Spacer().frame(height: 100) // Espaço para o FAB não tapar o fim da lista
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            
            // --- FAB (Floating Action Button) ---
            Button(action: {
                showDatePicker = true
            }) {
                Image(systemName: "plus")
                    .font(.title.weight(.semibold))
                    .foregroundColor(.white)
                    .frame(width: 56, height: 56)
                    .background(Color.blue) // MaterialTheme.primary
                    .clipShape(Circle())
                    .shadow(radius: 4, x: 0, y: 4)
            }
            .padding() // Margem do canto do ecrã
        }
        // DatePicker Modal (Sheet)
        .sheet(isPresented: $showDatePicker) {
            AddBookingSheet(
                date: $selectedDate,
                onConfirm: {
                    viewModel.addNewBooking(date: selectedDate)
                    showDatePicker = false
                }
            )
            // Detalhe iOS 16+: Permite que a sheet ocupe apenas metade do ecrã
            .presentationDetents([.medium])
        }
    }
}

// MARK: - Componentes

struct BookingGroupCard: View {
    let bookings: [BookingItem]
    let onCancelBooking: (BookingItem) -> Void
    
    var body: some View {
        VStack(spacing: 0) {
            if bookings.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "calendar.badge.exclamationmark")
                        .font(.largeTitle)
                        .foregroundColor(.textGray)
                    Text("Ainda não tens marcações agendadas.")
                        .foregroundColor(.textGray)
                        .font(.body)
                }
                .padding(32)
                .frame(maxWidth: .infinity)
            } else {
                ForEach(Array(bookings.enumerated()), id: \.element.id) { index, booking in
                    
                    BookingRowItem(booking: booking, onCancel: {
                        onCancelBooking(booking)
                    })
                    
                    // Divisória (apenas se não for o último)
                    if index < bookings.count - 1 {
                        Divider()
                            .padding(.leading, 16)
                    }
                }
            }
        }
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

struct BookingRowItem: View {
    let booking: BookingItem
    let onCancel: () -> Void
    
    var body: some View {
        VStack(spacing: 8) {
            // Linha 1: Título e Status
            HStack(alignment: .top) {
                Text(booking.serviceName)
                    .font(.headline) // titleMedium bold
                    .foregroundColor(.textDark)
                    .fixedSize(horizontal: false, vertical: true)
                
                Spacer()
                
                StatusBadge(status: booking.status)
            }
            
            Spacer().frame(height: 4)
            
            // Linha 2: Data e Hora
            HStack {
                BookingInfoItem(iconName: "calendar", text: booking.date)
                Spacer().frame(width: 24)
                BookingInfoItem(iconName: "clock", text: booking.time) // Schedule icon
                Spacer()
            }
        }
        .padding(16)
        .contentShape(Rectangle()) // Garante que toda a área é clicável
        // Context Menu (Equivalente ao onLongClick do Android)
        .contextMenu {
            Button(role: .destructive, action: onCancel) {
                Label("Cancelar Marcação", systemImage: "trash")
            }
        }
    }
}

struct StatusBadge: View {
    let status: BookingStatus
    
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: iconName)
                .font(.system(size: 10, weight: .bold))
            
            Text(statusText)
                .font(.caption)
                .fontWeight(.bold)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .foregroundColor(textColor)
        .background(bgColor)
        .clipShape(Capsule())
    }
    
    var iconName: String {
        switch status {
        case .confirmed: return "checkmark.circle"
        case .pending: return "hourglass"
        }
    }
    
    var statusText: String {
        switch status {
        case .confirmed: return "Confirmado"
        case .pending: return "Pendente"
        }
    }
    
    var bgColor: Color {
        switch status {
        case .confirmed: return .statusConfirmedBg
        case .pending: return .statusPendingBg
        }
    }
    
    var textColor: Color {
        switch status {
        case .confirmed: return .statusConfirmedText
        case .pending: return .statusPendingText
        }
    }
}

struct BookingInfoItem: View {
    let iconName: String
    let text: String
    
    var body: some View {
        HStack(spacing: 6) {
            Image(systemName: iconName)
                .foregroundColor(.textGray)
                .font(.system(size: 14))
            Text(text)
                .font(.subheadline) // bodyMedium
                .foregroundColor(.textDark)
        }
    }
}

// --- Sheet para Adicionar Data (Substitui o DatePickerModal) ---
struct AddBookingSheet: View {
    @Binding var date: Date
    let onConfirm: () -> Void
    @Environment(\.dismiss) var dismiss // Para fechar a sheet
    
    var body: some View {
        NavigationView {
            VStack {
                DatePicker(
                    "Selecione uma data",
                    selection: $date,
                    in: Date()..., // Apenas datas futuras
                    displayedComponents: [.date]
                )
                .datePickerStyle(.graphical)
                .padding()
                
                Spacer()
                
                Button(action: onConfirm) {
                    Text("Verificar Disponibilidade")
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding()
            }
            .navigationTitle("Nova Marcação")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
            }
        }
    }
}

struct BookingScreen_Previews: PreviewProvider {
    static var previews: some View {
        BookingScreen()
    }
}
