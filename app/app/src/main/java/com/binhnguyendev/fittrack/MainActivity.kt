package com.binhnguyendev.fittrack

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.binhnguyendev.fittrack.navigation.AppNavHost
import com.binhnguyendev.fittrack.navigation.Routes
import com.binhnguyendev.fittrack.ui.LocalRepositories
import com.binhnguyendev.fittrack.ui.components.TabBar
import com.binhnguyendev.fittrack.ui.nav.LocalProfileMenu
import com.binhnguyendev.fittrack.ui.nav.LocalRipple
import com.binhnguyendev.fittrack.ui.nav.ProfileMenuController
import com.binhnguyendev.fittrack.ui.nav.RippleController
import com.binhnguyendev.fittrack.ui.nav.RippleHost
import com.binhnguyendev.fittrack.ui.screens.ProfileMenuOverlay
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FitTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val repositories = (application as FitTrackApplication).repositories
        setContent {
            FitTrackTheme {
                CompositionLocalProvider(LocalRepositories provides repositories) {
                    FitTrackRoot()
                }
            }
        }
    }
}

@Composable
private fun FitTrackRoot() {
    val repositories = LocalRepositories.current
    val ripple = remember { RippleController() }
    val profileMenu = remember { ProfileMenuController() }
    val navController = rememberNavController()

    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        startDestination =
            if (repositories.user.isOnboardingComplete()) Routes.HOME else Routes.ONBOARDING
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(FT.bg),
    ) {
        val start = startDestination ?: return@Box
        CompositionLocalProvider(
            LocalRipple provides ripple,
            LocalProfileMenu provides profileMenu,
        ) {
            AppNavHost(navController = navController, startDestination = start)

            val currentRoute by navController.currentBackStackEntryAsState()
            val route = currentRoute?.destination?.route
            if (route in Routes.TAB_BAR_ROUTES) {
                val navInset = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = 14.dp,
                            end = 14.dp,
                            bottom = navInset + 14.dp,
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    TabBar(activeRoute = route) { target ->
                        if (target != route) {
                            navController.navigate(target) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }

            // Profile menu sheet (opened from the Home avatar).
            ProfileMenuOverlay(
                visible = profileMenu.open,
                onDismiss = { profileMenu.dismiss() },
                onEditProfile = {
                    profileMenu.dismiss()
                    navController.navigate(Routes.EDIT_PROFILE)
                },
                onOpenSettings = {
                    profileMenu.dismiss()
                    navController.navigate(Routes.SETTINGS)
                },
            )

            // Ripple overlay sits above everything (system-level layer).
            RippleHost(ripple)
        }
    }
}
