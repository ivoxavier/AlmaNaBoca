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
import androidx.compose.ui.text.input.KeyboardType
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

// --- Cores ---
private val CardWhite = Color.White
private val TextGray = Color(0xFF6B7280)
private val TextDark = Color(0xFF1F2937)
private val StatusConfirmedBg = Color(0xFFE8F5E9)
private val StatusConfirmedText = Color(0xFF2E7D32)
private val StatusPendingBg = Color(0xFFFFF3E0)
private val StatusPendingText = Color(0xFFEF6C00)
private val DeleteRed = Color(0xFFD32F2F)

// --- Modelo de Dados Visual (apenas para a lista da UI) ---
data class BookingItemUI(
    val id: String,
    val serviceName: String,
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

    // Estados de UI
    var showDatePicker by remember { mutableStateOf(false) }
    var showSlotPicker by remember { mutableStateOf(false) }
    var showBookingForm by remember { mutableStateOf(false) }

    // Dados temporários para o fluxo de reserva
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedTimeSlot by remember { mutableStateOf("") }

    // Dados vindos do ViewModel
    val occupiedSlots by viewModel.occupiedSlots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookingResult by viewModel.bookingResult.collectAsState()

    // Observa o resultado da reserva para mostrar Toast
    LaunchedEffect(bookingResult) {
        bookingResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearResult() // Limpa mensagem
            if (message.contains("sucesso")) {
                showBookingForm = false // Fecha o formulário se correu bem
            }
        }
    }

    // --- LISTA FICTÍCIA (Para demo, já que ainda não tens login de utilizador) ---
    // Futuramente isto virá de: viewModel.userBookings.collectAsState()
    val myBookings = remember {
        mutableStateListOf(
            BookingItemUI("1", "Sessão de Coaching", "12 Fev 2026", "14:30", BookingStatus.CONFIRMED),
            BookingItemUI("2", "Círculo de Meditação", "05 Mar 2026", "18:00", BookingStatus.CONFIRMED)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDatePicker = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Nova Marcação")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaddingBox {
                        SummaryTopPageText(stringResource(R.string.lbl_my_bookings))
                    }

                    PaddingBox {
                        BookingGroupCard(
                            bookings = myBookings,
                            onCancelBooking = { myBookings.remove(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // --- PASSO 1: ESCOLHER DATA ---
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    selectedDateMillis = millis
                    // Pergunta ao Firebase o que está ocupado neste dia
                    viewModel.onDateSelected(millis)
                    showDatePicker = false
                    showSlotPicker = true // Avança para o passo 2
                }
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // --- PASSO 2: ESCOLHER HORÁRIO (Modal Bottom Sheet) ---
    if (showSlotPicker && selectedDateMillis != null) {
        ModalBottomSheet(onDismissRequest = { showSlotPicker = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Horários Disponíveis para ${convertMillisToDate(selectedDateMillis!!)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    TimeSlotGrid(
                        availableHours = viewModel.availableHours,
                        occupiedSlots = occupiedSlots,
                        onSlotSelected = { time ->
                            selectedTimeSlot = time
                            showSlotPicker = false
                            showBookingForm = true // Avança para o passo 3
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // --- PASSO 3: FORMULÁRIO FINAL ---
    if (showBookingForm) {
        BookingFormDialog(
            date = convertMillisToDate(selectedDateMillis!!),
            time = selectedTimeSlot,
            onDismiss = { showBookingForm = false },
            onConfirm = { name, phone, email, service ->
                viewModel.createBooking(
                    dateMillis = selectedDateMillis!!,
                    time = selectedTimeSlot,
                    name = name,
                    email = email,
                    phone = phone,
                    service = service
                )
            }
        )
    }
}

// --- COMPONENTES LÓGICOS ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotGrid(
    availableHours: List<String>,
    occupiedSlots: List<String>,
    onSlotSelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        availableHours.forEach { time ->
            val isTaken = occupiedSlots.contains(time)

            Button(
                onClick = { onSlotSelected(time) },
                enabled = !isTaken,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTaken) Color.LightGray else MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isTaken) "$time (Ocupado)" else time,
                    color = if (isTaken) Color.DarkGray else Color.White
                )
            }
        }
    }
}

@Composable
fun BookingFormDialog(
    date: String,
    time: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("Coaching Individual") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Reserva") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Data: $date às $time", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("O teu nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telemóvel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown simples para serviço (fixo por agora)
                OutlinedTextField(
                    value = service,
                    onValueChange = {},
                    label = { Text("Serviço") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone, email, service) },
                enabled = name.isNotEmpty() && phone.isNotEmpty()
            ) {
                Text("Agendar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// --- UTILS ---
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

// --- COMPONENTES VISUAIS (GroupCard, RowItem, etc - Mantidos iguais) ---

@Composable
fun BookingGroupCard(
    bookings: List<BookingItemUI>,
    onCancelBooking: (BookingItemUI) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ainda não tens marcações agendadas.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column {
                bookings.forEachIndexed { index, booking ->
                    BookingRowItem(
                        booking = booking,
                        onCancel = { onCancelBooking(booking) }
                    )
                    if (index < bookings.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookingRowItem(
    booking: BookingItemUI,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        expanded = true
                    }
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = booking.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = booking.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BookingInfoItem(Icons.Outlined.CalendarMonth, booking.date)
                Spacer(modifier = Modifier.width(24.dp))
                BookingInfoItem(Icons.Filled.Schedule, booking.time)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 16.dp, y = 0.dp),
            containerColor = CardWhite
        ) {
            DropdownMenuItem(
                text = { Text("Cancelar Marcação", color = DeleteRed, fontWeight = FontWeight.Bold) },
                onClick = {
                    expanded = false
                    onCancel()
                },
                leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = DeleteRed) }
            )
        }
    }
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
                // Impede selecionar datas passadas
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // -1 dia margem
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

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bgColor, textColor, text, icon) = when (status) {
        BookingStatus.CONFIRMED -> Tuple4(StatusConfirmedBg, StatusConfirmedText, "Confirmado", Icons.Outlined.CheckCircle)
        BookingStatus.PENDING -> Tuple4(StatusPendingBg, StatusPendingText, "Pendente", Icons.Outlined.Pending)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(50)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BookingInfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextDark)
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)