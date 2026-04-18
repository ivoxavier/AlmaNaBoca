package com.ixsvf.almanaboca.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- DEFINE OS COLOR SCHEMES ---

// 🎨 Paleta Dark Elegante e Premium (AlmaNaBoca Dark)
private val DarkColorScheme = darkColorScheme(
    // Cor de destaque (vamos usar o novo roxo lavanda suave)
    primary = AccentPurpleDark,
    onPrimary = Color.Black, // Texto preto sobre roxo suave (ex: FAB central)

    // Cores de fundo geral
    background = BackgroundDarkColor,
    onBackground = TextLightDark,

    // Cores de superfícies (cards, barra inferior) - Ligeira elevação visual
    surface = CardDarkColor,
    onSurface = TextLightDark,
    surfaceVariant = CardDarkColor, // Usado em alguns layouts M3
    onSurfaceVariant = TextGrayDark, // Texto secundário em cards

    // Cor de erro
    error = BrandRedMain,
    onError = Color.White,

    // Outros
    outline = TextGrayDark, // Bordas suaves
    outlineVariant = TextGrayDark.copy(alpha = 0.5f) // Bordas muito suaves
)

// ☀️ Paleta Light Centralizada e Consistente (Centralizado conforme pedido anterior)
private val LightColorScheme = lightColorScheme(
    primary = AccentPurple, // Roxo saturado original da marca
    onPrimary = Color.White,

    background = Color.White, // Fundo branco puro
    onBackground = TextDarkLight,

    surface = Color.White, // Cards brancos
    onSurface = TextDarkLight,
    surfaceVariant = BackgroundLightColor, // Fundo muito suave de cards
    onSurfaceVariant = TextGrayLight, // Texto secundário

    error = BrandRedMain,
    onError = Color.White,

    outline = TextGrayLight
)

// --- O COMPONENTE THEME ---

@Composable
fun AlmaNaBocaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desativado para ter controle total das cores da marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Barra de status da cor do fundo para imersão
            window.statusBarColor = colorScheme.background.toArgb()
            // Barra de navegação inferior da cor do fundo
            window.navigationBarColor = colorScheme.background.toArgb()
            // Configura ícones claros ou escuros nas barras
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Certifica-te de ter definido isto num Typography.kt
        content = content
    )
}