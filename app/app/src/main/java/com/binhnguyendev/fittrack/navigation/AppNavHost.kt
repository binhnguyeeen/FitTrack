package com.binhnguyendev.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.binhnguyendev.fittrack.ui.nav.LocalRipple
import com.binhnguyendev.fittrack.ui.screens.AddExerciseScreen
import com.binhnguyendev.fittrack.ui.screens.CalendarScreen
import com.binhnguyendev.fittrack.ui.screens.CreateTemplateScreen
import com.binhnguyendev.fittrack.ui.screens.HomeScreen
import com.binhnguyendev.fittrack.ui.screens.OnboardingScreen
import com.binhnguyendev.fittrack.ui.screens.PlaceholderScreen
import com.binhnguyendev.fittrack.ui.screens.TemplatesScreen
import com.binhnguyendev.fittrack.ui.vm.CreateTemplateViewModel
import com.binhnguyendev.fittrack.ui.vm.ftScopedViewModel

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
        composable(Routes.TEMPLATES) {
            TemplatesScreen(onCreate = { navController.navigate(Routes.TEMPLATES_FLOW) })
        }

        // CreateTemplate ⇄ AddExercise share one VM scoped to this graph entry.
        navigation(
            route = Routes.TEMPLATES_FLOW,
            startDestination = Routes.TEMPLATES_CREATE,
        ) {
            composable(Routes.TEMPLATES_CREATE) { entry ->
                val parent = remember(entry) {
                    navController.getBackStackEntry(Routes.TEMPLATES_FLOW)
                }
                val vm = ftScopedViewModel(parent) { repos -> CreateTemplateViewModel(repos) }
                CreateTemplateScreen(
                    vm = vm,
                    onBack = { navController.popBackStack(Routes.TEMPLATES, false) },
                    onAddExercise = { navController.navigate(Routes.TEMPLATES_ADD) },
                    onSaved = { navController.popBackStack(Routes.TEMPLATES, false) },
                )
            }
            composable(Routes.TEMPLATES_ADD) { entry ->
                val parent = remember(entry) {
                    navController.getBackStackEntry(Routes.TEMPLATES_FLOW)
                }
                val vm = ftScopedViewModel(parent) { repos -> CreateTemplateViewModel(repos) }
                AddExerciseScreen(
                    createVm = vm,
                    onBack = { navController.popBackStack() },
                )
            }
        }

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
