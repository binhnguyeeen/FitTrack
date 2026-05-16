package com.binhnguyendev.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.binhnguyendev.fittrack.ui.nav.LocalRipple
import com.binhnguyendev.fittrack.ui.screens.CalendarScreen
import com.binhnguyendev.fittrack.ui.screens.HomeScreen
import com.binhnguyendev.fittrack.ui.screens.OnboardingScreen
import com.binhnguyendev.fittrack.ui.screens.PlaceholderScreen

/**
 * Single NavHost hosting every route. Milestone 3 wires Onboarding/Home/
 * Calendar; remaining routes stay placeholders until their milestones.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val ripple = LocalRipple.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { ftEnter() },
        exitTransition = { ftExit() },
        popEnterTransition = { ftPopEnter() },
        popExitTransition = { ftPopExit() },
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onComplete = { center ->
                ripple.launch(center) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onOpenProfile = { /* ProfileMenu wired in Milestone 6 */ },
                onStartWorkout = { kind, templateId, center ->
                    ripple.launch(center) {
                        navController.navigate(Routes.workout(kind, templateId))
                    }
                },
            )
        }
        composable(Routes.CALENDAR) { CalendarScreen() }
        composable(Routes.TEMPLATES) { PlaceholderScreen("Templates") }
        composable(Routes.TEMPLATES_CREATE) { PlaceholderScreen("Create template") }
        composable(Routes.TEMPLATES_ADD) { PlaceholderScreen("Add exercise") }
        composable(
            Routes.WORKOUT,
            arguments = listOf(
                navArgument("kind") { type = NavType.StringType },
                navArgument("templateId") { type = NavType.LongType },
            ),
        ) { PlaceholderScreen("Workout") }
        composable(
            Routes.SUMMARY,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { PlaceholderScreen("Summary") }
        composable(Routes.STATS) { PlaceholderScreen("Stats") }
        composable(Routes.SETTINGS) { PlaceholderScreen("Settings") }
        composable(Routes.EDIT_PROFILE) { PlaceholderScreen("Edit profile") }
    }
}
