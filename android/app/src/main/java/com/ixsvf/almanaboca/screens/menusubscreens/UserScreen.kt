package com.ixsvf.almanaboca.screens.menusubscreens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val currentUser by sessionViewModel.currentUser.collectAsState()
    val hasChatAccess by sessionViewModel.canAccessChat
    val realName by sessionViewModel.userName.collectAsState()
    val context = LocalContext.current

    // Estado para controlar o Alerta de Confirmação
    var showDeleteDialog by remember { mutableStateOf(false) }


    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "v${packageInfo.versionName}"
        } catch (e: Exception) {
            ""
        }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.checkChatAccess()
    }

    // --- ALERTA DE CONFIRMAÇÃO ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.lbl_delete_account)) },
            text = {
                Text(stringResource(R.string.lbl_delete_account_confirmation))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        sessionViewModel.deleteAccount(
                            onSuccess = {
                                Toast.makeText(context, R.string.lbl_delete_account_sucess, Toast.LENGTH_LONG).show()
                                navController.navigate("login_screen") { // Usa o nome exato da tua rota de login
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onError = { erro ->
                                Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.lbl_delete_account_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.lbl_cancel))
                }
            },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lbl_my_account), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.lbl_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- INFO DO UTILIZADOR ---
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50),
                color = Color(0xFFE0E0E0)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = (realName.firstOrNull() ?: '?').toString().uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = realName.ifBlank { (currentUser?.displayName ?: stringResource(R.string.lbl_user)) },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = currentUser?.email ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- STATUS ---
            StatusCard(isActive = hasChatAccess)

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÃO DE LOGOUT ---
            Button(
                onClick = {
                    sessionViewModel.signOut()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Vermelho escuro
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.lbl_logout), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÃO DE ELIMINAR CONTA ---
            TextButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.lbl_delete_account_permanentely), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaço final

            Text(
                text = appVersion,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ... (A função StatusCard mantém-se igual) ...
@Composable
fun StatusCard(isActive: Boolean) {
    // ... (o teu código do StatusCard que já tinhas)
    val containerColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val contentColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (isActive) Icons.Default.CheckCircle else Icons.Default.Lock
    val statusText = if (isActive) stringResource(R.string.lbl_enable) else stringResource(R.string.lbl_disable)
    val description = if (isActive) stringResource(R.string.lbl_community_access_granted) else stringResource(R.string.lbl_community_access_need)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.lbl_community_almanaboca),
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}