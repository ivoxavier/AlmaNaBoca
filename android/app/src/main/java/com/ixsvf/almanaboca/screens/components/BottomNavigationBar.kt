package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
private val BrandRedLight = Color(0xFFFFE5E5)
private val IconGray = Color(0xFF9CA3AF)

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
        // 1. BARRA DE FUNDO
        NavigationBar(
            modifier = Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter) // Alinhada ao fundo
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            containerColor = Color.White,
            tonalElevation = 0.dp
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
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = screen.title),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 4.dp)
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
                            indicatorColor = BrandRedLight,
                            unselectedIconColor = IconGray,
                            unselectedTextColor = IconGray
                        )
                    )
                }
            }
        }

        // 2. BOTÃO FLUTUANTE (Posicionado "Mais Dentro")
        FloatingActionButton(
            onClick = {
                navController.navigate(BottomBarScreen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            shape = CircleShape,
            //containerColor = BrandRed,
            containerColor = Color.White,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter) // IMPORTANTE: Alinha pelo fundo também
                .offset(y = (-18).dp) // "Empurra" para cima apenas 25dp (menos que antes)
                .size(72.dp)
                // A borda branca cria o efeito de recorte na barra
                .border(BorderStroke(5.dp, Color.White), CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.almanaboca_white),
                contentDescription = "Home",
                modifier = Modifier.size(76.dp),
                tint = Color.Unspecified
            )
        }
    }
}