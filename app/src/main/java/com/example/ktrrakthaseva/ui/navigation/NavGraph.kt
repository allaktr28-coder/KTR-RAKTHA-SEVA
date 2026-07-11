package com.example.ktrrakthaseva.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ktrrakthaseva.ui.screens.*
import com.example.ktrrakthaseva.ui.viewmodel.AuthState
import com.example.ktrrakthaseva.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object PostRequest : Screen("post_request")
    object FindDonors : Screen("find_donors")
    object RequestDetail : Screen("request_detail/{requestId}") {
        fun createRoute(requestId: String) = "request_detail/$requestId"
    }
    object Chat : Screen("chat/{requestId}") {
        fun createRoute(requestId: String) = "chat/$requestId"
    }
    object Profile : Screen("profile")
    object Leaderboard : Screen("leaderboard")
    object Badges : Screen("badges")
    object Alerts : Screen("alerts")
    object BloodMap : Screen("blood_map")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Analytics : Screen("analytics")
    object DigitalCard : Screen("digital_card")
    object AdminDashboard : Screen("admin_dashboard")
}

@Composable
fun RaktaSevaNavGraph(navController: NavHostController) {
    // Shared AuthViewModel scoped to the NavGraph lifecycle
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val isSessionReady by authViewModel.isSessionReady.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(animationSpec = tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) },
        popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) }
    ) {
        composable(Screen.Splash.route) {
            var animationFinished by remember { mutableStateOf(false) }

            // Navigate only when BOTH animation is done AND session check is complete
            LaunchedEffect(animationFinished, isSessionReady) {
                if (animationFinished && isSessionReady) {
                    val destination = if (authState is AuthState.Authenticated) Screen.Home.route else Screen.Onboarding.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }

            SplashScreen(onAnimationFinished = { animationFinished = true })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route)
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel, // Use shared ViewModel
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) { 
                        popUpTo(0) { inclusive = true } 
                    } 
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel, // Use shared ViewModel
                onRegisterSuccess = { 
                    navController.navigate(Screen.Home.route) { 
                        popUpTo(0) { inclusive = true } 
                    } 
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPostRequest = { navController.navigate(Screen.PostRequest.route) },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToMap = { navController.navigate(Screen.BloodMap.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToFindDonors = { navController.navigate(Screen.FindDonors.route) },
                onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) },
                onNavigateToRequestDetail = { requestId -> navController.navigate(Screen.RequestDetail.createRoute(requestId)) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel, // Use shared ViewModel
                onBack = { navController.popBackStack() },
                onLogout = { 
                    navController.navigate(Screen.Login.route) { 
                        popUpTo(0) { inclusive = true } 
                    } 
                },
                onNavigateToBadges = { navController.navigate(Screen.Badges.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToDigitalCard = { navController.navigate(Screen.DigitalCard.route) },
                onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) }
            )
        }

        composable(Screen.PostRequest.route) { PostRequestScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Leaderboard.route) { LeaderboardScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.FindDonors.route) { FindDonorsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Alerts.route) { AlertsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.BloodMap.route) { BloodMapScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.History.route) { HistoryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Badges.route) { BadgesScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Analytics.route) { AnalyticsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.DigitalCard.route) { DigitalCardScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.RequestDetail.route) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId")
            RequestDetailScreen(requestId = requestId, onBack = { navController.popBackStack() }, onNavigateToChat = { id -> navController.navigate(Screen.Chat.createRoute(id)) })
        }
        composable(Screen.Chat.route) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId")
            ChatScreen(requestId = requestId, onBack = { navController.popBackStack() })
        }
    }
}
