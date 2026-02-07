package com.ixsvf.almanaboca.screens.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.ui.theme.logos.Logos

// 1. Função auxiliar para carregar o Bitmap (evita duplicar código)
@Composable
fun rememberLogoBitmap(): ImageBitmap? {
    return remember(Logos.ALMA_NA_BOCA_LOGO_B64) {
        try {
            val decodedBytes = Base64.decode(Logos.ALMA_NA_BOCA_LOGO_B64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


@Composable
fun rememberMartaBannerBitmap(): ImageBitmap?
{
    return remember(Logos.MARTA_B64) {
        try {
            val decodedBytes = Base64.decode(Logos.MARTA_B64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun AlmanaBocaLogo(
    modifier: Modifier = Modifier,
    // Novos parâmetros para personalização
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    elevation: Dp = 4.dp,
    height: Dp = 167.dp
) {
    val logoBitmap = rememberLogoBitmap()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (logoBitmap != null) {
                Image(
                    bitmap = logoBitmap,
                    contentDescription = "Logo Almanaboca",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height), // Altura dinâmica
                    contentScale = ContentScale.Crop
                )
            } else {
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

@Composable
fun MartaBanner(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    elevation: Dp = 4.dp,
    // MUDANÇA 1: Definir a cor padrão como Transparent para não pintar o fundo
    containerColor: Color = Color.Transparent
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = containerColor, // O Surface agora é transparente
        shadowElevation = elevation
    ) {
        Image(
            painter = painterResource(id = R.drawable.marta),
            contentDescription = "Foto da Marta",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}