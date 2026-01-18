package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextComponent(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean
) {
    val shape = RoundedCornerShape(16.dp)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            // ADICIONADO: Padding horizontal para não colar nos cantos
            .padding(horizontal = 24.dp)
            // A sombra vem DEPOIS do padding para contornar o botão e não a tela toda
            .shadow(elevation = 8.dp, shape = shape, clip = false),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White,

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            // Cor do Label (Opcional: ajusta para cinzento ou cor da marca)
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray,

            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
        )
    )
}