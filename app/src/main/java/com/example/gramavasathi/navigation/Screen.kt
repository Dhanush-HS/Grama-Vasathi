package com.example.gramavasathi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Splash : Screen("splash")

    /** Guest vs host picker before email/password. */
    object WelcomeAuth : Screen("welcome_auth")

    /** Firebase email auth; path segment sets app flow before session starts. */
    object Login : Screen("login/{flow}") {
        fun createRoute(flow: AppFlow): String = when (flow) {
            AppFlow.Guest -> "login/guest"
            AppFlow.Host -> "login/host"
        }
    }
    object Discover : Screen("discover", "Discover", Icons.Filled.Home)
    object Host : Screen("host", "Host", Icons.Filled.Star)
    object Activities : Screen("activities", "Farm", Icons.Filled.List)
    object Culture : Screen("culture", "Guide", Icons.Filled.Info)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)

    /** Full-text style search over the Firebase stays directory (activity & location filters). */
    object Search : Screen("search", title = "Search", icon = Icons.Filled.Search)
    
    /** Full stay detail: gallery, amenities, experiences, host readiness. */
    object StayDetail : Screen("stay_detail/{stayId}") {
        fun createRoute(stayId: String) = "stay_detail/$stayId"
    }

    object Booking : Screen("booking/{stayId}") {
        fun createRoute(stayId: String) = "booking/$stayId"
    }
    
    object BookingSuccess : Screen("booking_success")
}
