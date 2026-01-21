package com.ixsvf.almanaboca.screens.menusubscreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

// --- Cores ---
private val CardWhite = Color.White
private val TextGray = Color(0xFF6B7280)
private val TextDark = Color(0xFF1F2937)
private val StatusConfirmedBg = Color(0xFFE8F5E9)
private val StatusConfirmedText = Color(0xFF2E7D32)
private val StatusPendingBg = Color(0xFFFFF3E0)
private val StatusPendingText = Color(0xFFEF6C00)
private val DeleteRed = Color(0xFFD32F2F)

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

    // --- DADOS FICTÍCIOS ---
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

                            // --- Card Único Agrupado ---
                            PaddingBox {
                                BookingGroupCard(
                                    bookings = dummyBookings,
                                    onCancelBooking = { bookingToRemove ->
                                        dummyBookings.remove(bookingToRemove)
                                    }
                                )
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

// --- COMPONENTE GROUP CARD (Card Único para a Lista) ---
@Composable
fun BookingGroupCard(
    bookings: List<BookingItem>,
    onCancelBooking: (BookingItem) -> Unit
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

                    // Adiciona divisória apenas se NÃO for o último item
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

// --- ITEM INDIVIDUAL DA LISTA (Sem Card, apenas Row com lógica de clique) ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookingRowItem(
    booking: BookingItem,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        // Clique normal (navegação ou detalhes)
                    },
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

        // Menu Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 16.dp, y = 0.dp),
            containerColor = CardWhite
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

// --- COMPONENTES AUXILIARES ---

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