package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.BottomBarScreen

@Composable
fun BottomNavigationBar(navController: NavController) {

    val screens = listOf(
        BottomBarScreen.Meditations, // Item da esquerda
        null,                        // Item do MEIO (null cria o espaço para o botão Home)
        BottomBarScreen.Booking      // Item da direita
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. A Barra de Navegação (Fundo)
        NavigationBar(
            modifier = Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            screens.forEach { screen ->
                if (screen == null) {
                    // 2. O Espaço Vazio
                    // Cria um item invisível e desativado para empurrar os outros
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { },
                        enabled = false,
                        label = { Text("") }
                    )
                } else {
                    // 3. Os Itens Reais (Esquerda e Direita)
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        label = {
                            Text(
                                text = stringResource(id = screen.title),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = stringResource(id = screen.title),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // 4. O Botão Flutuante (HOME)
        FloatingActionButton(
            onClick = {
                // AÇÃO DE NAVEGAÇÃO PARA A HOME
                navController.navigate(BottomBarScreen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp) // Sobe o botão
                .size(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}