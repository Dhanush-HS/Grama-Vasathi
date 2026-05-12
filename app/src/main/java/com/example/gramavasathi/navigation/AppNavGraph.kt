package com.example.gramavasathi.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gramavasathi.ui.screens.*
import com.example.gramavasathi.viewmodel.AppFlowViewModel
import com.example.gramavasathi.viewmodel.ProfileViewModel
import com.example.gramavasathi.viewmodel.SplashViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(Screen.Splash.route) {
            val splashVm = hiltViewModel<SplashViewModel>()
            SplashScreen(
                viewModel = splashVm,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WelcomeAuth.route) {
            WelcomeAuthScreen(
                onGuestLogin = {
                    navController.navigate(Screen.Login.createRoute(AppFlow.Guest))
                },
                onHostLogin = {
                    navController.navigate(Screen.Login.createRoute(AppFlow.Host))
                }
            )
        }

        composable(
            route = Screen.Login.route,
            arguments = listOf(
                navArgument("flow") {
                    type = NavType.StringType
                    defaultValue = "guest"
                }
            )
        ) { entry ->
            val flowArg = entry.arguments?.getString("flow") ?: "guest"
            val loginFlow = when (flowArg) {
                "host" -> AppFlow.Host
                else -> AppFlow.Guest
            }
            LoginScreen(
                loginFlow = loginFlow,
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    val dest = when (loginFlow) {
                        AppFlow.Host -> Screen.Host.route
                        AppFlow.Guest -> Screen.Discover.route
                    }
                    navController.navigate(dest) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Discover.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToBooking = { stayId ->
                    navController.navigate(Screen.Booking.createRoute(stayId))
                },
                onNavigateToStayDetail = { stayId ->
                    navController.navigate(Screen.StayDetail.createRoute(stayId))
                },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = hiltViewModel(),
                onNavigateToBooking = { stayId ->
                    navController.navigate(Screen.Booking.createRoute(stayId))
                },
                onNavigateToStayDetail = { stayId ->
                    navController.navigate(Screen.StayDetail.createRoute(stayId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StayDetail.route,
            arguments = listOf(
                navArgument("stayId") { type = NavType.StringType }
            )
        ) {
            StayDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onBookStay = { stayId ->
                    navController.navigate(Screen.Booking.createRoute(stayId))
                }
            )
        }
        
        composable(Screen.Host.route) {
            HostWizardScreen(viewModel = hiltViewModel())
        }
        
        composable(Screen.Activities.route) {
            ActivitiesScreen(
                viewModel = hiltViewModel(),
                onNavigateToDiscover = { activityFilter ->
                    DiscoverFilterHolder.setPendingFilter(activityFilter)
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(Screen.Discover.route) { inclusive = false }
                    }
                },
                onNavigateToCultureGuide = {
                    navController.navigate(Screen.Culture.route)
                }
            )
        }
        
        composable(
            route = Screen.Booking.route,
            arguments = listOf(
                navArgument("stayId") { type = NavType.StringType }
            )
        ) {
            BookingScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.createRoute(AppFlow.Guest))
                },
                onNavigateToSuccess = { stayName, dateRange, totalPaid, hostName ->
                    val sName = java.net.URLEncoder.encode(stayName, "UTF-8")
                    val dRange = java.net.URLEncoder.encode(dateRange, "UTF-8")
                    val tPaid = java.net.URLEncoder.encode(totalPaid, "UTF-8")
                    val hName = java.net.URLEncoder.encode(hostName, "UTF-8")
                    navController.navigate("booking_success/$sName/$dRange/$tPaid/$hName") {
                        popUpTo(Screen.Discover.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "booking_success/{stayName}/{dateRange}/{totalPaid}/{hostName}"
        ) { backStackEntry ->
            val stayName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("stayName") ?: "", "UTF-8")
            val dateRange = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("dateRange") ?: "", "UTF-8")
            val totalPaid = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("totalPaid") ?: "", "UTF-8")
            val hostName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("hostName") ?: "", "UTF-8")
            
            BookingSuccessScreen(
                stayName = stayName,
                dateRange = dateRange,
                totalPaid = totalPaid,
                hostName = hostName,
                onViewBookings = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Discover.route) { inclusive = false }
                    }
                },
                onDiscoverMore = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        
        composable(Screen.Culture.route) {
            CultureGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Profile.route) {
            val profileVm = hiltViewModel<ProfileViewModel>()
            val appFlowVm = hiltViewModel<AppFlowViewModel>()
            val appFlow by appFlowVm.appFlow.collectAsState()
            ProfileScreen(
                viewModel = profileVm,
                isGuestFlow = appFlow == AppFlow.Guest,
                onNavigateToDiscover = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(Screen.Discover.route) { inclusive = false }
                    }
                },
                onSwitchToHost = {
                    appFlowVm.switchToHost()
                    navController.navigate(Screen.Host.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSwitchToGuest = {
                    appFlowVm.switchToGuest()
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignOut = {
                    navController.navigate(Screen.WelcomeAuth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
