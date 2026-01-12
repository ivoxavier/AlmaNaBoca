package com.ixsvf.almanaboca.screens.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Meditations,
        BottomBarScreen.Consultations,
        BottomBarScreen.Shop
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        NavigationBar(
            modifier = Modifier
                .height(65.dp)
                .align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {

            screens.forEachIndexed { index, screen ->

                if (index == screens.size / 2) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* Não faz nada */ },
                        icon = {},
                        enabled = false // Desativa cliques
                    )
                }

                NavigationBarItem(
                    label = { Text(text = stringResource(id = screen.title), textAlign = TextAlign.Center, fontSize = 10.sp) },
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = stringResource(id = screen.title)
                        )
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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

        FloatingActionButton(
            onClick = { /* TODO: Ação do perfil */ },
            shape = CircleShape,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Perfil")
        }
    }
}