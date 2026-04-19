package com.ixsvf.almanaboca.screens.menusubscreens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.services.model.HomeItem
import com.ixsvf.almanaboca.ui.theme.AccentPurple
import com.ixsvf.almanaboca.ui.theme.BackgroundLight
import com.ixsvf.almanaboca.ui.theme.BrandRedMain
import com.ixsvf.almanaboca.ui.theme.TextDark
import com.ixsvf.almanaboca.ui.theme.TextGray
import com.ixsvf.almanaboca.ui.theme.WhatsAppGreen
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import com.ixsvf.almanaboca.viewmodel.HomeViewModel
import java.net.URLEncoder



private const val TARGET_WHATSAPP_NUMBER = AlmanaBocaConstants.ADMINS.MARTA_NUMBER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    programName: String,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val programItem = remember(uiState, programName) {
        (uiState as? HomeUiState.Success)?.courses?.find { it.coachProgram == programName }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.lbl_program_detail),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.lbl_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (programItem == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.lbl_program_not_found), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // --- CABEÇALHO ---
//                    Box(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .background(BrandRedMain.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Stars,
//                            contentDescription = null,
//                            tint = BrandRedMain,
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = programItem.coachProgram,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SECÇÃO: O QUE ESPERAR ---
                    DetailSectionTitle(title = stringResource(R.string.lbl_what_to_expect))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = programItem.whatToExpectProgram.ifEmpty { stringResource(R.string.lbl_no_description) },
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    // --- NOVA SECÇÃO: IDEAL PARA ---
                    if (programItem.idealFor.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DetailSectionTitle(title = stringResource(R.string.lbl_ideal_for))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = programItem.idealFor,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }

                    // --- NOVA SECÇÃO: A TUA EXPERIÊNCIA ---
                    if (programItem.youWillExperience.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DetailSectionTitle(title = stringResource(R.string.lbl_your_journey))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = programItem.youWillExperience,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }

                    // --- NOVA SECÇÃO: O RESULTADO ---
                    if (programItem.theResult.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DetailSectionTitle(title = stringResource(R.string.lbl_what_will_achieve))
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BrandRedMain.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = programItem.theResult,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- INFORMAÇÕES CHAVE (Incluindo Acesso ao Conteúdo) ---
                    InfoSummaryCard(item = programItem)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- OPÇÕES DE PAGAMENTO ---
                    if (programItem.coachPriceOption1 > 0 || programItem.coachPriceOption2 > 0) {
                        DetailSectionTitle(title = stringResource(R.string.lbl_investment_option))
                        Spacer(modifier = Modifier.height(16.dp))

                        if (programItem.coachPriceOption1 > 0) {
                            PaymentOptionCard(
                                price = programItem.coachPriceOption1,
                                description = programItem.option1WhatToExpect,
                                discount = programItem.coachDiscount
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        //Temporary NOTSHOWING THIS
                        if (programItem.coachPriceOption2 < 0) {
                            PaymentOptionCard(
                                price = programItem.coachPriceOption2,
                                description = programItem.option2WhatToExpect,
                                discount = 0.0
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- BOTÃO FIXO NO FUNDO ---
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Button(
                            onClick = {
                                try {
                                    val message = "Olá! Gostaria de me inscrever no programa: ${programItem.coachProgram}."
                                    val encodedMessage = URLEncoder.encode(message, "UTF-8")
                                    val url = "https://wa.me/$TARGET_WHATSAPP_NUMBER?text=$encodedMessage"
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(url)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, R.string.lbl_whatsapp_error, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.lbl_sign_me),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSummaryCard(item: HomeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // DATAS
            val dates = if (item.coachStartDate.isNotEmpty()) "${item.coachStartDate} - ${item.coachDateEnd}" else stringResource(R.string.lbl_dates_to_announced)
            InfoRowDetail(icon = Icons.Outlined.CalendarMonth, title = stringResource(R.string.lbl_dates), value = dates)

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

            // VAGAS
            InfoRowDetail(icon = Icons.Outlined.Groups, title = stringResource(R.string.lbl_vacancies), value = "${item.coachVacancies} " + stringResource(R.string.lbl_vacancies_available))

            // --- NOVO: FORMATO (Individual ou Grupo) ---
            if (item.coachType.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
                InfoRowDetail(
                    icon = Icons.Outlined.Groups,
                    title = stringResource(R.string.lbl_coach_type),
                    value = item.coachType
                )
            }

            // ACESSO
            if (item.contentAccess.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
                InfoRowDetail(icon = Icons.Outlined.Devices, title = stringResource(R.string.lbl_access), value = item.contentAccess)
            }
        }
    }
}



// --- COMPONENTES AUXILIARES DE DESIGN ---

@Composable
fun DetailSectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size( width = 4.dp, height = 20.dp).background(AccentPurple, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentPurple,
            letterSpacing = 1.sp
        )
    }
}



@Composable
fun InfoRowDetail(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = AccentPurple, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PaymentOptionCard(price: Double, description: String, discount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, null, tint = BrandRedMain, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = description.ifEmpty { stringResource(R.string.lbl_payment_option) }, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                if (discount > 0) {
                    Text(text = "${discount.toInt()}%" + stringResource(R.string.lbl_discount_included), fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "${price.toInt()}€", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandRedMain)
        }
    }
}