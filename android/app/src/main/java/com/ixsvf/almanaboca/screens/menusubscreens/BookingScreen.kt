package com.ixsvf.almanaboca.screens.menusubscreens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Cores
private val CardWhite = Color.White
private val TextGray = Color(0xFF6B7280)
private val TextDark = Color(0xFF1F2937)
private val StatusConfirmedBg = Color(0xFFE8F5E9)
private val StatusConfirmedText = Color(0xFF2E7D32)
private val StatusPendingBg = Color(0xFFFFF3E0)
private val StatusPendingText = Color(0xFFEF6C00)
private val DeleteRed = Color(0xFFD32F2F)

data class BookingItemUI(
    val id: String,
    val serviceName: String,
    val clientName: String = "", // Importante para o Admin saber quem é
    val date: String,
    val time: String,
    val status: BookingStatus
)

enum class BookingStatus { CONFIRMED, PENDING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados
    var showDatePicker by remember { mutableStateOf(false) }
    var showSlotPicker by remember { mutableStateOf(false) }
    var showBookingForm by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedTimeSlot by remember { mutableStateOf("") }

    // Dados do ViewModel
    val bookings by viewModel.bookingsList.collectAsState()
    val occupiedSlots by viewModel.occupiedSlots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookingResult by viewModel.bookingResult.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState() // Para saber se mostra opções extra

    LaunchedEffect(bookingResult) {
        bookingResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearResult()
            if (message.contains("enviado")) showBookingForm = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            // Admin não precisa de marcar para si próprio normalmente,
            // mas pode querer marcar para alguém. Mantemos o botão.
            FloatingActionButton(
                onClick = { showDatePicker = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "Nova Marcação")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaddingBox {
                        // Título muda se for Admin
                        val title = if (isAdmin) "Gestão de Agenda (Admin)" else stringResource(R.string.lbl_my_bookings)
                        SummaryTopPageText(title)
                    }

                    if (isAdmin) {
                        PaddingBox {
                            Text(
                                "Pressiona (Long Press) num cartão para Aprovar ou Rejeitar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }

                    PaddingBox {
                        if (bookings.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Sem marcações.", color = TextGray)
                            }
                        } else {
                            // Lista de Cartões Individuais (Melhor para gestão)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                bookings.forEach { booking ->
                                    BookingAdminCard(
                                        booking = booking,
                                        isAdmin = isAdmin,
                                        onApprove = { viewModel.approveBooking(booking.id) },
                                        onDelete = { viewModel.deleteBooking(booking.id) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // --- MODAIS (DatePicker, Slots, Form) MANTÊM-SE IGUAIS ---
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = {
                selectedDateMillis = it
                if(it != null) { viewModel.onDateSelected(it); showDatePicker = false; showSlotPicker = true }
            },
            onDismiss = { showDatePicker = false }
        )
    }
    if (showSlotPicker) {
        ModalBottomSheet(onDismissRequest = { showSlotPicker = false }) {
            Column(Modifier.padding(16.dp)) {
                Text("Horários Disponíveis", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                TimeSlotGrid(viewModel.availableHours, occupiedSlots) {
                    selectedTimeSlot = it; showSlotPicker = false; showBookingForm = true
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
    if (showBookingForm) {
        BookingFormDialog(
            convertMillisToDate(selectedDateMillis!!), selectedTimeSlot,
            { showBookingForm = false },
            { n, p, e, s -> viewModel.createBooking(selectedDateMillis!!, selectedTimeSlot, n, p, s) }
        )
    }
}

// --- CARTÃO INTELIGENTE (COM LONG PRESS) ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookingAdminCard(
    booking: BookingItemUI,
    isAdmin: Boolean,
    onApprove: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    // Cor de fundo muda se for pendente (Laranja claro)
    val bgColor = if (booking.status == BookingStatus.PENDING) Color(0xFFFFF3E0) else CardWhite

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { /* Clique normal: Detalhes se quiser */ },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            expanded = true
                        }
                    )
                    .padding(16.dp)
            ) {
                // Linha 1: Serviço e Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(booking.serviceName, fontWeight = FontWeight.Bold, color = TextDark)
                        // Se for Admin, mostra o nome do cliente
                        if (isAdmin && booking.clientName.isNotEmpty()) {
                            Text("Cliente: ${booking.clientName}", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(booking.status)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Linha 2: Data e Hora
                Row {
                    BookingInfoItem(Icons.Outlined.CalendarMonth, booking.date)
                    Spacer(modifier = Modifier.width(16.dp))
                    BookingInfoItem(Icons.Filled.Schedule, booking.time)
                }
            }

            // --- MENU DE AÇÃO (LONG PRESS) ---
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 16.dp, y = 0.dp),
                containerColor = CardWhite
            ) {
                // OPÇÕES PARA ADMIN
                if (isAdmin) {
                    if (booking.status == BookingStatus.PENDING) {
                        DropdownMenuItem(
                            text = { Text("Confirmar Reserva", color = StatusConfirmedText, fontWeight = FontWeight.Bold) },
                            onClick = { expanded = false; onApprove() },
                            leadingIcon = { Icon(Icons.Filled.CheckCircle, null, tint = StatusConfirmedText) }
                        )
                        Divider()
                    }
                    DropdownMenuItem(
                        text = { Text("Rejeitar / Apagar", color = DeleteRed) },
                        onClick = { expanded = false; onDelete() },
                        leadingIcon = { Icon(Icons.Filled.Delete, null, tint = DeleteRed) }
                    )
                }
                // OPÇÕES PARA CLIENTE (Só pode cancelar)
                else {
                    DropdownMenuItem(
                        text = { Text("Cancelar Pedido", color = DeleteRed) },
                        onClick = { expanded = false; onDelete() },
                        leadingIcon = { Icon(Icons.Filled.Close, null, tint = DeleteRed) }
                    )
                }
            }
        }
    }
}

// ... (Resto dos componentes: TimeSlotGrid, BookingFormDialog, StatusBadge, Utils mantêm-se iguais ao anterior)
@Composable
fun TimeSlotGrid(hours: List<String>, occupied: List<String>, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        hours.forEach { time ->
            val taken = occupied.contains(time)
            Button(onClick = { onSelect(time) }, enabled = !taken,
                colors = ButtonDefaults.buttonColors(containerColor = if(taken) Color.LightGray else MaterialTheme.colorScheme.primary)) {
                Text(time, color = if(taken) Color.DarkGray else Color.White)
            }
        }
    }
}

@Composable
fun BookingFormDialog(date: String, time: String, onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserva") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$date às $time")
            OutlinedTextField(name, { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text("Telemóvel") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(service, { service = it }, label = { Text("Serviço") }, modifier = Modifier.fillMaxWidth())
        }},
        confirmButton = { Button(onClick = { onConfirm(name, phone, "", service) }, enabled = name.isNotEmpty()) { Text("Enviar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bg, color, txt, icon) = if (status == BookingStatus.CONFIRMED)
        Tuple4(StatusConfirmedBg, StatusConfirmedText, "Confirmado", Icons.Outlined.CheckCircle)
    else
        Tuple4(StatusPendingBg, StatusPendingText, "Pendente", Icons.Outlined.Pending)

    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(txt, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BookingInfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextGray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextDark)
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Impede selecionar datas passadas (hoje - 1 dia de margem)
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) { Text("Verificar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}





data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)