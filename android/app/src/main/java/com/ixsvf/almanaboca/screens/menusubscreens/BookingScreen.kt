package com.ixsvf.almanaboca.screens.menusubscreens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.PaddingBox
import com.ixsvf.almanaboca.screens.components.SummaryTopPageText
import com.ixsvf.almanaboca.viewmodel.BookingViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current

    val bookings by viewModel.bookingsList.collectAsState()
    val occupiedSlots by viewModel.occupiedSlots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookingResult by viewModel.bookingResult.collectAsState()
    val isAdmin = viewModel.isAdmin

    // Estados locais
    var showDatePicker by remember { mutableStateOf(false) }
    var showSlotPicker by remember { mutableStateOf(false) }
    var showBookingForm by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTimeSlot by remember { mutableStateOf("") }

    // --- CARREGAMENTO INICIAL ---
    LaunchedEffect(key1 = true) {
        if (isAdmin) {
            viewModel.loadAllPendingBookings()
        } else {
            viewModel.loadUserBookings()
        }
    }

    // Observar resultados
    LaunchedEffect(bookingResult) {
        bookingResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (message.contains("enviado", ignoreCase = true) ||
                message.contains("sucesso", ignoreCase = true)) {
                showBookingForm = false
                showSlotPicker = false
            }
            viewModel.clearResult()
        }
    }

    // --- DATE PICKER ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= (System.currentTimeMillis() - 86400000)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDateMillis = millis
                            if (isAdmin) {
                                viewModel.loadBookingsForDate(millis)
                            } else {
                                viewModel.checkAvailabilityForDate(millis)
                                showSlotPicker = true
                            }
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        floatingActionButton = {
            if (!isAdmin) {
                FloatingActionButton(
                    onClick = { showDatePicker = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) { Icon(Icons.Default.Add, contentDescription = "Nova Marcação") }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PaddingBox {
                SummaryTopPageText(if (isAdmin) stringResource(R.string.lbl_bookings_management) else stringResource(R.string.lbl_my_bookings))
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (bookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isAdmin) "Sem pedidos pendentes." else "Ainda não tens marcações.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Mais espaço entre cards
                ) {
                    items(bookings) { booking ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(16.dp), // Cantos mais redondos
                            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                // 1. CABEÇALHO (DATA E ESTADO)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "${booking.date} · ${booking.time}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

                                    // Badge de Estado
                                    Surface(
                                        color = if(booking.status == "pending") Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(50),
                                    ) {
                                        Text(
                                            text = if(booking.status == "pending") "Pendente" else "Confirmado",
                                            color = if(booking.status == "pending") Color(0xFFEF6C00) else Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Título do Serviço
                                Text(
                                    text = booking.serviceType,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium
                                )

                                // 2. CAIXA DE INFORMAÇÃO DO CLIENTE (ADMIN ONLY)
                                if (isAdmin) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Bloco Cinzento para destacar dados do cliente
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text("Dados do Cliente", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        ClientInfoRow(icon = Icons.Default.Person, text = booking.clientName, isBold = true)
                                        ClientInfoRow(icon = Icons.Default.Email, text = booking.clientEmail)
                                        ClientInfoRow(icon = Icons.Default.Phone, text = booking.clientPhone)
                                    }
                                }

                                // 3. BOTÕES DE AÇÃO (SÓ PENDENTES E ADMIN)
                                if (isAdmin && booking.status == "pending") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(
                                            onClick = { /* Aprovar */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Verde Material
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(vertical = 12.dp)
                                        ) { Text("Aprovar") }

                                        Button(
                                            onClick = { /* Rejeitar */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)), // Vermelho Material
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(vertical = 12.dp)
                                        ) { Text("Rejeitar") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ... (Mantém o código do BottomSheet e do Dialog igual)
    // Vou apenas copiar aqui para garantir que o ficheiro fica completo e funcional se copiar tudo.

    if (showSlotPicker) {
        ModalBottomSheet(
            onDismissRequest = { showSlotPicker = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Horários Disponíveis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
                val slots = listOf("09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(slots) { slot ->
                        val isOccupied = occupiedSlots.contains(slot)
                        val backgroundColor = if (isOccupied) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.primaryContainer
                        val textColor = if (isOccupied) Color.Gray else MaterialTheme.colorScheme.onPrimaryContainer
                        val border = if (isOccupied) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .border(border ?: BorderStroke(0.dp, Color.Transparent), RoundedCornerShape(12.dp))
                                .clickable(enabled = !isOccupied) { selectedTimeSlot = slot; showSlotPicker = false; showBookingForm = true }
                                .padding(vertical = 16.dp)
                        ) { Text(slot, color = textColor, fontWeight = FontWeight.Bold) }
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (showBookingForm) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var service by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showBookingForm = false }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Finalizar Marcação", style = MaterialTheme.typography.titleLarge)
                    Text("$selectedTimeSlot", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telemóvel") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = service, onValueChange = { service = it }, label = { Text("Serviço") })
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.addBooking(selectedDateMillis, selectedTimeSlot, name, phone, service) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) { if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White) else Text("Confirmar") }
                }
            }
        }
    }
}

// Pequeno componente auxiliar para as linhas de informação do cliente
@Composable
fun ClientInfoRow(icon: ImageVector, text: String, isBold: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.DarkGray
        )
    }
}