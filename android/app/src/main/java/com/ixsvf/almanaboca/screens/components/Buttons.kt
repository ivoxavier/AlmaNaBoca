package com.ixsvf.almanaboca.screens.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LoginButton(label: String, onClick: () -> Unit, enabled: Boolean){
    Button(onClick = { onClick()}, enabled = enabled) {
        Text(text=label)
    }
}