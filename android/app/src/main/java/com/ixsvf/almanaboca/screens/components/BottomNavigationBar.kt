package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.BottomBarScreen

// Cores da Marca
private val BrandRed = Color(0xFF9E1919)
private val IconGray = Color(0xFF9CA3AF)

private val BottomBarBackground = Color(0xFFF5F5F5)

@Composable
fun BottomNavigationBar(navController: NavController) {

    val screens = listOf(
        BottomBarScreen.Meditations,
        null, // Espaço vazio para o botão central
        BottomBarScreen.Booking
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. BARRA DE FUNDO (Mantém-se igual)
        NavigationBar(
            modifier = Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter),
            containerColor = BottomBarBackground,
            tonalElevation = 10.dp
        ) {
            screens.forEach { screen ->
                if (screen == null) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = {},
                        enabled = false,
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                } else {
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(id = screen.title),
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = screen.title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandRed,
                            selectedTextColor = BrandRed,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = IconGray,
                            unselectedTextColor = IconGray
                        )
                    )
                }
            }
        }

        // 2. BOTÃO FLUTUANTE (HOME) COM O LOGO
        FloatingActionButton(
            onClick = {
                navController.navigate(BottomBarScreen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            shape = CircleShape,
            containerColor = BrandRed, // Fundo vermelho
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(65.dp)
        ) {
            // --- AQUI ESTÁ A MUDANÇA ---
            // Usamos 'Icon' com painterResource e tint Unspecified para manter as cores do JPG
            Icon(
                // SUBSTITUA 'R.drawable.almanaboca_logo' PELO NOME DO SEU FICHEIRO
                painter = painterResource(id = R.drawable.almanaboca),
                contentDescription = "Home",
                modifier = Modifier.size(70.dp), // Ajuste o tamanho do logo aqui
                tint = Color.Unspecified // IMPORTANTE: Isto faz com que o logo não fique branco!
            )
        }
    }
}