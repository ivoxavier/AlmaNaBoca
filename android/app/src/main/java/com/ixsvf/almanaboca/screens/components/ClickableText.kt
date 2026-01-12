package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HelpText(label: String) {
    Text(text = label, modifier = Modifier.clickable{})
}