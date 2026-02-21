package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun LoginInputText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector, // Ícone da esquerda (Email, Cadeado, etc)
    isPassword: Boolean = false // Define se é campo de password
) {
    val shape = RoundedCornerShape(16.dp)
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        shape = shape,
        // --- ÍCONE DA ESQUERDA ---
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        // --- ÍCONE DA DIREITA (Olhinho para Password) ---
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar Password", tint = Color.Gray)
                }
            }
        },
        // --- ESCONDER CARACTERES SE FOR PASSWORD ---
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,

        modifier = Modifier
            .fillMaxWidth()
            //.padding(horizontal = 10.dp)
            .shadow(elevation = 4.dp, shape = shape, clip = false), // Sombra mais suave

        // --- CORES (Sem contorno, fundo branco) ---
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White,

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray,

            // Tornar a linha de baixo invisível
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}