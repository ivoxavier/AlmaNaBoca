package com.ixsvf.almanaboca.screens.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OutlinedTextComponent(value:String, onValueChange:(String)->Unit, label:String, singleLine:Boolean){
    OutlinedTextField(value=value,
        onValueChange = onValueChange,label={
            Text(text = label)
        }, singleLine = singleLine)
}