package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
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
private val DeleteRed = Color(0xFFD32F2F) // Cor para o botão cancelar

// --- Modelo de Dados ---
data class BookingItem(
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
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    // --- DADOS FICTÍCIOS (Tornamos a lista mutável para simular a remoção na UI) ---
    // Nota: Numa app real, a remoção deve ser feita no ViewModel e reagir ao State
    val dummyBookings = remember {
        mutableStateListOf(
            BookingItem("1", "Sessão de Coaching Executivo", "12 Fev 2026", "14:30", BookingStatus.CONFIRMED),
            BookingItem("2", "Mentoria de Carreira", "28 Fev 2026", "10:00", BookingStatus.PENDING),
            BookingItem("3", "Círculo de Meditação", "05 Mar 2026", "18:00", BookingStatus.CONFIRMED)
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
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Adicionar Nova Marcação"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Erro: ${state.message}", color = Color.Red)
                    }
                }
                is HomeUiState.Success -> {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {

                        Column(
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PaddingBox {
                                SummaryTopPageText(stringResource(R.string.lbl_my_bookings))
                            }

                            if (dummyBookings.isEmpty()) {
                                PaddingBox {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "Ainda não tens marcações agendadas.",
                                            modifier = Modifier.padding(16.dp),
                                            color = TextGray
                                        )
                                    }
                                }
                            } else {
                                dummyBookings.forEach { booking ->
                                    PaddingBox {
                                        BookingCard(
                                            booking = booking,
                                            onCancel = {
                                                // Lógica de cancelamento
                                                // Numa app real: viewModel.cancelBooking(booking.id)
                                                dummyBookings.remove(booking)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    selectedDateMillis = millis
                    println("Data selecionada: $millis")
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
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
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Verificar Disponibilidade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// --- COMPONENTE CARD DE MARCAÇÃO COM LONG PRESS ---
@OptIn(ExperimentalFoundationApi::class) // Necessário para combinedClickable
@Composable
fun BookingCard(
    booking: BookingItem,
    onCancel: () -> Unit // Callback para quando o user clica em cancelar
) {
    // Estado para controlar se o menu está visível
    var expanded by remember { mutableStateOf(false) }

    // Para vibração tátil
    val haptics = LocalHapticFeedback.current

    Box {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                // MODIFICADOR MÁGICO PARA LONG PRESS
                .combinedClickable(
                    onClick = {
                        // Clique normal (pode abrir detalhes se quiseres)
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress) // Vibra
                        expanded = true // Abre o menu
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BookingInfoItem(
                        icon = Icons.Outlined.CalendarMonth,
                        text = booking.date
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    BookingInfoItem(
                        icon = Icons.Filled.Schedule,
                        text = booking.time
                    )
                }
            }
        }

        // MENU POPUP (Aparece em cima do card ou próximo do toque)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 16.dp, y = 0.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text("Cancelar Marcação", color = DeleteRed, fontWeight = FontWeight.Bold)
                },
                onClick = {
                    expanded = false
                    onCancel()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = DeleteRed
                    )
                }
            )
        }
    }
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bgColor, textColor, text, icon) = when (status) {
        BookingStatus.CONFIRMED -> Tuple4(StatusConfirmedBg, StatusConfirmedText, "Confirmado", Icons.Outlined.CheckCircle)
        BookingStatus.PENDING -> Tuple4(StatusPendingBg, StatusPendingText, "Pendente", Icons.Outlined.Pending)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BookingInfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark
        )
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)