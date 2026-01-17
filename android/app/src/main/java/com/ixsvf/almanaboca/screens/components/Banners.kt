package com.ixsvf.almanaboca.screens.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ixsvf.almanaboca.ui.theme.logos.Logos


@Composable
fun AlmanaBocaLogo(
    modifier: Modifier = Modifier
) {
    // 1. Tentar descodificar a string Base64 para um ImageBitmap.
    // Usamos 'remember' para fazer isso apenas uma vez.
    val logoBitmap = remember(Logos.ALMA_NA_BOCA_LOGO_B64) {
        try {
            // Decodifica a string Base64 para ByteArray
            // Use android.util.Base64 (importante não usar o java.util.Base64 em Android antigo)
            val decodedBytes = Base64.decode(Logos.ALMA_NA_BOCA_LOGO_B64, Base64.DEFAULT)

            // Decodifica o ByteArray para um Bitmap do Android
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            // Converte para ImageBitmap do Compose
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            // Se algo der errado (string inválida, etc.), retorna null
            e.printStackTrace()
            null
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            //.wrapContentHeight()  herda as dimensoes da imagem
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
                //.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (logoBitmap != null) {
                // 2. Se a conversão funcionou, exibe a imagem
                Image(
                    bitmap = logoBitmap,
                    contentDescription = "Logo Almanaboca",
                    modifier = Modifier
                        .fillMaxWidth() // Ajuste o tamanho conforme necessário
                        .height(167.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 3. Fallback caso a string Base64 esteja corrompida ou vazia
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "Erro no Logo",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Erro ao carregar logo",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}