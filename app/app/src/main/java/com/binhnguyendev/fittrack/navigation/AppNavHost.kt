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
import com.binhnguyendev.fittrack.ui.screens.LogWorkoutScreen
import com.binhnguyendev.fittrack.ui.screens.OnboardingScreen
import com.binhnguyendev.fittrack.ui.screens.PlaceholderScreen
import com.binhnguyendev.fittrack.ui.screens.SummaryScreen
import com.binhnguyendev.fittrack.ui.screens.TemplatesScreen
import com.binhnguyendev.fittrack.ui.vm.CreateTemplateViewModel
import com.binhnguyendev.fittrack.ui.vm.SummaryViewModel
import com.binhnguyendev.fittrack.ui.vm.WorkoutViewModel
import com.binhnguyendev.fittrack.ui.vm.ftScopedViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

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
        ) { entry ->
            val kind = entry.arguments?.getString("kind") ?: "routine"
            val templateId = entry.arguments?.getLong("templateId") ?: -1L
            val vm = ftViewModel(key = "workout/$kind/$templateId") { repos ->
                WorkoutViewModel(repos, kind, templateId)
            }
            LogWorkoutScreen(
                vm = vm,
                onFinished = { sessionId, center ->
                    ripple.launch(center) {
                        navController.navigate(Routes.summary(sessionId)) {
                            popUpTo(Routes.WORKOUT) { inclusive = true }
                        }
                    }
                },
            )
        }
        composable(
            Routes.SUMMARY,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { entry ->
            val sessionId = entry.arguments?.getLong("sessionId") ?: -1L
            val vm = ftViewModel(key = "summary/$sessionId") { repos ->
                SummaryViewModel(repos, sessionId)
            }
            SummaryScreen(
                vm = vm,
                onDone = { center ->
                    ripple.launch(center) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
            )
        }
        composable(Routes.STATS) { PlaceholderScreen("Stats") }
        composable(Routes.SETTINGS) { PlaceholderScreen("Settings") }
        composable(Routes.EDIT_PROFILE) { PlaceholderScreen("Edit profile") }
    }
}
