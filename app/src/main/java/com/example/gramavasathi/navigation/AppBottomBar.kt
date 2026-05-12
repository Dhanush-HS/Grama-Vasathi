package com.example.gramavasathi.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavController, appFlow: AppFlow) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route
    if (shouldHideBottomBar(route)) return

    when (appFlow) {
        AppFlow.Guest -> GuestBottomNav(navController)
        AppFlow.Host -> HostBottomNav(navController)
    }
}

private fun shouldHideBottomBar(route: String?): Boolean {
    if (route == null) return true
    return when {
        route == Screen.Splash.route -> true
        route == Screen.WelcomeAuth.route -> true
        route.startsWith("login/") -> true
        route.startsWith("stay_detail") -> true
        route.startsWith("booking/") -> true
        route.startsWith("booking_success") -> true
        else -> false
    }
}

@Composable
private fun GuestBottomNav(navController: NavController) {
    BottomNavItems(
        navController,
        listOf(Screen.Discover, Screen.Activities, Screen.Culture, Screen.Profile)
    )
}

@Composable
private fun HostBottomNav(navController: NavController) {
    BottomNavItems(
        navController,
        listOf(Screen.Host, Screen.Profile)
    ) { screen ->
        when (screen) {
            Screen.Host -> "Readiness"
            else -> screen.title ?: ""
        }
    }
}

@Composable
private fun BottomNavItems(
    navController: NavController,
    items: List<Screen>,
    labelFor: (Screen) -> String = { it.title ?: "" }
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(imageVector = it, contentDescription = screen.title)
                    }
                },
                label = { Text(labelFor(screen)) },
                selected = currentDestination?.hierarchy?.any { dest ->
                    dest.route == screen.route
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
