package com.ixsvf.almanaboca.screens.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TopPageText(label: String)
{
    Text(text = label,fontSize = 28.sp, fontWeight = FontWeight.Bold)

}

@Composable
fun SummaryTopPageText(label: String)
{
    Text(text = label,fontSize = 18.sp, fontWeight = FontWeight.Bold)
}