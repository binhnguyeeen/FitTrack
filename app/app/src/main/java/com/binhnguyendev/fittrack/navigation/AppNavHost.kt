package com.binhnguyendev.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.binhnguyendev.fittrack.ui.screens.PlaceholderScreen

/**
 * Single NavHost hosting every route. Milestone 2 wires the skeleton with
 * placeholders + the prototype's transition system; later milestones swap in
 * the real screens.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { ftEnter() },
        exitTransition = { ftExit() },
        popEnterTransition = { ftPopEnter() },
        popExitTransition = { ftPopExit() },
    ) {
        composable(Routes.ONBOARDING) { PlaceholderScreen("Onboarding") }
        composable(Routes.HOME) { PlaceholderScreen("Home") }
        composable(Routes.CALENDAR) { PlaceholderScreen("Calendar") }
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
