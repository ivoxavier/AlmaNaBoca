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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalOffer
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
import com.ixsvf.almanaboca.ui.theme.states.HomeUiState
import com.ixsvf.almanaboca.viewmodel.HomeViewModel
import java.net.URLEncoder

// Cores do tema (Reutilizadas da HomeScreen)
private val BrandRedMain = Color(0xFFC62828)
private val WhatsAppGreen = Color(0xFF25D366)
//private val TextGray = Color(0xFF6B7280)
//private val TextDark = Color(0xFF1F2937)
private val BackgroundLight = Color(0xFFF9F9F9)

// Número da Marta para inscrições
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

    // Tenta encontrar o programa específico na lista carregada
    val programItem = remember(uiState, programName) {
        (uiState as? HomeUiState.Success)?.courses?.find { it.coachProgram == programName }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do Programa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (programItem == null) {
            // Estado de erro ou item não encontrado
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Programa não encontrado.", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                // Conteúdo Scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // --- CABEÇALHO DO PROGRAMA ---
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(BrandRedMain.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Groups,
                            contentDescription = null,
                            tint = BrandRedMain,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = programItem.coachProgram,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SECÇÃO: O QUE ESPERAR ---
                    DetailSectionTitle(title = "O que esperar")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = programItem.whatToExpectProgram.ifEmpty { "Sem descrição disponível." },
                        fontSize = 15.sp,
                        color = TextGray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- ESPAÇO PARA NOVAS VARIÁVEIS (Ex: Vantagens, Conteúdos) ---
                    // Exemplo de como adicionar:
                    // if (programItem.novasVantagens.isNotEmpty()) {
                    //     DetailSectionTitle(title = "Vantagens")
                    //     BulletPointsList(programItem.novasVantagens)
                    //     Spacer(modifier = Modifier.height(32.dp))
                    // }

                    // --- INFORMAÇÕES CHAVE (Cartão Flutuante) ---
                    InfoSummaryCard(item = programItem)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- OPÇÕES DE PAGAMENTO (Se existirem) ---
                    if (programItem.coachPriceOption1 > 0 || programItem.coachPriceOption2 > 0) {
                        DetailSectionTitle(title = "Opções de Investimento")
                        Spacer(modifier = Modifier.height(16.dp))

                        if (programItem.coachPriceOption1 > 0) {
                            PaymentOptionCard(
                                price = programItem.coachPriceOption1,
                                description = programItem.option1WhatToExpect,
                                discount = programItem.coachDiscount
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (programItem.coachPriceOption2 > 0) {
                            PaymentOptionCard(
                                price = programItem.coachPriceOption2,
                                description = programItem.option2WhatToExpect,
                                discount = 0.0 // Assumindo desconto apenas na opção 1 (geralmente pronto pagamento)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Espaço extra no fundo para o scroll não colar no botão
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- BOTÃO FIXO NO FUNDO: INSCRIÇÃO ---
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(16.dp),
                    color = Color.White,
                    tonalElevation = 8.dp
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Button(
                            onClick = {
                                try {
                                    val message = "Olá Marta! Gostaria de me inscrever no programa: ${programItem.coachProgram}."
                                    val encodedMessage = URLEncoder.encode(message, "UTF-8")
                                    val url = "https://wa.me/$TARGET_WHATSAPP_NUMBER?text=$encodedMessage"
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(url)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "QUERO ME INSCREVER",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES DE DESIGN ---

@Composable
fun DetailSectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size( width = 4.dp, height = 20.dp).background(BrandRedMain, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = BrandRedMain,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun InfoSummaryCard(item: HomeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            val dates = if (item.coachStartDate.isNotEmpty()) "${item.coachStartDate} - ${item.coachDateEnd}" else "Datas a anunciar"
            InfoRowDetail(icon = Icons.Outlined.CalendarMonth, title = "Datas", value = dates)

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            InfoRowDetail(icon = Icons.Outlined.Groups, title = "Vagas", value = "${item.coachVacancies} vagas disponíveis")

            // --- ESPAÇO PARA NOVAS VARIÁVEIS (Ex: Duração, Formato) ---
            // HorizontalDivider(...)
            // InfoRowDetail(icon = Icons.Outlined.Timer, title = "Duração", value = item.novaDuracao)
        }
    }
}

@Composable
fun InfoRowDetail(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = BrandRedMain, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontSize = 12.sp, color = TextGray)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
    }
}

@Composable
fun PaymentOptionCard(price: Double, description: String, discount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, null, tint = BrandRedMain, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = description.ifEmpty { "Opção de pagamento" }, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
                if (discount > 0) {
                    Text(text = "${discount.toInt()}% de desconto incluído", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "${price.toInt()}€", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandRedMain)
        }
    }
}